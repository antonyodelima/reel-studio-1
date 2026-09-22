import { useEffect, useMemo, useRef, useState } from "react";
import { Check, Globe2, Loader2, Mic2, Pause, Play, Search, ShieldCheck, Sparkles, Trash2, Upload, Volume2, Wand2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { colorClasses, defaultVoice, voicePreviewText } from "@/lib/reel-data";
import { trpc } from "@/lib/trpc";

type Voice = { id: string; name: string; language: string; description: string; gender: string | null; previewFileUrl: string | null };
const fallbackVoices: Voice[] = [
  { id: "fallback-maya", name: "Maya", language: "pt-BR", description: "Voz acolhedora e confiante", gender: "feminine", previewFileUrl: null },
  { id: "fallback-sofia", name: "Sofia", language: "pt-BR", description: "Voz natural e luminosa", gender: "feminine", previewFileUrl: null },
];
const fileToDataUrl = (file: File) => new Promise<string>((resolve, reject) => { const reader = new FileReader(); reader.onload = () => resolve(String(reader.result)); reader.onerror = () => reject(new Error("Não foi possível ler o arquivo")); reader.readAsDataURL(file); });

export default function Voices() {
  const utils = trpc.useUtils();
  const voiceQuery = trpc.projects.listVoices.useQuery(undefined, { retry: false });
  const cloneQuery = trpc.voiceClones.list.useQuery(undefined, { retry: false });
  const previewMutation = trpc.projects.previewVoice.useMutation();
  const cloneMutation = trpc.voiceClones.create.useMutation({ onSuccess: async () => { await Promise.all([utils.voiceClones.list.invalidate(), utils.projects.listVoices.invalidate()]); setCloneFile(null); setCloneName(""); setCloneTagline(""); setConsent(false); toast.success("Voz exclusiva criada com sucesso"); }, onError: (error) => toast.error(error.message) });
  const deleteMutation = trpc.voiceClones.delete.useMutation({ onSuccess: async () => { await Promise.all([utils.voiceClones.list.invalidate(), utils.projects.listVoices.invalidate()]); toast.success("Voz clonada excluída"); }, onError: (error) => toast.error(error.message) });
  const [activeId, setActiveId] = useState("");
  const [playing, setPlaying] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const [language, setLanguage] = useState("Todos");
  const [previewText, setPreviewText] = useState(voicePreviewText);
  const [cloneName, setCloneName] = useState("");
  const [cloneTagline, setCloneTagline] = useState("");
  const [cloneLanguage, setCloneLanguage] = useState("pt-BR");
  const [cloneFile, setCloneFile] = useState<File | null>(null);
  const [consent, setConsent] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [recordingSeconds, setRecordingSeconds] = useState(0);
  const [recordedPreviewUrl, setRecordedPreviewUrl] = useState<string | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const recordingChunksRef = useRef<Blob[]>([]);
  const recordingStartedAtRef = useRef<number | null>(null);
  const recordingTimerRef = useRef<number | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const voices = (voiceQuery.data?.length ? voiceQuery.data : fallbackVoices) as Voice[];
  const languages = useMemo(() => ["Todos", ...Array.from(new Set(voices.map((voice) => voice.language))).sort()], [voices]);
  const filteredVoices = useMemo(() => voices.filter((voice) => `${voice.name} ${voice.description} ${voice.language}`.toLowerCase().includes(search.toLowerCase()) && (language === "Todos" || voice.language === language)), [voices, search, language]);
  const active = voices.find((voice) => voice.id === activeId) ?? filteredVoices[0] ?? voices[0];
  const readyClones = (cloneQuery.data ?? []).filter((clone) => clone.status === "ready" && clone.cartesiaVoiceId);

  useEffect(() => { if (!activeId && active) setActiveId(active.id); }, [active, activeId]);

  useEffect(() => () => {
    if (recordingTimerRef.current) window.clearInterval(recordingTimerRef.current);
    if (recordedPreviewUrl) URL.revokeObjectURL(recordedPreviewUrl);
    mediaRecorderRef.current?.stream.getTracks().forEach((track) => track.stop());
  }, [recordedPreviewUrl]);

  const recordingMimeType = () => {
    const candidates = ["audio/webm;codecs=opus", "audio/webm", "audio/ogg;codecs=opus"];
    return candidates.find((type) => MediaRecorder.isTypeSupported(type)) ?? "";
  };

  const startRecording = async () => {
    if (!navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === "undefined") {
      toast.error("Seu navegador não oferece gravação de áudio");
      return;
    }
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mimeType = recordingMimeType();
      const recorder = new MediaRecorder(stream, mimeType ? { mimeType } : undefined);
      recordingChunksRef.current = [];
      recordingStartedAtRef.current = Date.now();
      setRecordingSeconds(0);
      setIsRecording(true);
      mediaRecorderRef.current = recorder;
      recorder.ondataavailable = (event) => { if (event.data.size > 0) recordingChunksRef.current.push(event.data); };
      recorder.onstop = () => {
        const seconds = Math.round((Date.now() - (recordingStartedAtRef.current ?? Date.now())) / 1000);
        const blob = new Blob(recordingChunksRef.current, { type: recorder.mimeType || "audio/webm" });
        const file = new File([blob], `voz-gravada-${new Date().toISOString().slice(0, 10)}.webm`, { type: blob.type || "audio/webm" });
        if (recordedPreviewUrl) URL.revokeObjectURL(recordedPreviewUrl);
        setRecordedPreviewUrl(URL.createObjectURL(blob));
        setCloneFile(file);
        setIsRecording(false);
        stream.getTracks().forEach((track) => track.stop());
        if (recordingTimerRef.current) window.clearInterval(recordingTimerRef.current);
        setRecordingSeconds(seconds);
        if (seconds < 10) toast.info("Grave pelo menos 10 segundos para obter um clone melhor");
      };
      recorder.start(250);
      recordingTimerRef.current = window.setInterval(() => {
        const elapsed = Math.floor((Date.now() - (recordingStartedAtRef.current ?? Date.now())) / 1000);
        setRecordingSeconds(elapsed);
        if (elapsed >= 60) recorder.stop();
      }, 250);
    } catch (error) {
      toast.error(error instanceof DOMException && error.name === "NotAllowedError" ? "Permita o acesso ao microfone para gravar" : "Não foi possível iniciar o microfone");
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current?.state === "recording") mediaRecorderRef.current.stop();
  };

  const clearSelectedAudio = () => {
    if (recordedPreviewUrl) URL.revokeObjectURL(recordedPreviewUrl);
    setRecordedPreviewUrl(null);
    setCloneFile(null);
    setRecordingSeconds(0);
  };

  const preview = (voice: Voice) => {
    if (playing === voice.id) { audioRef.current?.pause(); setPlaying(null); return; }
    if (voice.id.startsWith("fallback-")) { toast.info("Conecte a Cartesia para ouvir esta voz"); return; }
    setPlaying(voice.id);
    previewMutation.mutate({ voiceId: voice.id, text: previewText, language: voice.language || "pt-BR" }, { onSuccess: ({ audioDataUrl }) => { const audio = new Audio(audioDataUrl); audioRef.current = audio; audio.onended = () => setPlaying(null); void audio.play().catch(() => setPlaying(null)); }, onError: (error) => { setPlaying(null); toast.error(error.message); } });
  };

  const createClone = async () => {
    if (isRecording) return toast.info("Pare a gravação antes de criar o clone");
    if (!cloneFile) return toast.error("Escolha um áudio de referência ou grave pelo microfone");
    if (recordedPreviewUrl && recordingSeconds < 10) return toast.error("A gravação precisa ter pelo menos 10 segundos");
    if (cloneFile.size > 16 * 1024 * 1024) return toast.error("O áudio precisa ter no máximo 16 MB");
    if (!cloneName.trim()) return toast.error("Dê um nome para sua voz");
    if (!consent) return toast.error("Confirme que você tem autorização para usar esta voz");
    try { cloneMutation.mutate({ name: cloneName.trim(), tagline: cloneTagline.trim() || undefined, language: cloneLanguage, fileName: cloneFile.name, mimeType: cloneFile.type || "application/octet-stream", dataUrl: await fileToDataUrl(cloneFile), consentConfirmed: true }); } catch (error) { toast.error(error instanceof Error ? error.message : "Não foi possível preparar o áudio"); }
  };

  return <div className="space-y-9"><section className="flex flex-col justify-between gap-5 md:flex-row md:items-end"><div><p className="text-[10px] font-black uppercase tracking-[.18em] text-coral">Biblioteca Cartesia</p><h1 className="mt-2 text-4xl font-black tracking-[-.04em]">Dê voz à sua ideia.</h1><p className="mt-3 max-w-xl text-sm leading-6 text-muted">Escolha uma voz real do Cartesia Sonic ou crie uma voz exclusiva a partir de um clipe autorizado.</p></div><Button onClick={() => document.getElementById("clonar-voz")?.scrollIntoView({ behavior: "smooth" })} className="w-fit bg-violet text-white hover:bg-violet/90"><Sparkles className="size-4" />Clonar uma voz</Button></section><div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_320px]"><div className="space-y-4"><div className="flex flex-wrap items-center gap-2"><div className="relative"><Search className="absolute left-3 top-1/2 size-3.5 -translate-y-1/2 text-muted" /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar vozes" className="w-60 rounded-full border border-ink/8 bg-white py-2.5 pl-9 pr-4 text-xs outline-none focus:border-coral" /></div><label className="flex items-center gap-2 rounded-full border border-ink/8 bg-white px-3 py-2.5 text-xs font-bold text-muted"><Globe2 className="size-3.5" /><select value={language} onChange={(event) => setLanguage(event.target.value)} className="bg-transparent outline-none">{languages.map((item) => <option key={item}>{item}</option>)}</select></label><span className="rounded-full bg-mint/12 px-3 py-2.5 text-[10px] font-black text-emerald-700">{voiceQuery.isLoading ? "Carregando…" : `${filteredVoices.length} vozes Cartesia`}</span></div>{voiceQuery.error && <div className="rounded-2xl border border-coral/20 bg-coral/5 p-4 text-xs text-coral">Não foi possível carregar as vozes Cartesia agora. Verifique a chave do projeto.</div>}{filteredVoices.map((voice, index) => <div key={voice.id} className={`flex flex-col gap-4 rounded-2xl border bg-white p-4 shadow-soft transition sm:flex-row sm:items-center ${active?.id === voice.id ? "border-coral/40 ring-4 ring-coral/6" : "border-ink/8"}`}><span className={`grid size-14 shrink-0 place-items-center rounded-2xl text-sm font-black ${colorClasses(["coral", "violet", "mint", "sky", "amber"][index % 5])}`}>{voice.name.slice(0, 2).toUpperCase()}</span><div className="min-w-0 flex-1"><div className="flex flex-wrap items-center gap-2"><h3 className="text-sm font-black">{voice.name}</h3>{active?.id === voice.id && <span className="rounded-full bg-mint/12 px-2 py-1 text-[10px] font-bold text-emerald-700"><Check className="mr-1 inline size-3" />Voz padrão</span>}</div><p className="mt-1 text-xs text-muted">{voice.description} · {voice.language}</p><div className="mt-3 flex items-center gap-1">{Array.from({ length: 16 }, (_, bar) => <span key={bar} className={`h-${(bar % 3) + 2} w-1 rounded-full ${bar % 3 === 0 ? "bg-coral/35" : "bg-ink/12"}`} style={{ height: `${10 + ((bar * 7) % 20)}px` }} />)}</div></div><div className="flex items-center gap-2"><button disabled={previewMutation.isPending && playing === voice.id} onClick={() => preview(voice)} className="grid size-10 place-items-center rounded-xl border border-ink/8 text-muted transition hover:bg-ink/5 hover:text-ink disabled:opacity-50">{playing === voice.id ? <Pause className="size-4 fill-current" /> : <Play className="ml-0.5 size-4 fill-current" />}</button><button onClick={() => { setActiveId(voice.id); toast.success(`${voice.name} definida como voz padrão`); }} className={`rounded-xl px-3 py-2.5 text-xs font-bold transition ${active?.id === voice.id ? "bg-ink/6 text-muted" : "bg-ink text-paper hover:bg-ink/90"}`}>{active?.id === voice.id ? "Selecionada" : "Usar voz"}</button></div></div>)}</div><aside className="h-fit rounded-[26px] bg-ink p-6 text-paper shadow-dark"><div className="flex items-center justify-between"><div><p className="text-[10px] font-black uppercase tracking-[.16em] text-white/45">Prévia da voz</p><h2 className="mt-2 text-xl font-black">{active?.name ?? defaultVoice}</h2></div><span className="grid size-10 place-items-center rounded-xl bg-coral text-white"><Mic2 className="size-4" /></span></div><textarea value={previewText} onChange={(event) => setPreviewText(event.target.value)} className="mt-6 min-h-28 w-full resize-none rounded-2xl border border-white/10 bg-white/8 p-4 text-sm font-semibold leading-6 text-white outline-none placeholder:text-white/40 focus:border-coral" placeholder="Digite o texto da prévia" /><div className="mt-5 flex items-center gap-2 text-[10px] font-bold text-white/45"><Volume2 className="size-3.5" />Cartesia Sonic · áudio WAV · {active?.language ?? "pt-BR"}</div><button disabled={!active || previewMutation.isPending} onClick={() => active && preview(active)} className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl bg-coral py-3 text-xs font-black text-white hover:bg-coral/90 disabled:opacity-50">{previewMutation.isPending ? <Wand2 className="size-3.5 animate-pulse" /> : playing === active?.id ? <Pause className="size-3.5" /> : <Play className="size-3.5" />}Ouvir prévia real</button><p className="mt-4 text-[11px] leading-5 text-white/45">A chave Cartesia fica somente no servidor. O navegador recebe apenas o áudio gerado para reprodução.</p></aside></div><section id="clonar-voz" className="grid gap-5 rounded-[26px] border border-violet/15 bg-violet/5 p-6 lg:grid-cols-[1.1fr_.9fr]"><div><p className="text-[10px] font-black uppercase tracking-[.16em] text-violet">Voz exclusiva</p><h2 className="mt-2 text-2xl font-black tracking-tight">Crie seu timbre proprietário.</h2><p className="mt-3 max-w-xl text-sm leading-6 text-muted">Envie uma gravação de 10 a 60 segundos com uma única pessoa falando naturalmente. A Cartesia cria um clone privado que aparece na sua biblioteca e pode narrar qualquer cena.</p><div className="mt-5 grid gap-3 text-xs text-muted sm:grid-cols-3"><span className="flex items-center gap-2"><ShieldCheck className="size-4 text-violet" />Somente com autorização</span><span className="flex items-center gap-2"><Mic2 className="size-4 text-violet" />Sem música ou eco</span><span className="flex items-center gap-2"><Volume2 className="size-4 text-violet" />Até 16 MB</span></div>{readyClones.length > 0 && <div className="mt-6 space-y-2"><p className="text-[10px] font-black uppercase tracking-[.14em] text-muted">Suas vozes exclusivas</p>{readyClones.map((clone) => <div key={clone.id} className="flex items-center justify-between rounded-2xl border border-ink/8 bg-white px-4 py-3"><div><p className="text-xs font-black">{clone.name}</p><p className="mt-1 text-[10px] text-muted">Cartesia · {clone.language}</p></div><button onClick={() => clone.cartesiaVoiceId && deleteMutation.mutate({ id: clone.id })} disabled={deleteMutation.isPending} className="rounded-lg p-2 text-muted hover:bg-coral/10 hover:text-coral" aria-label={`Excluir ${clone.name}`}><Trash2 className="size-4" /></button></div>)}</div>}</div><div className="rounded-2xl border border-ink/8 bg-white p-5 shadow-soft"><div className="grid gap-3 sm:grid-cols-2"><label className="grid gap-1.5 sm:col-span-2"><span className="text-[11px] font-bold text-muted">Nome da voz</span><input value={cloneName} onChange={(event) => setCloneName(event.target.value)} placeholder="Ex.: Voz da marca" className="rounded-xl border border-ink/8 px-3 py-2.5 text-xs outline-none focus:border-violet" /></label><label className="grid gap-1.5"><span className="text-[11px] font-bold text-muted">Idioma da gravação</span><select value={cloneLanguage} onChange={(event) => setCloneLanguage(event.target.value)} className="rounded-xl border border-ink/8 bg-white px-3 py-2.5 text-xs outline-none focus:border-violet"><option value="pt-BR">Português (Brasil)</option><option value="en">English</option><option value="es">Español</option><option value="fr">Français</option></select></label><label className="grid gap-1.5"><span className="text-[11px] font-bold text-muted">Descrição curta</span><input value={cloneTagline} onChange={(event) => setCloneTagline(event.target.value)} placeholder="Ex.: Confiante e calorosa" maxLength={32} className="rounded-xl border border-ink/8 px-3 py-2.5 text-xs outline-none focus:border-violet" /></label></div><div className="mt-3 space-y-2"><div className="grid gap-2 sm:grid-cols-2"><label className="flex cursor-pointer items-center justify-center gap-2 rounded-xl border border-dashed border-violet/30 bg-violet/5 px-4 py-4 text-xs font-bold transition hover:bg-violet/10"><Upload className="size-4 text-violet" />Enviar áudio<input type="file" accept="audio/flac,audio/mpeg,audio/mp3,audio/ogg,audio/oga,audio/wav,audio/webm" onChange={(event) => { setRecordedPreviewUrl(null); setRecordingSeconds(0); setCloneFile(event.target.files?.[0] ?? null); }} className="sr-only" /></label><button type="button" onClick={isRecording ? stopRecording : startRecording} className={`flex items-center justify-center gap-2 rounded-xl px-4 py-4 text-xs font-black transition ${isRecording ? "bg-coral text-white" : "border border-coral/25 bg-coral/5 text-coral hover:bg-coral/10"}`}>{isRecording ? <><span className="size-2 animate-pulse rounded-full bg-white" />Parar gravação</> : <><Mic2 className="size-4" />Gravar pelo microfone</>}</button></div>{isRecording && <div className="flex items-center justify-between rounded-xl bg-coral/8 px-3 py-2 text-[11px] font-bold text-coral"><span>Gravando… fale naturalmente em um ambiente silencioso</span><span className="font-mono">{String(Math.floor(recordingSeconds / 60)).padStart(2, "0")}:{String(recordingSeconds % 60).padStart(2, "0")} / 01:00</span></div>}{cloneFile && <div className="flex items-center gap-3 rounded-xl border border-ink/8 bg-ink/3 px-3 py-2"><span className="min-w-0 flex-1 truncate text-[11px] font-bold">{cloneFile.name} · {(cloneFile.size / 1024 / 1024).toFixed(1)} MB{recordedPreviewUrl ? ` · ${recordingSeconds}s gravados` : ""}</span>{recordedPreviewUrl && <audio controls src={recordedPreviewUrl} className="h-8 max-w-[150px]" />}<button type="button" onClick={clearSelectedAudio} className="text-[10px] font-bold text-coral hover:underline">Remover</button></div>}</div><label className="mt-4 flex items-start gap-3 text-[11px] leading-5 text-muted"><input type="checkbox" checked={consent} onChange={(event) => setConsent(event.target.checked)} className="mt-1 accent-violet" />Confirmo que tenho autorização da pessoa gravada para criar e usar este clone de voz.</label><Button onClick={createClone} disabled={cloneMutation.isPending} className="mt-4 w-full bg-violet text-white hover:bg-violet/90">{cloneMutation.isPending ? <><Loader2 className="size-4 animate-spin" />Criando clone…</> : <><Sparkles className="size-4" />Criar voz exclusiva</>}</Button><p className="mt-3 text-center text-[10px] leading-4 text-muted">Grave de 10 a 60 segundos ou envie um arquivo. O áudio é enviado somente para a Cartesia e a voz fica privada na sua conta.</p></div></section></div>;
}
