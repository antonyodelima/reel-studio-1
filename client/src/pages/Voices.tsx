import { useEffect, useMemo, useRef, useState } from "react";
import { Check, Globe2, Mic2, Pause, Play, Search, Sparkles, Volume2, Wand2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { colorClasses, defaultVoice, voicePreviewText } from "@/lib/reel-data";
import { trpc } from "@/lib/trpc";

type Voice = { id: string; name: string; language: string; description: string; gender: string | null; previewFileUrl: string | null };
const fallbackVoices: Voice[] = [
  { id: "fallback-maya", name: "Maya", language: "pt-BR", description: "Voz acolhedora e confiante", gender: "feminine", previewFileUrl: null },
  { id: "fallback-sofia", name: "Sofia", language: "pt-BR", description: "Voz natural e luminosa", gender: "feminine", previewFileUrl: null },
];

export default function Voices() {
  const voiceQuery = trpc.projects.listVoices.useQuery(undefined, { retry: false });
  const previewMutation = trpc.projects.previewVoice.useMutation();
  const [activeId, setActiveId] = useState("");
  const [playing, setPlaying] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const [language, setLanguage] = useState("Todos");
  const [previewText, setPreviewText] = useState(voicePreviewText);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const voices = (voiceQuery.data?.length ? voiceQuery.data : fallbackVoices) as Voice[];
  const languages = useMemo(() => ["Todos", ...Array.from(new Set(voices.map((voice) => voice.language))).sort()], [voices]);
  const filteredVoices = useMemo(() => voices.filter((voice) => {
    const matchesSearch = `${voice.name} ${voice.description} ${voice.language}`.toLowerCase().includes(search.toLowerCase());
    return matchesSearch && (language === "Todos" || voice.language === language);
  }), [voices, search, language]);
  const active = voices.find((voice) => voice.id === activeId) ?? filteredVoices[0] ?? voices[0];

  useEffect(() => {
    if (!activeId && active) setActiveId(active.id);
  }, [active, activeId]);

  const preview = (voice: Voice) => {
    if (playing === voice.id) {
      audioRef.current?.pause();
      setPlaying(null);
      return;
    }
    if (voice.id.startsWith("fallback-")) {
      toast.info("Conecte a Cartesia para ouvir esta voz");
      return;
    }
    setPlaying(voice.id);
    previewMutation.mutate({ voiceId: voice.id, text: previewText, language: voice.language || "pt-BR" }, {
      onSuccess: ({ audioDataUrl }) => {
        const audio = new Audio(audioDataUrl);
        audioRef.current = audio;
        audio.onended = () => setPlaying(null);
        void audio.play().catch(() => setPlaying(null));
      },
      onError: (error) => { setPlaying(null); toast.error(error.message); },
    });
  };

  return <div className="space-y-9"><section className="flex flex-col justify-between gap-5 md:flex-row md:items-end"><div><p className="text-[10px] font-black uppercase tracking-[.18em] text-coral">Biblioteca Cartesia</p><h1 className="mt-2 text-4xl font-black tracking-[-.04em]">Dê voz à sua ideia.</h1><p className="mt-3 max-w-xl text-sm leading-6 text-muted">Escolha uma voz real do Cartesia Sonic, ouça uma prévia e use o mesmo timbre em todas as cenas.</p></div><Button onClick={() => toast.info("Clonagem de voz Cartesia será adicionada em breve")} className="w-fit bg-violet text-white hover:bg-violet/90"><Sparkles className="size-4" />Clonar uma voz</Button></section><div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_320px]"><div className="space-y-4"><div className="flex flex-wrap items-center gap-2"><div className="relative"><Search className="absolute left-3 top-1/2 size-3.5 -translate-y-1/2 text-muted" /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar vozes" className="w-60 rounded-full border border-ink/8 bg-white py-2.5 pl-9 pr-4 text-xs outline-none focus:border-coral" /></div><label className="flex items-center gap-2 rounded-full border border-ink/8 bg-white px-3 py-2.5 text-xs font-bold text-muted"><Globe2 className="size-3.5" /><select value={language} onChange={(event) => setLanguage(event.target.value)} className="bg-transparent outline-none">{languages.map((item) => <option key={item}>{item}</option>)}</select></label><span className="rounded-full bg-mint/12 px-3 py-2.5 text-[10px] font-black text-emerald-700">{voiceQuery.isLoading ? "Carregando…" : `${filteredVoices.length} vozes reais`}</span></div>{voiceQuery.error && <div className="rounded-2xl border border-coral/20 bg-coral/5 p-4 text-xs text-coral">Não foi possível carregar as vozes Cartesia agora. Verifique a chave nas configurações do projeto.</div>}{filteredVoices.map((voice, index) => <div key={voice.id} className={`flex flex-col gap-4 rounded-2xl border bg-white p-4 shadow-soft transition sm:flex-row sm:items-center ${active?.id === voice.id ? "border-coral/40 ring-4 ring-coral/6" : "border-ink/8"}`}><span className={`grid size-14 shrink-0 place-items-center rounded-2xl text-sm font-black ${colorClasses(["coral", "violet", "mint", "sky", "amber"][index % 5])}`}>{voice.name.slice(0, 2).toUpperCase()}</span><div className="min-w-0 flex-1"><div className="flex flex-wrap items-center gap-2"><h3 className="text-sm font-black">{voice.name}</h3>{active?.id === voice.id && <span className="rounded-full bg-mint/12 px-2 py-1 text-[10px] font-bold text-emerald-700"><Check className="mr-1 inline size-3" />Voz padrão</span>}</div><p className="mt-1 text-xs text-muted">{voice.description} · {voice.language}</p><div className="mt-3 flex items-center gap-1">{Array.from({ length: 16 }, (_, bar) => <span key={bar} className={`h-${(bar % 3) + 2} w-1 rounded-full ${bar % 3 === 0 ? "bg-coral/35" : "bg-ink/12"}`} style={{ height: `${10 + ((bar * 7) % 20)}px` }} />)}</div></div><div className="flex items-center gap-2"><button disabled={previewMutation.isPending && playing === voice.id} onClick={() => preview(voice)} className="grid size-10 place-items-center rounded-xl border border-ink/8 text-muted transition hover:bg-ink/5 hover:text-ink disabled:opacity-50">{playing === voice.id ? <Pause className="size-4 fill-current" /> : <Play className="ml-0.5 size-4 fill-current" />}</button><button onClick={() => { setActiveId(voice.id); toast.success(`${voice.name} definida como voz padrão`); }} className={`rounded-xl px-3 py-2.5 text-xs font-bold transition ${active?.id === voice.id ? "bg-ink/6 text-muted" : "bg-ink text-paper hover:bg-ink/90"}`}>{active?.id === voice.id ? "Selecionada" : "Usar voz"}</button></div></div>)}</div><aside className="h-fit rounded-[26px] bg-ink p-6 text-paper shadow-dark"><div className="flex items-center justify-between"><div><p className="text-[10px] font-black uppercase tracking-[.16em] text-white/45">Prévia da voz</p><h2 className="mt-2 text-xl font-black">{active?.name ?? defaultVoice}</h2></div><span className="grid size-10 place-items-center rounded-xl bg-coral text-white"><Mic2 className="size-4" /></span></div><textarea value={previewText} onChange={(event) => setPreviewText(event.target.value)} className="mt-6 min-h-28 w-full resize-none rounded-2xl border border-white/10 bg-white/8 p-4 text-sm font-semibold leading-6 text-white outline-none placeholder:text-white/40 focus:border-coral" placeholder="Digite o texto da prévia" /><div className="mt-5 flex items-center gap-2 text-[10px] font-bold text-white/45"><Volume2 className="size-3.5" />Cartesia Sonic · áudio WAV · {active?.language ?? "pt-BR"}</div><button disabled={!active || previewMutation.isPending} onClick={() => active && preview(active)} className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl bg-coral py-3 text-xs font-black text-white hover:bg-coral/90 disabled:opacity-50">{previewMutation.isPending ? <Wand2 className="size-3.5 animate-pulse" /> : playing === active?.id ? <Pause className="size-3.5" /> : <Play className="size-3.5" />}Ouvir prévia real</button><p className="mt-4 text-[11px] leading-5 text-white/45">A chave Cartesia fica somente no servidor. O navegador recebe apenas o áudio gerado para reprodução.</p></aside></div></div>;
}
