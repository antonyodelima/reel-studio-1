import { useEffect, useState } from "react";
import { Link, Route, Switch, useLocation } from "wouter";
import {
  Bell,
  BookOpen,
  ChevronDown,
  Command,
  Film,
  Grid2X2,
  LogOut,
  Menu,
  Mic2,
  Plus,
  Search,
  Settings2,
  Sparkles,
  X,
} from "lucide-react";
import { Toaster, toast } from "sonner";
import Home from "@/pages/Home";
import Editor from "@/pages/Editor";
import Templates from "@/pages/Templates";
import Voices from "@/pages/Voices";
import Settings from "@/pages/Settings";
import NotFound from "@/pages/NotFound";
import { navItems, navigationHint, notificationCount, ownerName, initials, workspaceName, navFooter } from "@/lib/reel-data";
import { cn } from "@/lib/utils";

const iconMap = { grid: Grid2X2, sparkles: Sparkles, mic: Mic2, settings: Settings2, book: BookOpen };

function Sidebar({ mobileOpen, onClose }: { mobileOpen: boolean; onClose: () => void }) {
  const [location] = useLocation();
  return (
    <>
      {mobileOpen && <button aria-label="Fechar menu" onClick={onClose} className="fixed inset-0 z-30 bg-ink/30 backdrop-blur-sm lg:hidden" />}
      <aside className={cn("fixed inset-y-0 left-0 z-40 flex w-[252px] flex-col border-r border-ink/8 bg-paper px-4 py-5 transition-transform duration-300 lg:static lg:translate-x-0", mobileOpen ? "translate-x-0" : "-translate-x-full")}>
        <div className="flex items-center justify-between px-2">
          <Link href="/" onClick={onClose} className="group flex items-center gap-3">
            <span className="grid size-9 place-items-center rounded-xl bg-ink text-paper shadow-soft transition group-hover:rotate-6"><Film className="size-4" /></span>
            <span><span className="block text-sm font-black tracking-tight">Reel Studio</span><span className="block text-[10px] font-bold uppercase tracking-[.18em] text-muted">{workspaceName}</span></span>
          </Link>
          <button className="rounded-lg p-2 text-muted hover:bg-ink/5 lg:hidden" onClick={onClose}><X className="size-4" /></button>
        </div>
        <div className="mt-8">
          <p className="px-3 text-[10px] font-bold uppercase tracking-[.18em] text-muted">Espaço de trabalho</p>
          <nav aria-label="Navigation" className="mt-2 space-y-1">
            {navItems.map((item) => {
              const Icon = iconMap[item.icon as keyof typeof iconMap];
              const active = item.href === "/" ? location === "/" : location.startsWith(item.href);
              return <Link key={item.href} href={item.href} onClick={onClose} className={cn("flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-semibold transition", active ? "bg-white text-ink shadow-soft" : "text-muted hover:bg-ink/5 hover:text-ink")}><Icon className={cn("size-[17px]", active ? "text-coral" : "text-muted")} />{item.label}{item.label === "Projetos" && <span className="ml-auto rounded-full bg-ink/6 px-2 py-0.5 text-[10px] text-muted">3</span>}</Link>;
            })}
          </nav>
        </div>
        <div className="mt-8">
          <p className="px-3 text-[10px] font-bold uppercase tracking-[.18em] text-muted">Recursos</p>
          <div className="mt-2 space-y-1">
            <button onClick={() => toast.info("Guia do criador is coming soon")} className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-semibold text-muted transition hover:bg-ink/5 hover:text-ink"><BookOpen className="size-[17px]" />Guia do criador</button>
            <button onClick={() => toast.info(navigationHint)} className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-semibold text-muted transition hover:bg-ink/5 hover:text-ink"><Command className="size-[17px]" />Atalhos de teclado<span className="ml-auto text-[10px] text-muted">⌘K</span></button>
          </div>
        </div>
        <div className="mt-auto rounded-2xl border border-ink/8 bg-white p-3 shadow-soft">
          <div className="flex items-center gap-3"><span className="grid size-9 place-items-center rounded-xl bg-coral/12 text-xs font-black text-coral">{initials}</span><div className="min-w-0"><p className="truncate text-xs font-bold">{ownerName}</p><p className="truncate text-[11px] text-muted">Espaço local</p></div><ChevronDown className="ml-auto size-4 text-muted" /></div>
          <div className="mt-3 flex items-center justify-between border-t border-ink/8 pt-3 text-[10px] font-semibold text-muted"><span>{navFooter}</span><button onClick={() => toast.info("Você já está trabalhando localmente")}><LogOut className="size-3.5" /></button></div>
        </div>
      </aside>
    </>
  );
}

