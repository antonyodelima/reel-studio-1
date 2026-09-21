import { z } from "zod";
import { COOKIE_NAME } from "@shared/const";
import { getSessionCookieOptions } from "./_core/cookies";
import { invokeLLM } from "./_core/llm";
import { systemRouter } from "./_core/systemRouter";
import { protectedProcedure, publicProcedure, router } from "./_core/trpc";
import { createMediaRecord, createProjectRecord, createRenderJob, createSceneRecords, getProjectById, getRenderJob, getScenes, listProjects, updateProjectRecord, updateRenderJob, updateSceneRecord } from "./db";
import { renderProject } from "./render";
import { storagePut } from "./storage";
import { generateCartesiaWav, listCartesiaVoices } from "./tts";

const sceneSchema = z.object({ label: z.string().min(1).max(80), title: z.string().min(1).max(400), caption: z.string().min(1).max(400), durationMs: z.number().int().min(1000).max(60000).optional() });
const projectId = z.number().int().positive();

function userId(ctx: { user: { id: number } }) { return ctx.user.id; }
function extractText(html: string) { return html.replace(/<script[\s\S]*?<\/script>/gi, " ").replace(/<style[\s\S]*?<\/style>/gi, " ").replace(/<[^>]+>/g, " ").replace(/\s+/g, " ").trim().slice(0, 30000); }
function messageText(response: any) { const content = response?.choices?.[0]?.message?.content; return typeof content === "string" ? content : Array.isArray(content) ? content.map((part: any) => part.text ?? "").join(" ") : ""; }

async function generateSceneDraft(sourceText: string) {
  const response = await invokeLLM({
    messages: [
      { role: "system", content: "Você é um roteirista de vídeos curtos. Responda apenas com JSON válido no formato {\"scenes\":[{\"label\":string,\"title\":string,\"caption\":string,\"durationMs\":number}]}. Crie entre 4 e 8 cenas com gancho, contexto, prova, desenvolvimento e CTA. Escreva em português do Brasil." },
      { role: "user", content: `Transforme este briefing em cenas para um vídeo vertical de 30 a 45 segundos:\n\n${sourceText.slice(0, 12000)}` },
    ],
    response_format: { type: "json_schema", json_schema: { name: "scene_draft", strict: true, schema: { type: "object", properties: { scenes: { type: "array", minItems: 4, maxItems: 8, items: { type: "object", properties: { label: { type: "string" }, title: { type: "string" }, caption: { type: "string" }, durationMs: { type: "integer" } }, required: ["label", "title", "caption", "durationMs"], additionalProperties: false } } }, required: ["scenes"], additionalProperties: false } } },
  });
  const parsed = JSON.parse(messageText(response));
  return z.object({ scenes: z.array(sceneSchema).min(1) }).parse(parsed).scenes;
}

