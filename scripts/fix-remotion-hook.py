from pathlib import Path
p=Path("server/render.ts")
t=p.read_text().replace("Composition, AbsoluteFill, Sequence, registerRoot", "Composition, AbsoluteFill, Sequence, useCurrentFrame, registerRoot")
p.write_text(t)
