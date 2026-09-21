import { describe, expect, it } from "vitest";
import { getTtsProviderConfig, listCartesiaVoices, normalizeTtsText } from "./tts";

describe("Cartesia TTS", () => {
  it("requires a server-side Cartesia API key", () => {
    const previous = process.env.CARTESIA_API_KEY;
    delete process.env.CARTESIA_API_KEY;
    expect(() => getTtsProviderConfig()).toThrow("CARTESIA_API_KEY");
    if (previous) process.env.CARTESIA_API_KEY = previous;
  });

  it("normalizes scene text without changing its meaning", () => {
    expect(normalizeTtsText("  Olá\n\n  Reel Studio  ")).toBe("Olá Reel Studio");
  });

  it("authenticates the configured key against Cartesia voices", async () => {
    const voices = await listCartesiaVoices({ language: "pt-BR", limit: 1 });
    expect(Array.isArray(voices)).toBe(true);
  }, 20_000);
});
