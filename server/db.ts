import { and, asc, desc, eq } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import { InsertUser, users, projects, scenes, mediaAssets, renderJobs, Project, Scene } from "../drizzle/schema";
import { ENV } from "./_core/env";

let _db: ReturnType<typeof drizzle> | null = null;

export async function getDb() {
  if (!_db && process.env.DATABASE_URL) {
    try { _db = drizzle(process.env.DATABASE_URL); } catch (error) { console.warn("[Database] Failed to connect:", error); _db = null; }
  }
  return _db;
}

export async function upsertUser(user: InsertUser): Promise<void> {
  if (!user.openId) throw new Error("User openId is required for upsert");
  const db = await getDb();
  if (!db) return;
  const values: InsertUser = { openId: user.openId };
  const updateSet: Record<string, unknown> = {};
  for (const field of ["name", "email", "loginMethod"] as const) {
    if (user[field] !== undefined) { values[field] = user[field] ?? null; updateSet[field] = user[field] ?? null; }
  }
  values.lastSignedIn = user.lastSignedIn ?? new Date(); updateSet.lastSignedIn = values.lastSignedIn;
  if (user.role !== undefined || user.openId === ENV.ownerOpenId) { values.role = user.role ?? "admin"; updateSet.role = values.role; }
  await db.insert(users).values(values).onDuplicateKeyUpdate({ set: updateSet });
}

export async function getUserByOpenId(openId: string) {
  const db = await getDb(); if (!db) return undefined;
  const result = await db.select().from(users).where(eq(users.openId, openId)).limit(1);
  return result[0];
}

export async function listProjects(userId: number) {
  const db = await getDb(); if (!db) return [];
  return db.select().from(projects).where(eq(projects.userId, userId)).orderBy(desc(projects.updatedAt));
}

export async function getProjectById(userId: number, projectId: number) {
  const db = await getDb(); if (!db) return undefined;
  const result = await db.select().from(projects).where(and(eq(projects.id, projectId), eq(projects.userId, userId))).limit(1);
  return result[0];
}

export async function updateProjectRecord(userId: number, projectId: number, input: Partial<Pick<Project, "name" | "format" | "engine" | "status">>) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  await db.update(projects).set(input).where(and(eq(projects.id, projectId), eq(projects.userId, userId)));
  return getProjectById(userId, projectId);
}

export async function getScenes(projectId: number) {
  const db = await getDb(); if (!db) return [];
  return db.select().from(scenes).where(eq(scenes.projectId, projectId)).orderBy(asc(scenes.position));
}

export async function createProjectRecord(userId: number, input: { name: string; preset?: string; format?: string; type?: string; sourceText?: string; sourceUrl?: string }) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  const inserted = await db.insert(projects).values({ userId, name: input.name, preset: input.preset ?? "Product Launch", format: input.format ?? "9:16 Portrait", type: input.type ?? "Reel", sourceText: input.sourceText, sourceUrl: input.sourceUrl }).$returningId();
  return getProjectById(userId, inserted[0].id);
}

export async function createSceneRecords(projectId: number, records: Array<{ position: number; label: string; title: string; caption?: string; durationMs?: number }>) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  if (records.length) await db.insert(scenes).values(records.map((scene) => ({ projectId, position: scene.position, label: scene.label, title: scene.title, caption: scene.caption ?? scene.title, durationMs: scene.durationMs ?? 5000 })));
  return getScenes(projectId);
}

export async function updateSceneRecord(projectId: number, sceneId: number, input: Partial<Pick<Scene, "title" | "caption" | "durationMs" | "locked" | "position" | "mediaUrl" | "mediaKey">>) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  await db.update(scenes).set(input).where(and(eq(scenes.id, sceneId), eq(scenes.projectId, projectId)));
  const result = await db.select().from(scenes).where(and(eq(scenes.id, sceneId), eq(scenes.projectId, projectId))).limit(1);
  return result[0];
}

export async function createMediaRecord(input: typeof mediaAssets.$inferInsert) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  const inserted = await db.insert(mediaAssets).values(input).$returningId();
  const result = await db.select().from(mediaAssets).where(eq(mediaAssets.id, inserted[0].id)).limit(1);
  return result[0];
}

export async function createRenderJob(input: typeof renderJobs.$inferInsert) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  const inserted = await db.insert(renderJobs).values(input).$returningId();
  const result = await db.select().from(renderJobs).where(eq(renderJobs.id, inserted[0].id)).limit(1);
  return result[0];
}

export async function updateRenderJob(id: number, patch: Partial<typeof renderJobs.$inferInsert>) {
  const db = await getDb(); if (!db) throw new Error("Database is not configured");
  await db.update(renderJobs).set(patch).where(eq(renderJobs.id, id));
  const result = await db.select().from(renderJobs).where(eq(renderJobs.id, id)).limit(1);
  return result[0];
}

export async function getRenderJob(userId: number, id: number) {
  const db = await getDb(); if (!db) return undefined;
  const result = await db.select().from(renderJobs).where(and(eq(renderJobs.id, id), eq(renderJobs.userId, userId))).limit(1);
  return result[0];
}
