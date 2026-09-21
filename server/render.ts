import { execFile } from "node:child_process";
import { mkdtemp, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { promisify } from "node:util";
import { storagePut } from "./storage";
import type { Project, Scene } from "../drizzle/schema";

const execFileAsync = promisify(execFile);
const FONT = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf";
const REMOTION_ENTRY = `import React from 'react'; import {Composition, AbsoluteFill, Sequence, useCurrentFrame, registerRoot} from 'remotion';
const colors=['#ff7452','#6c63ff','#1fbf9f','#57a9df','#f5b83d'];
const Reel=({scenes,width,height})=>{const frame=useCurrentFrame();let cursor=0;return <AbsoluteFill style={{background:'#14171c',width,height}}>{scenes.map((s,i)=>{const from=cursor;const frames=Math.max(30,Math.round((s.durationMs||5000)/1000*30));cursor+=frames;return <Sequence key={i} from={from} durationInFrames={frames}><AbsoluteFill style={{background:colors[i%colors.length],justifyContent:'center',alignItems:'center',padding:48}}><div style={{color:'white',fontFamily:'Arial,sans-serif',fontSize:Math.max(34,Math.floor(width/18)),fontWeight:800,textAlign:'center',background:'rgba(0,0,0,.28)',padding:30,borderRadius:24}}>{s.title}</div><div style={{position:'absolute',bottom:80,left:48,right:48,color:'white',fontFamily:'Arial,sans-serif',fontSize:Math.max(22,Math.floor(width/32)),fontWeight:700,textAlign:'center'}}>{s.caption}</div></AbsoluteFill></Sequence>})}</AbsoluteFill>};
const Root=()=> <Composition id='ReelComposition' component={Reel} width={1080} height={1920} fps={30} durationInFrames={900} defaultProps={{scenes:[],width:1080,height:1920}} calculateMetadata={({props})=>({durationInFrames:Math.max(30,props.scenes.reduce((sum,s)=>sum+Math.max(1000,s.durationMs||5000),0)/1000*30),width:props.width,height:props.height})}/>; registerRoot(Root);`;

function timecode(ms: number, separator: "," | ".") { const hours = Math.floor(ms / 3_600_000).toString().padStart(2, "0"); const minutes = Math.floor((ms % 3_600_000) / 60_000).toString().padStart(2, "0"); const seconds = Math.floor((ms % 60_000) / 1000).toString().padStart(2, "0"); const millis = (ms % 1000).toString().padStart(3, "0"); return `${hours}:${minutes}:${seconds}${separator}${millis}`; }
function escapeSubtitle(value: string) { return value.replace(/\r?\n/g, " ").trim(); }
function escapeDrawtext(value: string) { return value.replace(/\\/g, "\\\\").replace(/:/g, "\\:").replace(/'/g, "\\'").replace(/\n/g, " "); }
export function buildSrt(scenes: Scene[]) { let cursor = 0; return scenes.map((scene, index) => { const start = cursor; cursor += scene.durationMs; return `${index + 1}\n${timecode(start, ",")} --> ${timecode(cursor, ",")}\n${escapeSubtitle(scene.caption)}\n`; }).join("\n"); }
export function buildVtt(scenes: Scene[]) { let cursor = 0; return `WEBVTT\n\n${scenes.map((scene) => { const start = cursor; cursor += scene.durationMs; return `${timecode(start, ".")} --> ${timecode(cursor, ".")}\n${escapeSubtitle(scene.caption)}\n`; }).join("\n")}`; }
function dimensions(format: string) { if (format === "16:9 Landscape") return { width: 1280, height: 720 }; if (format === "1:1 Square") return { width: 1080, height: 1080 }; return { width: 1080, height: 1920 }; }
function colorFor(index: number) { return ["#ff7452", "#6c63ff", "#1fbf9f", "#57a9df", "#f5b83d"][index % 5]; }

export async function renderWithRemotion(folder: string, project: Project, scenes: Scene[]) {
  const entry = `${folder}/remotion-entry.tsx`; const output = `${folder}/reel-remotion.mp4`; await writeFile(entry, REMOTION_ENTRY, "utf8");
  const [{ bundle }, { getCompositions, renderMedia }] = await Promise.all([import("@remotion/bundler"), import("@remotion/renderer")]);
  const serveUrl = await bundle({ entryPoint: entry, enableCaching: false, onProgress: () => undefined });
  const { width, height } = dimensions(project.format);
  const inputProps = { scenes, width, height };
  const compositions = await getCompositions({ serveUrl, inputProps });
  const composition = compositions.find((item) => item.id === "ReelComposition");
  if (!composition) throw new Error("Composição Remotion não encontrada");
  await renderMedia({ composition, serveUrl, codec: "h264", outputLocation: output, inputProps, overwrite: true, concurrency: 1, onProgress: () => undefined, timeoutInMilliseconds: 180_000 });
  return output;
}

async function renderWithFfmpeg(folder: string, project: Project, scenes: Scene[]) {
  const { width, height } = dimensions(project.format); const inputs: string[] = []; const filters: string[] = [];
  scenes.forEach((scene, index) => { const duration = Math.max(1, Math.round(scene.durationMs / 1000)); inputs.push("-f", "lavfi", "-i", `color=c=${colorFor(index)}:s=${width}x${height}:d=${duration}:r=30`); const safeTitle = escapeDrawtext(scene.title); filters.push(`[${index}:v]drawtext=fontfile=${FONT}:text='${safeTitle}':fontcolor=white:fontsize=${Math.max(34, Math.floor(width / 18))}:x=(w-text_w)/2:y=(h-text_h)/2:box=1:boxcolor=black@0.28:boxborderw=30,format=yuv420p[v${index}]`); });
  filters.push(`${scenes.map((_, index) => `[v${index}]`).join("")}concat=n=${scenes.length}:v=1:a=0[outv]`); const output = `${folder}/reel-ffmpeg.mp4`; await execFileAsync("ffmpeg", [...inputs, "-filter_complex", filters.join(";"), "-map", "[outv]", "-movflags", "+faststart", "-pix_fmt", "yuv420p", "-c:v", "libx264", "-preset", "veryfast", "-crf", "25", output], { maxBuffer: 2_000_000, timeout: 180_000 }); return output;
}

export async function renderProject(project: Project, scenes: Scene[], userId: number, engine = project.engine) {
  if (!scenes.length) throw new Error("O projeto precisa ter pelo menos uma cena."); const folder = await mkdtemp(`${tmpdir()}/reel-studio-`);
  try {
    let output: string;
    if (engine === "remotion") { try { output = await renderWithRemotion(folder, project, scenes); } catch (error) { console.warn("[Render] Remotion falhou; usando fallback FFmpeg:", error); output = await renderWithFfmpeg(folder, project, scenes); } } else { output = await renderWithFfmpeg(folder, project, scenes); }
    const [mp4, srt, vtt] = await Promise.all([readFile(output), Promise.resolve(Buffer.from(buildSrt(scenes), "utf8")), Promise.resolve(Buffer.from(buildVtt(scenes), "utf8"))]); const prefix = `users/${userId}/projects/${project.id}/renders/${Date.now()}`;
    const [videoFile, srtFile, vttFile] = await Promise.all([storagePut(`${prefix}/reel-${engine}.mp4`, mp4, "video/mp4"), storagePut(`${prefix}/captions.srt`, srt, "application/x-subrip"), storagePut(`${prefix}/captions.vtt`, vtt, "text/vtt")]); return { mp4Url: videoFile.url, srtUrl: srtFile.url, vttUrl: vttFile.url, engine };
  } finally { await rm(folder, { recursive: true, force: true }); }
}
