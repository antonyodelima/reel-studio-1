import { int, mysqlEnum, mysqlTable, text, timestamp, varchar } from "drizzle-orm/mysql-core";

export const users = mysqlTable("users", {
  id: int("id").autoincrement().primaryKey(),
  openId: varchar("openId", { length: 64 }).notNull().unique(),
  name: text("name"),
  email: varchar("email", { length: 320 }),
  loginMethod: varchar("loginMethod", { length: 64 }),
  role: mysqlEnum("role", ["user", "admin"]).default("user").notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
  lastSignedIn: timestamp("lastSignedIn").defaultNow().notNull(),
});

export const projects = mysqlTable("projects", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  name: varchar("name", { length: 180 }).notNull(),
  type: varchar("type", { length: 40 }).default("Reel").notNull(),
  preset: varchar("preset", { length: 80 }).default("Product Launch").notNull(),
  format: varchar("format", { length: 30 }).default("9:16 Portrait").notNull(),
  engine: mysqlEnum("engine", ["ffmpeg", "remotion", "hyperframes"]).default("ffmpeg").notNull(),
  status: mysqlEnum("status", ["draft", "rendering", "ready", "failed"]).default("draft").notNull(),
  sourceText: text("sourceText"),
  sourceUrl: varchar("sourceUrl", { length: 1000 }),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export const scenes = mysqlTable("scenes", {
  id: int("id").autoincrement().primaryKey(),
  projectId: int("projectId").notNull(),
  position: int("position").notNull(),
  label: varchar("label", { length: 80 }).notNull(),
  title: text("title").notNull(),
  caption: text("caption").notNull(),
  durationMs: int("durationMs").default(5000).notNull(),
  mediaUrl: varchar("mediaUrl", { length: 1200 }),
  mediaKey: varchar("mediaKey", { length: 600 }),
  locked: int("locked").default(0).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export const mediaAssets = mysqlTable("mediaAssets", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  projectId: int("projectId"),
  fileName: varchar("fileName", { length: 255 }).notNull(),
  mimeType: varchar("mimeType", { length: 120 }).notNull(),
  storageKey: varchar("storageKey", { length: 600 }).notNull(),
  storageUrl: varchar("storageUrl", { length: 1200 }).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export const renderJobs = mysqlTable("renderJobs", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  projectId: int("projectId").notNull(),
  engine: mysqlEnum("engine", ["ffmpeg", "remotion", "hyperframes"]).default("ffmpeg").notNull(),
  status: mysqlEnum("status", ["queued", "rendering", "ready", "failed"]).default("queued").notNull(),
  progress: int("progress").default(0).notNull(),
  mp4Url: varchar("mp4Url", { length: 1200 }),
  srtUrl: varchar("srtUrl", { length: 1200 }),
  vttUrl: varchar("vttUrl", { length: 1200 }),
  errorMessage: text("errorMessage"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type User = typeof users.$inferSelect;
export type InsertUser = typeof users.$inferInsert;
export type Project = typeof projects.$inferSelect;
export type Scene = typeof scenes.$inferSelect;
export type MediaAsset = typeof mediaAssets.$inferSelect;
export type RenderJob = typeof renderJobs.$inferSelect;
