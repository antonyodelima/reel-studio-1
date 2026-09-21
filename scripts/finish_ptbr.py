from pathlib import Path

app = Path("client/src/App.tsx")
t = app.read_text()
for a,b in {
    '"Workspace"': '"Espaço de trabalho"',
    '"Resources"': '"Recursos"',
    'item.label === "Projects"': 'item.label === "Projetos"',
    '"Close menu"': '"Fechar menu"',
    '"Open navigation"': '"Abrir navegação"',
    '"Your studio"': '"Seu estúdio"',
    '"Search projects, scenes, templates…"': '"Buscar projetos, cenas e templates…"',
    '>Open projects<': '>Abrir projetos<',
    '>Browse templates<': '>Explorar templates<',
    '>Open settings<': '>Abrir configurações<',
    '"No new notifications"': '"Nenhuma notificação nova"',
    '"Local workspace"': '"Espaço local"',
    '"You are already working locally"': '"Você já está trabalhando localmente"',
}.items(): t=t.replace(a,b)
app.write_text(t)

settings = Path("client/src/pages/Settings.tsx")
t = settings.read_text()
for a,b in {
    'useState("General")':'useState("Geral")',
    'section === "General"':'section === "Geral"',
    'section === "Rendering"':'section === "Renderização"',
    'section === "Captions"':'section === "Legendas"',
    'section === "Integrations"':'section === "Integrações"',
    '"Enabled"':'"Ativada"',
    '"Connectors are configured in your project settings"':'"As integrações são configuradas nas configurações do projeto"',
}.items(): t=t.replace(a,b)
settings.write_text(t)

editor = Path("client/src/pages/Editor.tsx")
t = editor.read_text()
for a,b in {
    '>Draft<': '>Rascunho<',
    '>Save<': '>Salvar<',
    '>Produce<': '>Produzir<',
    '>scenes<': '>cenas<',
    '"Scenes are already autosaved"':'"As cenas já são salvas automaticamente"',
    '"Format"':'"Formato"',
    '"Quality"':'"Qualidade"',
    '"Required"':'"Obrigatório"',
    '"Optional"':'"Opcional"',
    '"Caption track"':'"Faixa de legendas"',
    '"Make every word land."':'"Faça cada palavra chegar."',
    '"Timing from scene timeline · auto-wrapped to 4 words per line."':'"Tempo da linha do tempo · quebra automática em 4 palavras por linha."',
    '"Give the story a pulse."':'"Dê ritmo à história."',
    '"Share link is coming soon"':'"O link de compartilhamento estará disponível em breve"',
    '>Share<': '>Compartilhar<',
    '"Download prepared"':'"Download preparado"',
    '"Latest render ready"':'"Último render pronto"',
    '"Saved just now"':'"Salvo agora"',
}.items(): t=t.replace(a,b)
editor.write_text(t)

reel = Path("client/src/lib/reel-data.ts")
t = reel.read_text()
for a,b in {
    '{ label: "Projects", href: "/", icon: "grid" }':'{ label: "Projetos", href: "/", icon: "grid" }',
    '{ label: "Templates", href: "/templates", icon: "sparkles" }':'{ label: "Templates", href: "/templates", icon: "sparkles" }',
    '{ label: "Voices", href: "/voices", icon: "mic" }':'{ label: "Vozes", href: "/voices", icon: "mic" }',
    '{ label: "Settings", href: "/settings", icon: "settings" }':'{ label: "Configurações", href: "/settings", icon: "settings" }',
    'export const settingsSections = ["General", "Rendering", "Captions", "Integrations"];':'export const settingsSections = ["Geral", "Renderização", "Legendas", "Integrações"];',
    'export const languageOptions = ["English (US)", "Português (BR)", "Español (ES)"];':'export const languageOptions = ["Português (BR)", "English (US)", "Español (ES)"];',
}.items(): t=t.replace(a,b)
reel.write_text(t)