function Topbar({ onOpenNav }: { onOpenNav: () => void }) {
  const [, navigate] = useLocation();
  const [commandOpen, setCommandOpen] = useState(false);
  useEffect(() => {
    const listener = (event: KeyboardEvent) => { if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "k") { event.preventDefault(); setCommandOpen(true); } };
    window.addEventListener("keydown", listener); return () => window.removeEventListener("keydown", listener);
  }, []);
  return <>
    <header className="sticky top-0 z-20 flex h-[72px] items-center justify-between border-b border-ink/8 bg-paper/90 px-4 backdrop-blur-xl sm:px-7">
      <div className="flex items-center gap-3"><button aria-label="Abrir navegação" onClick={onOpenNav} className="rounded-xl p-2 text-muted hover:bg-ink/5 lg:hidden"><Menu className="size-5" /></button><button onClick={() => setCommandOpen(true)} className="hidden items-center gap-3 rounded-full border border-ink/8 bg-white px-3 py-2 text-xs font-semibold text-muted shadow-soft transition hover:border-ink/15 sm:flex"><Search className="size-3.5" />Buscar no estúdio<span className="ml-4 rounded-md bg-ink/5 px-1.5 py-0.5 text-[10px]">⌘ K</span></button><span className="text-sm font-bold text-muted sm:hidden">Seu estúdio</span></div>
      <div className="flex items-center gap-2"><button onClick={() => toast.info("Nenhuma notificação nova")} className="relative rounded-xl p-2.5 text-muted transition hover:bg-ink/5 hover:text-ink"><Bell className="size-[18px]" /><span className="absolute right-1.5 top-1.5 grid size-3.5 place-items-center rounded-full bg-coral text-[8px] font-black text-white">{notificationCount}</span></button><button onClick={() => navigate("/")} className="hidden items-center gap-2 rounded-full bg-ink px-3 py-2 text-xs font-bold text-paper shadow-dark transition hover:bg-ink/90 sm:flex"><Plus className="size-3.5" />Novo projeto</button><span className="grid size-9 place-items-center rounded-full bg-coral/12 text-xs font-black text-coral">AM</span></div>
    </header>
    {commandOpen && <div className="fixed inset-0 z-50 grid place-items-start bg-ink/25 px-4 pt-[14vh] backdrop-blur-sm" onClick={() => setCommandOpen(false)}><div className="w-full max-w-lg overflow-hidden rounded-2xl border border-ink/10 bg-paper shadow-dark" onClick={(e) => e.stopPropagation()}><div className="flex items-center gap-3 border-b border-ink/8 p-4"><Search className="size-4 text-muted" /><input autoFocus placeholder="Buscar projetos, cenas e templates…" className="flex-1 bg-transparent text-sm outline-none" /><kbd className="rounded bg-ink/6 px-2 py-1 text-[10px] text-muted">Esc</kbd></div><div className="p-2"><button onClick={() => { setCommandOpen(false); navigate("/"); }} className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left text-sm font-semibold hover:bg-ink/5"><Grid2X2 className="size-4 text-coral" />Abrir projetos<span className="ml-auto text-xs text-muted">↵</span></button><button onClick={() => { setCommandOpen(false); navigate("/templates"); }} className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left text-sm font-semibold hover:bg-ink/5"><Sparkles className="size-4 text-violet" />Explorar templates</button><button onClick={() => { setCommandOpen(false); navigate("/settings"); }} className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left text-sm font-semibold hover:bg-ink/5"><Settings2 className="size-4 text-muted" />Abrir configurações</button></div></div></div>}
  </>;
}

function Shell({ children }: { children: React.ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  return <div className="min-h-screen bg-paper text-ink"><div className="flex min-h-screen"><Sidebar mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} /><div className="min-w-0 flex-1"><Topbar onOpenNav={() => setMobileOpen(true)} /><main className="mx-auto w-full max-w-[1360px] px-4 py-7 sm:px-7 lg:px-10 lg:py-10">{children}</main></div></div><Toaster position="bottom-right" richColors /></div>;
}

export default function App() {
  return <Shell><Switch><Route path="/" component={Home} /><Route path="/editor/:id" component={Editor} /><Route path="/templates" component={Templates} /><Route path="/voices" component={Voices} /><Route path="/settings" component={Settings} /><Route component={NotFound} /></Switch></Shell>;
}
