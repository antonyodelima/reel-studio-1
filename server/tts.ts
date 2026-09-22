const CARTESIA_BASE_URL = "https://api.cartesia.ai";
const CARTESIA_VERSION = "2026-08-14";

export type CartesiaVoice = {
  id: string;
  name: string;
  language: string;
  description: string;
  gender: string | null;
  previewFileUrl: string | null;
  accents: Array<{ locale?: string; language?: string; country?: string; is_native?: boolean }>;
};

export function getTtsProviderConfig() {
  const apiKey = process.env.CARTESIA_API_KEY?.trim();
  if (!apiKey) throw new Error("CARTESIA_API_KEY não está configurada");
  return { apiKey, baseUrl: CARTESIA_BASE_URL, version: CARTESIA_VERSION };
}

export function normalizeTtsText(value: string) {
  return value.replace(/\s+/g, " ").trim().slice(0, 5000);
}

function headers(apiKey: string) {
  return {
    Authorization: `Bearer ${apiKey}`,
    "Cartesia-Version": CARTESIA_VERSION,
    "Content-Type": "application/json",
  };
}

async function cartesiaError(response: Response) {
  const body = await response.text().catch(() => "");
  try {
    const parsed = JSON.parse(body) as { message?: string; title?: string };
    return parsed.message ?? parsed.title ?? `Cartesia respondeu ${response.status}`;
  } catch {
    return body || `Cartesia respondeu ${response.status}`;
  }
}

export async function listCartesiaVoices(options: { language?: string; limit?: number } = {}) {
  const { apiKey, baseUrl } = getTtsProviderConfig();
  const url = new URL("/voices", baseUrl);
  url.searchParams.set("limit", String(Math.min(100, Math.max(1, options.limit ?? 100))));
  url.searchParams.set("expand[]", "preview_file_url");
  if (options.language) url.searchParams.set("language", options.language);
  const response = await fetch(url, { headers: { Authorization: `Bearer ${apiKey}`, "Cartesia-Version": CARTESIA_VERSION } });
  if (!response.ok) throw new Error(`Falha ao listar vozes Cartesia: ${await cartesiaError(response)}`);
  const payload = await response.json() as { data?: any[]; voices?: any[] } | any[];
  const rows = Array.isArray(payload) ? payload : payload.data ?? payload.voices ?? [];
  return rows.map((voice) => ({
    id: String(voice.id),
    name: String(voice.name ?? voice.id),
    language: String(voice.language ?? voice.accents?.find((accent: any) => accent.is_native)?.locale ?? voice.accents?.[0]?.locale ?? "pt-BR"),
    description: String(voice.description ?? voice.tagline ?? "Voz Cartesia"),
    gender: voice.gender ? String(voice.gender) : null,
    previewFileUrl: voice.preview_file_url ? String(voice.preview_file_url) : null,
    accents: Array.isArray(voice.accents) ? voice.accents : [],
  })) as CartesiaVoice[];
}

export async function generateCartesiaWav(input: { transcript: string; voiceId: string; language?: string }) {
  const { apiKey, baseUrl } = getTtsProviderConfig();
  const transcript = normalizeTtsText(input.transcript);
  if (!transcript) throw new Error("A cena precisa ter texto para gerar a narração");
  const response = await fetch(`${baseUrl}/tts/bytes`, {
    method: "POST",
    headers: headers(apiKey),
    body: JSON.stringify({
      model_id: "sonic-3.6",
      transcript,
      voice: input.voiceId,
      language: input.language ?? "pt-BR",
      output_format: { container: "wav", encoding: "pcm_f32le", sample_rate: 44100 },
    }),
  });
  if (!response.ok) throw new Error(`Falha ao gerar narração Cartesia: ${await cartesiaError(response)}`);
  return Buffer.from(await response.arrayBuffer());
}

export async function generateCartesiaPreview(input: { transcript: string; voiceId: string; language?: string }) {
  const audio = await generateCartesiaWav(input);
  return `data:audio/wav;base64,${audio.toString("base64")}`;
}

const supportedCloneMimeTypes = new Set(["audio/flac", "audio/mpeg", "audio/mp3", "audio/ogg", "audio/oga", "audio/wav", "audio/x-wav", "audio/webm"]);

export function normalizeCloneLanguage(language: string) {
  return language.trim().toLowerCase().split(/[-_]/)[0] || "pt";
}

export function validateCloneClip(input: { buffer: Buffer; mimeType: string; fileName: string }) {
  if (input.buffer.length > 16 * 1024 * 1024) throw new Error("O áudio precisa ter no máximo 16 MB");
  const mimeType = input.mimeType.toLowerCase().split(";")[0];
  const extension = input.fileName.toLowerCase().split(".").pop() ?? "";
  const supportedExtensions = new Set(["flac", "mp3", "mpeg", "mpga", "oga", "ogg", "wav", "webm"]);
  if (!supportedCloneMimeTypes.has(mimeType) && !supportedExtensions.has(extension)) throw new Error("Formato não suportado. Use WAV, MP3, OGG, FLAC ou WEBM");
}

export async function cloneCartesiaVoice(input: { buffer: Buffer; fileName: string; mimeType: string; name: string; language: string; tagline?: string; description?: string }) {
  const { apiKey, baseUrl } = getTtsProviderConfig();
  validateCloneClip(input);
  const form = new FormData();
  form.append("clip", new Blob([new Uint8Array(input.buffer)], { type: input.mimeType }), input.fileName);
  form.append("name", input.name.trim().slice(0, 160));
  form.append("language", normalizeCloneLanguage(input.language));
  if (input.tagline?.trim()) form.append("tagline", input.tagline.trim().slice(0, 32));
  if (input.description?.trim()) form.append("description", input.description.trim().slice(0, 2000));
  const response = await fetch(`${baseUrl}/voices/clone`, { method: "POST", headers: { Authorization: `Bearer ${apiKey}`, "Cartesia-Version": CARTESIA_VERSION }, body: form });
  if (!response.ok) throw new Error(`Falha ao clonar voz na Cartesia: ${await cartesiaError(response)}`);
  const voice = await response.json() as { id?: string; name?: string; language?: string; description?: string; tagline?: string };
  if (!voice.id) throw new Error("A Cartesia não retornou o identificador da voz clonada");
  return { id: voice.id, name: voice.name ?? input.name, language: voice.language ?? normalizeCloneLanguage(input.language), description: voice.description ?? input.description ?? "", tagline: voice.tagline ?? input.tagline ?? "" };
}

export async function deleteCartesiaVoice(voiceId: string) {
  const { apiKey, baseUrl } = getTtsProviderConfig();
  const response = await fetch(`${baseUrl}/voices/${encodeURIComponent(voiceId)}`, { method: "DELETE", headers: { Authorization: `Bearer ${apiKey}`, "Cartesia-Version": CARTESIA_VERSION } });
  if (!response.ok && response.status !== 404) throw new Error(`Não foi possível excluir a voz na Cartesia (${response.status})`);
}
