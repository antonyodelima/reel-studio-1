from pathlib import Path
p = Path("client/src/pages/Editor.tsx")
t = p.read_text()
t = t.replace('  const saveSceneMutation = trpc.projects.updateScene.useMutation();\n  const renderMutation', '  const saveSceneMutation = trpc.projects.updateScene.useMutation();\n  const updateProjectMutation = trpc.projects.update.useMutation();\n  const renderMutation')
old = '  const save = () => { const projects = safeParse<Project[]>(localStorage.getItem(storageKeys.projects), starterProjects); localStorage.setItem(storageKeys.projects, JSON.stringify(projects.map((item) => item.id === project.id ? { ...item, name: projectName, scenes: scenes.length, updated: "Just now" } : item))); toast.success("Projeto salvo"); };'
new = '  const save = () => { const scene = scenes[active]; if (Number.isInteger(numericId) && numericId > 0) { updateProjectMutation.mutate({ id: numericId, name: projectName, format }, { onSuccess: () => { if (typeof scene?.id === "number") saveSceneMutation.mutate({ projectId: numericId, sceneId: scene.id, title: scene.title, caption: scene.caption, durationMs: Math.max(1000, Math.round(parseFloat(scene.duration.replace(":", ".")) * 1000)) }); toast.success("Projeto salvo"); }, onError: (error) => toast.error(error.message) }); return; } const projects = safeParse<Project[]>(localStorage.getItem(storageKeys.projects), starterProjects); localStorage.setItem(storageKeys.projects, JSON.stringify(projects.map((item) => item.id === project.id ? { ...item, name: projectName, scenes: scenes.length, updated: "Agora" } : item))); toast.success("Projeto salvo"); };'
if old not in t:
    raise SystemExit('save function not found')
t = t.replace(old, new)
p.write_text(t)
