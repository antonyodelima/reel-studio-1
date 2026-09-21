from pathlib import Path
for name in ["client/src/pages/Home.tsx", "client/src/App.tsx"]:
    p=Path(name); t=p.read_text()
    for a,b in {
        '>Workspace<':'>Espaço de trabalho<',
        'placeholder="Search"':'placeholder="Buscar"',
        '>Why teams ship faster<':'>Por que equipes entregam mais rápido<',
        '>Local workspace<':'>Espaço local<',
        '"Local workspace"':'"Espaço local"',
    }.items(): t=t.replace(a,b)
    p.write_text(t)
