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
