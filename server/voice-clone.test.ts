import { afterEach, describe, expect, it, vi } from "vitest";
import { cloneCartesiaVoice, validateCloneClip } from "./tts";

afterEach(() => vi.unstubAllGlobals());

describe("Cartesia voice cloning", () => {
  it("rejects oversized or unsupported clips before calling Cartesia", () => {
    expect(() => validateCloneClip({ buffer: Buffer.alloc(16 * 1024 * 1024 + 1), mimeType: "audio/wav", fileName: "voice.wav" })).toThrow("16 MB");
    expect(() => validateCloneClip({ buffer: Buffer.from("audio"), mimeType: "application/pdf", fileName: "voice.pdf" })).toThrow("Formato não suportado");
  });

  it("sends the clip as multipart form data with private-clone metadata", async () => {
    const fetchMock = vi.fn().mockResolvedValue({ ok: true, status: 200, json: async () => ({ id: "voice_custom_123", name: "Voz da marca", language: "pt" }) });
    vi.stubGlobal("fetch", fetchMock);
    const result = await cloneCartesiaVoice({ buffer: Buffer.from("RIFF fake wav"), fileName: "marca.wav", mimeType: "audio/wav", name: "Voz da marca", language: "pt-BR", tagline: "Quente e clara" });
    expect(result.id).toBe("voice_custom_123");
    expect(fetchMock).toHaveBeenCalledWith("https://api.cartesia.ai/voices/clone", expect.objectContaining({ method: "POST" }));
    const body = fetchMock.mock.calls[0][1].body as FormData;
    expect(body.get("name")).toBe("Voz da marca");
    expect(body.get("language")).toBe("pt");
    expect(body.get("tagline")).toBe("Quente e clara");
    expect(body.get("clip")).toBeTruthy();
  });
});