export const appRouter = router({
  system: systemRouter,
  auth: router({
    me: publicProcedure.query(opts => opts.ctx.user),
    logout: publicProcedure.mutation(({ ctx }) => { const cookieOptions = getSessionCookieOptions(ctx.req); ctx.res.clearCookie(COOKIE_NAME, { ...cookieOptions, maxAge: -1 }); return { success: true } as const; }),
  }),
  projects: router({
    list: protectedProcedure.query(({ ctx }) => listProjects(userId(ctx))),
    get: protectedProcedure.input(z.object({ id: projectId })).query(async ({ ctx, input }) => { const project = await getProjectById(userId(ctx), input.id); if (!project) throw new Error("Projeto não encontrado"); return { project, scenes: await getScenes(project.id) }; }),
    create: protectedProcedure.input(z.object({ name: z.string().min(2).max(180), preset: z.string().optional(), format: z.string().optional(), type: z.string().optional(), brief: z.string().max(30000).optional() })).mutation(async ({ ctx, input }) => { const project = await createProjectRecord(userId(ctx), { name: input.name, preset: input.preset, format: input.format, type: input.type, sourceText: input.brief }); if (!project) throw new Error("Não foi possível criar o projeto"); const draft = input.brief ? await generateSceneDraft(input.brief).catch(() => [{ label: "Gancho", title: input.brief!.slice(0, 120), caption: input.brief!.slice(0, 120), durationMs: 6000 }]) : [{ label: "Gancho", title: "Comece a sua história.", caption: "Comece a sua história.", durationMs: 5000 }]; const scenes = await createSceneRecords(project.id, draft.map((scene, index) => ({ ...scene, position: index }))); return { project, scenes }; }),
    update: protectedProcedure.input(z.object({ id: projectId, name: z.string().min(2).max(180).optional(), format: z.string().optional(), engine: z.enum(["ffmpeg", "remotion", "hyperframes"]).optional(), status: z.enum(["draft", "rendering", "ready", "failed"]).optional() })).mutation(async ({ ctx, input }) => { const { id, ...patch } = input; const project = await getProjectById(userId(ctx), id); if (!project) throw new Error("Projeto não encontrado"); return updateProjectRecord(userId(ctx), id, patch); }),
    importSource: protectedProcedure.input(z.object({ projectId: projectId, url: z.string().url().max(1000) })).mutation(async ({ ctx, input }) => { const project = await getProjectById(userId(ctx), input.projectId); if (!project) throw new Error("Projeto não encontrado"); const response = await fetch(input.url, { headers: { "User-Agent": "ReelStudio/1.0" }, signal: AbortSignal.timeout(12000) }); if (!response.ok) throw new Error(`Não foi possível importar esta página (${response.status})`); const text = extractText(await response.text()); if (text.length < 40) throw new Error("A página não trouxe texto suficiente"); const draft = await generateSceneDraft(text); const scenes = await createSceneRecords(project.id, draft.map((scene, index) => ({ ...scene, position: index }))); return { sourceText: text, scenes }; }),
    generateScenes: protectedProcedure.input(z.object({ projectId, brief: z.string().min(20).max(30000) })).mutation(async ({ ctx, input }) => { const project = await getProjectById(userId(ctx), input.projectId); if (!project) throw new Error("Projeto não encontrado"); const draft = await generateSceneDraft(input.brief); const scenes = await createSceneRecords(project.id, draft.map((scene, index) => ({ ...scene, position: index }))); return scenes; }),
    updateScene: protectedProcedure.input(z.object({ projectId, sceneId: z.number().int().positive(), title: z.string().min(1).max(400).optional(), caption: z.string().min(1).max(400).optional(), durationMs: z.number().int().min(1000).max(60000).optional(), locked: z.number().int().min(0).max(1).optional(), position: z.number().int().min(0).optional() })).mutation(async ({ ctx, input }) => { const project = await getProjectById(userId(ctx), input.projectId); if (!project) throw new Error("Projeto não encontrado"); const { projectId: _projectId, sceneId, ...patch } = input; return updateSceneRecord(project.id, sceneId, patch); }),
    listVoices: protectedProcedure.input(z.object({ language: z.string().max(20).optional() }).optional()).query(({ input }) => listCartesiaVoices({ language: input?.language, limit: 100 })),
    previewVoice: protectedProcedure.input(z.object({ voiceId: z.string().min(1).max(160), text: z.string().min(1).max(600), language: z.string().min(2).max(20).default("pt-BR") })).mutation(async ({ input }) => { const audio = await generateCartesiaWav({ transcript: input.text, voiceId: input.voiceId, language: input.language }); return { audioDataUrl: `data:audio/wav;base64,${audio.toString("base64")}` }; }),
    generateVoice: protectedProcedure.input(z.object({ projectId, sceneId: z.number().int().positive(), voiceId: z.string().min(1).max(160), voiceName: z.string().min(1).max(160), language: z.string().min(2).max(20).default("pt-BR"), text: z.string().min(1).max(5000) })).mutation(async ({ ctx, input }) => {
      const project = await getProjectById(userId(ctx), input.projectId);
      if (!project) throw new Error("Projeto não encontrado");
      const scenes = await getScenes(project.id);
      const scene = scenes.find((item) => item.id === input.sceneId);
      if (!scene) throw new Error("Cena não encontrada");
      await updateSceneRecord(project.id, scene.id, { voiceProvider: "cartesia", voiceId: input.voiceId, voiceName: input.voiceName, voiceLanguage: input.language, voiceStatus: "generating", voiceError: null });
      try {
        const audio = await generateCartesiaWav({ transcript: input.text, voiceId: input.voiceId, language: input.language });
        const uploaded = await storagePut(`users/${userId(ctx)}/projects/${project.id}/scenes/${scene.id}/voice.wav`, audio, "audio/wav");
        return updateSceneRecord(project.id, scene.id, { voiceProvider: "cartesia", voiceId: input.voiceId, voiceName: input.voiceName, voiceLanguage: input.language, voiceAudioKey: uploaded.key, voiceAudioUrl: uploaded.url, voiceStatus: "ready", voiceError: null });
      } catch (error) {
        await updateSceneRecord(project.id, scene.id, { voiceStatus: "failed", voiceError: error instanceof Error ? error.message : "Falha ao gerar a narração" });
        throw error;
      }
    }),
    uploadAsset: protectedProcedure.input(z.object({ projectId: projectId.optional(), fileName: z.string().min(1).max(255), mimeType: z.string().min(1).max(120), dataUrl: z.string().min(20).max(25_000_000) })).mutation(async ({ ctx, input }) => { const match = input.dataUrl.match(/^data:[^;]+;base64,(.+)$/); if (!match) throw new Error("Arquivo inválido"); const buffer = Buffer.from(match[1], "base64"); const uploaded = await storagePut(`users/${userId(ctx)}/uploads/${input.fileName}`, buffer, input.mimeType); return createMediaRecord({ userId: userId(ctx), projectId: input.projectId, fileName: input.fileName, mimeType: input.mimeType, storageKey: uploaded.key, storageUrl: uploaded.url }); }),
  }),
  renders: router({
    start: protectedProcedure.input(z.object({ projectId, engine: z.enum(["ffmpeg", "remotion", "hyperframes"]).default("ffmpeg") })).mutation(async ({ ctx, input }) => { const project = await getProjectById(userId(ctx), input.projectId); if (!project) throw new Error("Projeto não encontrado"); const scenes = await getScenes(project.id); const job = await createRenderJob({ userId: userId(ctx), projectId: project.id, engine: input.engine, status: "rendering", progress: 10 }); if (!job) throw new Error("Não foi possível criar o job de renderização"); try { const result = await renderProject({ ...project, engine: input.engine }, scenes, userId(ctx), input.engine); return updateRenderJob(job.id, { status: "ready", progress: 100, mp4Url: result.mp4Url, srtUrl: result.srtUrl, vttUrl: result.vttUrl }); } catch (error) { await updateRenderJob(job.id, { status: "failed", progress: 0, errorMessage: error instanceof Error ? error.message : "Falha desconhecida" }); throw error; } }),
    get: protectedProcedure.input(z.object({ id: z.number().int().positive() })).query(({ ctx, input }) => getRenderJob(userId(ctx), input.id)),
  }),
});

export type AppRouter = typeof appRouter;
