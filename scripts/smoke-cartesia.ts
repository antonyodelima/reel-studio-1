import { listCartesiaVoices, generateCartesiaWav } from "../server/tts";

const voices = await listCartesiaVoices({ language: "pt-BR", limit: 1 });
if (!voices.length) throw new Error("Nenhuma voz Cartesia pt-BR disponível para o smoke test");
const voice = voices[0];
const audio = await generateCartesiaWav({ voiceId: voice.id, language: voice.language || "pt-BR", transcript: "Olá, esta é uma narração real do Reel Studio." });
if (audio.length < 1000 || audio.subarray(0, 4).toString() !== "RIFF") throw new Error("Cartesia não retornou um WAV válido");
console.log(`Cartesia smoke ok: ${voice.name} · ${audio.length} bytes`);
