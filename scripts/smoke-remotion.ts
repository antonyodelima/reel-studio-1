import { mkdtemp, rm, stat } from "node:fs/promises";
import { tmpdir } from "node:os";
import { renderWithRemotion } from "../server/render";

const folder = await mkdtemp(`${tmpdir()}/reel-remotion-smoke-`);
try {
  const output = await renderWithRemotion(folder, { id: 1, userId: 1, name: "Smoke", type: "Reel", preset: "Teste", format: "1:1 Square", engine: "remotion", status: "draft", sourceText: null, sourceUrl: null, createdAt: new Date(), updatedAt: new Date() }, [
    { id: 1, projectId: 1, position: 0, label: "Gancho", title: "Teste Remotion", caption: "Teste Remotion", durationMs: 1000, mediaUrl: null, mediaKey: null, locked: 0, createdAt: new Date(), updatedAt: new Date() },
  ]);
  const file = await stat(output);
  if (file.size < 1000) throw new Error("MP4 muito pequeno");
  console.log(`Remotion smoke ok: ${file.size} bytes`);
} finally {
  await rm(folder, { recursive: true, force: true });
}
