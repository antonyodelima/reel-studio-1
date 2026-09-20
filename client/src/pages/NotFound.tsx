import { Link } from "wouter";
import { ArrowLeft, Film } from "lucide-react";
import { notFoundCopy, notFoundAction } from "@/lib/reel-data";

export default function NotFound() {
  return <div className="grid min-h-[65vh] place-items-center"><div className="max-w-md text-center"><span className="mx-auto grid size-14 place-items-center rounded-2xl bg-coral/12 text-coral"><Film className="size-6" /></span><p className="mt-6 text-[10px] font-black uppercase tracking-[.18em] text-coral">404 / Off the timeline</p><h1 className="mt-3 text-3xl font-black tracking-tight">{notFoundCopy}</h1><p className="mt-3 text-sm leading-6 text-muted">The page you were looking for does not exist in this workspace.</p><Link href="/" className="mt-7 inline-flex items-center gap-2 rounded-full bg-ink px-4 py-2.5 text-xs font-bold text-paper"><ArrowLeft className="size-3.5" />{notFoundAction}</Link></div></div>;
}
