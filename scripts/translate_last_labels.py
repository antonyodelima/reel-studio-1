from pathlib import Path
app=Path("client/src/App.tsx")
t=app.read_text().replace('>Workspace<','>Espaço de trabalho<').replace('>Resources<','>Recursos<').replace('>Your studio<','>Seu estúdio<')
app.write_text(t)
reel=Path("client/src/lib/reel-data.ts")
t=reel.read_text()
for a,b in {
'export const editorTabs = ["Scenes", "Captions", "Audio"];':'export const editorTabs = ["Cenas", "Legendas", "Áudio"];',
'export const renderFormats = ["MP4 video", "SRT captions", "VTT captions", "WAV voiceover"];':'export const renderFormats = ["Vídeo MP4", "Legendas SRT", "Legendas VTT", "Narração WAV"];',
'export const productionStages = ["Plan", "Write", "Design", "Produce"];':'export const productionStages = ["Planejar", "Escrever", "Projetar", "Produzir"];',
'export const resetSuccess = "Demo data restored";':'export const resetSuccess = "Dados de demonstração restaurados";',
}.items(): t=t.replace(a,b)
reel.write_text(t)
settings=Path("client/src/pages/Settings.tsx")
t=settings.read_text().replace('>Enabled<','>Ativada<').replace('>Configure<','>Configurar<').replace('"Support link copied"','"Link de suporte copiado"')
settings.write_text(t)
editor=Path("client/src/pages/Editor.tsx")
t=editor.read_text().replace('>Format<','>Formato<').replace('>Quality<','>Qualidade<').replace('>Required<','>Obrigatório<').replace('>Optional<','>Opcional<')
editor.write_text(t)
