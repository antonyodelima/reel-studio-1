from pathlib import Path
p = Path("server/render.ts")
t = p.read_text().replace("Composition, AbsoluteFill, Sequence, useCurrentFrame, interpolate", "Composition, AbsoluteFill, Sequence, registerRoot")
p.write_text(t)
