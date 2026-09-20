import { describe, expect, it } from "vitest";
import { colorClasses, formatDuration, makeProject, safeParse, slugify, statusLabel, statusTone } from "../client/src/lib/reel-data";

describe("reel data helpers", () => {
  it("formats project durations consistently", () => {
    expect(formatDuration(0)).toBe("00:00");
    expect(formatDuration(68)).toBe("01:08");
    expect(formatDuration(305)).toBe("05:05");
  });

  it("creates a usable draft project from a name", () => {
    const project = makeProject("Spring launch / Aurora", "Product Launch");
    expect(project.name).toBe("Spring launch / Aurora");
    expect(project.preset).toBe("Product Launch");
    expect(project.status).toBe("draft");
    expect(project.id).toContain("spring-launch-aurora");
  });

  it("provides semantic labels and tones", () => {
    expect(statusLabel("ready")).toBe("Ready");
    expect(statusTone("rendering")).toContain("amber");
    expect(colorClasses("coral")).toContain("coral");
  });

  it("slugifies names and safely parses local data", () => {
    expect(slugify("  Hello, Reel Studio!  ")).toBe("hello-reel-studio");
    expect(safeParse('{"value":42}', { value: 0 })).toEqual({ value: 42 });
    expect(safeParse("not-json", { value: 7 })).toEqual({ value: 7 });
    expect(safeParse(null, ["fallback"])).toEqual(["fallback"]);
  });
});
