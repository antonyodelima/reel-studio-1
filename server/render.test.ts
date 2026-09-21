import { describe, expect, it } from "vitest";
import { buildSrt, buildVtt } from "./render";

describe("render subtitle exports", () => {
  const scenes = [
    { caption: "Primeira cena", durationMs: 2500 },
    { caption: "Segunda cena", durationMs: 4000 },
  ] as any;

  it("creates valid SRT cue timing", () => {
    const srt = buildSrt(scenes);
    expect(srt).toContain("00:00:00,000 --> 00:00:02,500");
    expect(srt).toContain("00:00:02,500 --> 00:00:06,500");
    expect(srt).toContain("Primeira cena");
  });

  it("creates a WebVTT document", () => {
    const vtt = buildVtt(scenes);
    expect(vtt.startsWith("WEBVTT")).toBe(true);
    expect(vtt).toContain("00:00:00.000 --> 00:00:02.500");
    expect(vtt).toContain("Segunda cena");
  });
});
