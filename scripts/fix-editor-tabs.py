from pathlib import Path
p=Path("client/src/pages/Editor.tsx")
t=p.read_text().replace('tab === "Scenes"','tab === "Cenas"').replace('tab === "Captions"','tab === "Legendas"').replace('tab === "Audio"','tab === "Áudio"')
p.write_text(t)
