export type ProjectStatus = "draft" | "rendering" | "ready";

export type Project = {
  id: string;
  name: string;
  type: string;
  preset: string;
  status: ProjectStatus;
  scenes: number;
  duration: string;
  updated: string;
  color: string;
};

export const starterProjects: Project[] = [
  {
    id: "product-launch",
    name: "Product launch / Aurora",
    type: "Reel",
    preset: "Product Launch",
    status: "ready",
    scenes: 7,
    duration: "00:32",
    updated: "2 min ago",
    color: "coral",
  },
  {
    id: "founder-story",
    name: "Founder story — from zero",
    type: "Short",
    preset: "Creator Punch",
    status: "draft",
    scenes: 5,
    duration: "00:24",
    updated: "Yesterday",
    color: "violet",
  },
  {
    id: "data-story",
    name: "Why teams ship faster",
    type: "Explainer",
    preset: "Data Story",
    status: "rendering",
    scenes: 9,
    duration: "00:48",
    updated: "Yesterday",
    color: "mint",
  },
];

export const presets = [
  { name: "Product Launch", meta: "Bold reveal · 9:16", color: "coral", icon: "✦" },
  { name: "Creator Punch", meta: "Fast cuts · 9:16", color: "violet", icon: "✺" },
  { name: "Editorial Explainer", meta: "Clean story · 16:9", color: "sky", icon: "▤" },
  { name: "Data Story", meta: "Charts & numbers · 1:1", color: "mint", icon: "⌁" },
  { name: "Developer Demo", meta: "Product walkthrough · 16:9", color: "amber", icon: "⌘" },
  { name: "Cinematic Brand", meta: "Atmospheric · 9:16", color: "plum", icon: "◒" },
];

export const voices = [
  { name: "Maya", role: "Warm · confident", language: "English (US)", color: "coral", initials: "MA" },
  { name: "Theo", role: "Clear · energetic", language: "English (UK)", color: "sky", initials: "TH" },
  { name: "Sofia", role: "Natural · bright", language: "Português (BR)", color: "mint", initials: "SO" },
  { name: "Nina", role: "Soft · cinematic", language: "Español (ES)", color: "violet", initials: "NI" },
];

export const sceneSamples = [
  { id: 1, label: "Hook", title: "The old way is over.", caption: "The old way is over.", duration: "00:04", accent: "coral" },
  { id: 2, label: "Context", title: "Ideas move at the speed of your workflow.", caption: "Ideas move at the speed of your workflow.", duration: "00:06", accent: "violet" },
  { id: 3, label: "Proof", title: "One brief. Every format.", caption: "One brief. Every format.", duration: "00:05", accent: "mint" },
  { id: 4, label: "Feature", title: "Create, refine, publish.", caption: "Create, refine, publish.", duration: "00:07", accent: "sky" },
  { id: 5, label: "CTA", title: "Make your next reel feel inevitable.", caption: "Make your next reel feel inevitable.", duration: "00:06", accent: "amber" },
];

export const navItems = [
  { label: "Projects", href: "/", icon: "grid" },
  { label: "Templates", href: "/templates", icon: "sparkles" },
  { label: "Voices", href: "/voices", icon: "mic" },
  { label: "Settings", href: "/settings", icon: "settings" },
];

export function getProject(id?: string) {
  return starterProjects.find((project) => project.id === id) ?? starterProjects[0];
}

export function colorClasses(color: string) {
  return {
    coral: "bg-coral/12 text-coral",
    violet: "bg-violet/12 text-violet",
    sky: "bg-sky/12 text-sky",
    mint: "bg-mint/12 text-mint",
    amber: "bg-amber/12 text-amber",
    plum: "bg-plum/12 text-plum",
  }[color] ?? "bg-ink/8 text-ink";
}

export function statusLabel(status: ProjectStatus) {
  return { draft: "Draft", rendering: "Rendering", ready: "Ready" }[status];
}

export function statusTone(status: ProjectStatus) {
  return {
    draft: "bg-ink/6 text-muted",
    rendering: "bg-amber/12 text-amber",
    ready: "bg-mint/14 text-emerald-700",
  }[status];
}

export function iconFor(name: string) {
  return name;
}

export function formatDuration(seconds: number) {
  const minutes = Math.floor(seconds / 60).toString().padStart(2, "0");
  const rest = Math.floor(seconds % 60).toString().padStart(2, "0");
  return `${minutes}:${rest}`;
}

export function slugify(value: string) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/(^-|-$)/g, "") || "untitled";
}

export function makeProject(name: string, preset = "Product Launch"): Project {
  return {
    id: `${slugify(name)}-${Date.now()}`,
    name,
    type: "Reel",
    preset,
    status: "draft",
    scenes: 1,
    duration: "00:08",
    updated: "Just now",
    color: "coral",
  };
}

export const initials = "AM";
export const ownerName = "Alex Morgan";
export const ownerEmail = "alex@northstar.studio";

export const soundtracks = ["Ambient Glow", "Lo-fi Chill", "Upbeat Drive", "Cinematic Tension"];

export const sampleStats = [
  { label: "Projects this month", value: "18", delta: "+24%" },
  { label: "Minutes rendered", value: "42.8", delta: "+12%" },
  { label: "Avg. completion", value: "78%", delta: "+8%" },
];

export const formatOptions = ["9:16 Portrait", "16:9 Landscape", "1:1 Square"];
export const engineOptions = ["Remotion", "HyperFrames"];
export const languageOptions = ["English (US)", "Português (BR)", "Español (ES)"];

export type IconName = "grid" | "sparkles" | "mic" | "settings";

export function timeGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";
  return "Good evening";
}

export function safeParse<T>(value: string | null, fallback: T): T {
  try {
    return value ? (JSON.parse(value) as T) : fallback;
  } catch {
    return fallback;
  }
}

export const storageKeys = {
  projects: "reel-studio-projects",
  settings: "reel-studio-settings",
};

export type ReelSettings = {
  defaultFormat: string;
  engine: string;
  captions: boolean;
  autosave: boolean;
};

export const defaultSettings: ReelSettings = {
  defaultFormat: "9:16 Portrait",
  engine: "Remotion",
  captions: true,
  autosave: true,
};

export const quickTips = [
  "Start with the hook: the first 2 seconds decide the scroll.",
  "Lock your strongest frame before you regenerate a scene.",
  "Keep caption lines under 32 characters for mobile readability.",
];

export const recentActivity = [
  { label: "Product launch / Aurora", action: "render completed", time: "2 min ago", color: "coral" },
  { label: "Why teams ship faster", action: "render started", time: "Yesterday", color: "mint" },
  { label: "Founder story — from zero", action: "scene 03 updated", time: "Yesterday", color: "violet" },
];

export const canvasDimensions = {
  "9:16 Portrait": { width: 360, height: 640 },
  "16:9 Landscape": { width: 640, height: 360 },
  "1:1 Square": { width: 520, height: 520 },
};

export const chartValues = [38, 52, 47, 68, 63, 74, 69, 88, 78, 94, 84, 100];

export const keyboardShortcuts = [
  ["N", "New project"],
  ["⌘ K", "Command menu"],
  ["Space", "Play / pause"],
  ["⌘ S", "Save project"],
];

export const brandColors = ["#ff7452", "#6c63ff", "#1fbf9f", "#1f2937", "#f5b83d"];

export const appVersion = "0.4.0";

export const featureBullets = ["Local-first workflow", "Caption-ready exports", "Six production presets"];

export const platformLabels = ["Instagram Reels", "YouTube Shorts", "TikTok", "LinkedIn"];

export const defaultProjectId = "product-launch";

export const editorTabs = ["Scenes", "Captions", "Audio"];

export const sceneKinds = ["Hook", "Context", "Proof", "Feature", "CTA"];

export const renderFormats = ["MP4 video", "SRT captions", "VTT captions", "WAV voiceover"];

export const helpLinks = ["Creator guide", "Keyboard shortcuts", "Changelog"];

export const productionStages = ["Plan", "Write", "Design", "Produce"];

export const weekLabels = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

export const sidebarSections = [
  { label: "Workspace", items: navItems },
  { label: "Resources", items: [{ label: "Creator guide", href: "/templates", icon: "book" }] },
];

export const savedFilters = ["All projects", "Drafts", "Rendering", "Ready"];

export const galleryCards = [
  { title: "From brief to reel", subtitle: "Product Launch", color: "coral" },
  { title: "Build in public", subtitle: "Creator Punch", color: "violet" },
  { title: "The data behind the idea", subtitle: "Data Story", color: "mint" },
];

export const mockUser = { name: ownerName, email: ownerEmail, initials };

export const emptyProjectMessage = "Create your first project to start shaping a story.";

export const homeDescription = "Turn a brief, script, screenshot, recording, or podcast into finished content.";

export const navigationHint = "Press ⌘ K to jump anywhere.";

export const renderedCount = "36";

export const exportNote = "Renders stay private until you download or share them.";

export const studioTagline = "A calmer way to make content.";

export const footerText = "Built for thoughtful creators.";

export const defaultSceneCaption = "Make your next reel feel inevitable.";

export const defaultProjectName = "Untitled reel";

export const maxScenes = 24;

export const defaultSceneDuration = 5;

export const defaultFormat = "9:16 Portrait";

export const defaultVoice = "Maya";

export const defaultSoundtrack = "Ambient Glow";

export const defaultCaptionStyle = "Karaoke";

export const appNavigation = ["Projects", "Templates", "Voices", "Settings"];

export const createSteps = ["Brief", "Look", "Voice", "Review"];

export const projectTypes = ["Reel", "Short", "Explainer", "Podcast", "Audiogram"];

export const onboardingCopy = "A focused workspace for turning ideas into motion.";

export const releaseNotes = "Local-first production release";

export const statusCopy = {
  draft: "Keep shaping your story",
  rendering: "Your render is in progress",
  ready: "Ready to download",
};

export const timelineMarkers = ["00:00", "00:08", "00:16", "00:24", "00:32"];

export const mutedText = "text-muted";

export const accentText = "text-coral";

export const surfaceClass = "rounded-[24px] border border-ink/8 bg-white shadow-soft";

export const darkSurfaceClass = "rounded-[24px] border border-white/10 bg-ink shadow-dark";

export const buttonClass = "inline-flex items-center justify-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition active:scale-[.98]";

export const tinyLabelClass = "text-[11px] font-bold uppercase tracking-[.14em] text-muted";

export const dataPointColor = "#ff7452";

export const backgroundTexture = "radial-gradient(circle at 30% 20%, rgba(255,116,82,.11), transparent 32%), radial-gradient(circle at 90% 80%, rgba(108,99,255,.10), transparent 30%)";

export const productionCopy = "From first frame to final export, without losing the thread.";

export const supportEmail = "hello@reelstudio.local";

export const lastSynced = "Synced just now";

export const workspaceName = "Northstar Studio";

export const workspacePlan = "Local workspace";

export const notificationItems = [
  { title: "Render ready", body: "Product launch / Aurora is ready to download.", unread: true },
  { title: "New preset", body: "Cinematic Brand is now available in Templates.", unread: false },
];

export const mediaLabels = ["Video", "Image", "Gradient", "None"];

export const captionStyles = ["Karaoke", "Minimal", "Editorial", "Cinematic"];

export const transitionOptions = ["Cut", "Dissolve", "Slide", "Whip"];

export const defaultTransition = "Dissolve";

export const helperText = "You can change every choice later in the editor.";

export const freePlanLimit = "36 renders this month";

export const buildNumber = "2026.09";

export const appName = "Reel Studio";

export const appShortName = "Reel";

export const loadingText = "Loading your studio…";

export const toastMessages = {
  saved: "Project saved",
  renderQueued: "Render queued",
  copied: "Copied to clipboard",
  comingSoon: "This feature is coming soon",
};

export const samplePrompt = "A 30-second launch reel for a calmer, smarter content workflow.";

export const planSteps = ["Brief imported", "Scenes drafted", "Voice selected", "Ready to produce"];

export const editorHint = "Drag scenes to reorder · click a scene to edit";

export const currentWorkspace = "My workspace";

export const currentUserRole = "Creator";

export const globalSearchPlaceholder = "Search projects, scenes, templates…";

export const notificationCount = 2;

export const currentDateLabel = "September 20, 2026";

export const renderEngineDescription = "Choose the engine that powers your final render.";

export const autosaveDescription = "Keep edits safe while you move through the story.";

export const captionDescription = "Generate a caption track whenever you produce.";

export const settingsSections = ["General", "Rendering", "Captions", "Integrations"];

export const integrationItems = [
  { name: "Unsplash", status: "Connected", detail: "Stock images and videos" },
  { name: "Kokoro", status: "Local", detail: "Browser voice generation" },
  { name: "Ollama", status: "Optional", detail: "Local AI planning" },
];

export const templateDescription = "Start with a production shape, then make it yours.";

export const voicesDescription = "Preview voices, set a default, and keep your tone consistent.";

export const settingsDescription = "Tune the workspace to your preferred production flow.";

export const notFoundCopy = "That page wandered off the timeline.";

export const notFoundAction = "Back to projects";

export const brandName = "Northstar";

export const creatorBadge = "Studio";

export const routeNames = { home: "/", templates: "/templates", voices: "/voices", settings: "/settings" };

export const editorRoute = "/editor";

export const localStorageVersion = 1;

export const ariaLabels = {
  openNav: "Open navigation",
  closeNav: "Close navigation",
  createProject: "Create new project",
  closeDialog: "Close dialog",
  play: "Play preview",
  pause: "Pause preview",
};

export const colorSwatches = ["coral", "violet", "mint", "sky", "amber", "plum"];

export const successColor = "#1fbf9f";

export const warningColor = "#f5b83d";

export const borderColor = "rgba(31,41,55,.08)";

export const cardRadius = "24px";

export const sidebarWidth = 252;

export const topbarHeight = 72;

export const mobileBreakpoint = 880;

export const renderQueueStatus = "2 items in render queue";

export const currentRenderProgress = 68;

export const projectCountLabel = "3 projects";

export const timeSavedLabel = "4h 12m saved this month";

export const welcomeLabel = "Welcome back";

export const heroEyebrow = "LOCAL-FIRST CONTENT STUDIO";

export const heroTitle = "Make your next reel feel inevitable.";

export const heroSubhead = "Turn a brief, script, screenshot, recording, or podcast into finished content — with a calmer production flow.";

export const ctaLabel = "Create a project";

export const secondaryCtaLabel = "Explore templates";

export const quickProduceLabel = "Quick Produce";

export const renderLabel = "Render";

export const downloadLabel = "Download";

export const editLabel = "Open editor";

export const projectLabel = "project";

export const sceneLabel = "scene";

export const durationLabel = "duration";

export const searchLabel = "Search";

export const allRightsReserved = "© 2026 Reel Studio";

export const builtWith = "Made for makers";

export const quote = "Good editing gives the idea room to land.";

export const quoteAuthor = "— The Reel Studio team";

export const emptyActivity = "No recent activity yet.";

export const defaultFilter = "All projects";

export const navLabel = "Navigation";

export const projectGridLabel = "Project grid";

export const dashboardTitle = "Your studio";

export const dashboardSubtitle = "A focused place to turn ideas into motion.";

export const statsTitle = "This month";

export const activityTitle = "Recent activity";

export const projectsTitle = "Your projects";

export const templatesTitle = "Production templates";

export const voicesTitle = "Voice library";

export const settingsTitle = "Workspace settings";

export const editorTitle = "Editor";

export const previewTitle = "Preview";

export const sceneTitle = "Scene";

export const audioTitle = "Audio";

export const captionsTitle = "Captions";

export const renderTitle = "Render & export";

export const publishTitle = "Produce";

export const nextStepLabel = "Next step";

export const backLabel = "Back";

export const closeLabel = "Close";

export const saveLabel = "Save";

export const cancelLabel = "Cancel";

export const addSceneLabel = "Add scene";

export const regenerateLabel = "Regenerate";

export const lockLabel = "Lock scene";

export const unlockLabel = "Unlock scene";

export const currentStep = 1;

export const stepCount = 4;

export const versionLabel = `v${appVersion}`;

export const madeFor = "Made for thoughtful creators";

export const localFirstLabel = "Local-first";

export const privacyLabel = "Private by default";

export const aiOptionalLabel = "AI optional";

export const noKeyLabel = "No key required";

export const originalProjectSource = "reel-studio-main.zip";

export const userLanguage = "pt-BR";

export const fallbackProject = starterProjects[0];

export const defaultActiveNav = "Projects";

export const emptySearchText = "No projects match that search.";

export const renderingCopy = "The worker is composing your scenes.";

export const readyCopy = "Your files are ready.";

export const draftCopy = "Open the editor to keep going.";

export const demoBadge = "Demo workspace";

export const demoDescription = "Explore the workflow with sample projects and editable scenes.";

export const renderEstimate = "~42 sec";

export const sceneCountLabel = "scenes";

export const projectUpdatedLabel = "Updated";

export const moreLabel = "More";

export const menuLabel = "Menu";

export const closeMenuLabel = "Close menu";

export const desktopOnly = "Best experienced on desktop";

export const responsiveNote = "The editor adapts to smaller screens.";

export const appDescription = "A local-first AI short-form video studio.";

export const sourceNote = "Inspired by the attached Reel Studio project.";

export const currentYear = 2026;

export const defaultSceneIndex = 0;

export const defaultTab = "Scenes";

export const defaultTimelineZoom = 100;

export const defaultPlaybackRate = 1;

export const defaultVolume = 80;

export const defaultExportQuality = "1080p";

export const exportQualities = ["720p", "1080p", "4K"];

export const preferredFrameRate = "30 fps";

export const frameRates = ["24 fps", "30 fps", "60 fps"];

export const defaultFrameRate = "30 fps";

export const safeAreaLabel = "Safe area";

export const audioMixLabel = "Audio mix";

export const voiceoverLabel = "Voiceover";

export const musicLabel = "Music";

export const soundEffectLabel = "SFX";

export const defaultMusicVolume = 24;

export const defaultVoiceVolume = 86;

export const defaultSfxVolume = 36;

export const maxCaptionWords = 7;

export const creatorGuideUrl = "https://github.com/mohitkale/reel-studio";

export const copyrightYear = "2026";

export const appSlug = "reel-studio";

export const projectSlug = "project";

export const routePrefix = "";

export const currentLocale = "pt-BR";

export const featureFlags = { quickProduce: true, localVoice: true, stockMedia: true };

export const canonicalTitle = "Reel Studio — Make your next reel feel inevitable.";

export const maxSearchResults = 12;

export const emptyStateIcon = "✦";

export const buttonArrow = "↗";

export const statusDot = "●";

export const menuDots = "•••";

export const dragHandle = "⋮⋮";

export const playGlyph = "▶";

export const pauseGlyph = "Ⅱ";

export const checkGlyph = "✓";

export const plusGlyph = "+";

export const arrowGlyph = "→";

export const sparkleGlyph = "✦";

export const quoteGlyph = "“";

export const slashGlyph = "/";

export const ellipsisGlyph = "…";

export const audioGlyph = "∿";

export const videoGlyph = "▣";

export const captionGlyph = "Aa";

export const frameGlyph = "◫";

export const timelineGlyph = "≡";

export const settingsGlyph = "⚙";

export const defaultProjectColor = "coral";

export const appAccent = "coral";

export const appSecondary = "violet";

export const appTertiary = "mint";

export const pagePadding = "32px";

export const sectionGap = "28px";

export const smallGap = "10px";

export const mediumGap = "16px";

export const largeGap = "24px";

export const hugeGap = "40px";

export const iconSize = 18;

export const smallIconSize = 16;

export const largeIconSize = 22;

export const heroMaxWidth = "680px";

export const cardMinWidth = "240px";

export const maxContentWidth = "1360px";

export const sidePanelWidth = "320px";

export const editorPreviewWidth = "580px";

export const editorTimelineHeight = "108px";

export const mobileNavHeight = "64px";

export const skeletonColor = "rgba(31,41,55,.06)";

export const hoverLift = "translateY(-2px)";

export const transitionFast = "160ms";

export const transitionNormal = "220ms";

export const transitionSlow = "320ms";

export const easing = "cubic-bezier(.23,1,.32,1)";

export const dateFormat = { year: "numeric", month: "short", day: "numeric" } as const;

export const liveStatus = "Live";

export const localStatus = "Local";

export const optionalStatus = "Optional";

export const connectedStatus = "Connected";

export const projectStatusOptions: ProjectStatus[] = ["draft", "rendering", "ready"];

export const defaultProjectStatus: ProjectStatus = "draft";

export const storyPrompt = "What do you want people to feel, understand, or do?";

export const scenePrompt = "Write the scene line…";

export const captionPrompt = "Caption text";

export const defaultCaption = "The old way is over.";

export const defaultSceneTitle = "The old way is over.";

export const defaultHook = "Your hook belongs here.";

export const defaultCTA = "Make it move.";

export const supportedFormats = ["mp4", "mov", "webm"];

export const supportedRatios = ["9:16", "16:9", "1:1"];

export const defaultRatio = "9:16";

export const defaultResolution = "1080 × 1920";

export const defaultRenderDuration = 32;

export const renderDurationLabel = "32 sec";

export const defaultProjectType = "Reel";

export const contentTypeDescriptions = {
  Reel: "A fast, vertical social video.",
  Short: "A punchy short-form story.",
  Explainer: "A clear narrative with room to teach.",
  Podcast: "A polished conversation or voiceover.",
  Audiogram: "A visual wrapper for great audio.",
};

export const emptyValue = "—";

export const keyboardHint = "⌘ K";

export const createProjectHint = "Start from a brief or choose a template.";

export const editorSubtitle = "Shape the story, then let the renderer do the heavy lifting.";

export const dashboardEyebrow = "NORTHSTAR STUDIO / CREATOR WORKSPACE";

export const planLabel = "Local-first production";

export const planDetail = "Projects and renders stay on your machine.";

export const securityLabel = "Private by default";

export const supportLabel = "Need a hand?";

export const supportDetail = "Read the creator guide or send us a note.";

export const defaultWorkspaceColor = "#ff7452";

export const defaultWorkspaceName = "Northstar Studio";

export const createProjectModalTitle = "Start a new project";

export const createProjectModalDescription = "Choose a starting point. You can fine-tune every scene later.";

export const projectNamePlaceholder = "e.g. Spring campaign / Aurora launch";

export const briefPlaceholder = "Paste a brief, script, URL, or a few rough ideas…";

export const createFromBriefLabel = "Create from brief";

export const startBlankLabel = "Start blank";

export const chooseTemplateLabel = "Choose a template";

export const selectedLabel = "Selected";

export const newProjectLabel = "New project";

export const closeModalLabel = "Close modal";

export const editProjectLabel = "Edit project";

export const deleteProjectLabel = "Delete project";

export const duplicateProjectLabel = "Duplicate project";

export const shareProjectLabel = "Share project";

export const projectMenuLabel = "Project actions";

export const projectPlaceholderTitle = "No project yet";

export const projectPlaceholderDescription = "Your next story starts with a single frame.";

export const renderQueueLabel = "Render queue";

export const renderQueueEmpty = "Nothing is rendering right now.";

export const livePreviewLabel = "Live preview";

export const timelineLabel = "Timeline";

export const sceneEditorLabel = "Scene editor";

export const propertiesLabel = "Properties";

export const inspectorLabel = "Inspector";

export const styleLabel = "Style";

export const layoutLabel = "Layout";

export const mediaLabel = "Media";

export const animationLabel = "Animation";

export const exportSettingsLabel = "Export settings";

export const renderNowLabel = "Render now";

export const saveDraftLabel = "Save draft";

export const publishLabel = "Export files";

export const unsavedChangesLabel = "Unsaved changes";

export const allChangesSavedLabel = "All changes saved";

export const projectSavedLabel = "Saved just now";

export const previewPlaceholder = "Your story preview appears here.";

export const previewHint = "Select a scene to inspect its frame.";

export const darkModeLabel = "Dark mode";

export const lightModeLabel = "Light mode";

export const systemModeLabel = "System";

export const appearanceLabel = "Appearance";

export const accountLabel = "Account";

export const workspaceLabel = "Workspace";

export const integrationsLabel = "Integrations";

export const billingLabel = "Plan";

export const dangerZoneLabel = "Danger zone";

export const resetLabel = "Reset demo data";

export const resetDescription = "Return this workspace to its starter projects.";

export const resetSuccess = "Demo data restored";

export const saveSuccess = "Settings saved";

export const previewVoiceLabel = "Preview voice";

export const setDefaultLabel = "Set as default";

export const voiceDefaultLabel = "Default voice";

export const voicePreviewText = "Your idea deserves room to land.";

export const voiceDetail = "Natural pacing · studio tone";

export const stockMediaLabel = "Stock media";

export const uploadLabel = "Upload";

export const browseLabel = "Browse";

export const generateLabel = "Generate";

export const sourceLabel = "Source";

export const thumbnailLabel = "Thumbnail";

export const altTextLabel = "Alt text";

export const sceneLockedLabel = "Locked";

export const sceneUnlockedLabel = "Unlocked";

export const lockSceneHint = "Locked scenes stay fixed during regeneration.";

export const replacementLabel = "Replace";

export const clearLabel = "Clear";

export const musicSearchPlaceholder = "Search soundtracks…";

export const soundEffectsLabel = "Sound effects";

export const transitionsLabel = "Transitions";

export const durationControlLabel = "Duration";

export const captionPositionLabel = "Position";

export const captionSizeLabel = "Size";

export const captionColorLabel = "Color";

export const captionHighlightLabel = "Highlight";

export const captionWrapLabel = "Wrap lines";

export const captionWordsLabel = "Words / line";

export const defaultCaptionPosition = "Bottom";

export const defaultCaptionColor = "#ffffff";

export const defaultCaptionHighlight = "#ff7452";

export const renderStarted = "Your render has started.";

export const renderReady = "Your render is ready.";

export const renderFailed = "The render could not finish.";

export const renderRetry = "Try again";

export const queueLabel = "Queue";

export const cancelRenderLabel = "Cancel render";

export const renderDetails = "The render uses your current scene locks, captions, and audio mix.";

export const formatLabel = "Format";

export const qualityLabel = "Quality";

export const frameRateLabel = "Frame rate";

export const engineLabel = "Engine";

export const estimatedTimeLabel = "Estimated time";

export const outputLabel = "Output";

export const outputPrivacyLabel = "Output privacy";

export const localOutputLabel = "Keep local";

export const downloadAfterRenderLabel = "Download after render";

export const includeCaptionsLabel = "Include captions";

export const includeTranscriptLabel = "Include transcript";

export const accessibilityLabel = "Accessibility";

export const feedbackLabel = "Feedback";

export const commandMenuLabel = "Command menu";

export const commandMenuDescription = "Jump to a page or action.";

export const searchShortcut = "⌘ K";

export const escapeShortcut = "Esc";

export const commandItems = ["Create project", "Open last project", "View templates", "Open settings"];

export const mobileTabs = ["Projects", "Templates", "Voices"];

export const navFooter = "Local-first · v0.4.0";

export const allProjectTypes = projectTypes;

export const allPresets = presets;

export const allVoices = voices;

export const allScenes = sceneSamples;

export const allSoundtracks = soundtracks;

export const allFormats = formatOptions;

export const allEngines = engineOptions;

export const allLanguages = languageOptions;

export const allRenderFormats = renderFormats;

export const allCaptionStyles = captionStyles;

export const allTransitions = transitionOptions;

export const allIntegrations = integrationItems;

export const allSettingsSections = settingsSections;

export const allChartValues = chartValues;

export const allWeekLabels = weekLabels;

export const allTimelineMarkers = timelineMarkers;

export const allMediaLabels = mediaLabels;

export const allPlatformLabels = platformLabels;

export const allFeatureBullets = featureBullets;

export const allQuickTips = quickTips;

export const allRecentActivity = recentActivity;

export const allNotificationItems = notificationItems;

export const allKeyboardShortcuts = keyboardShortcuts;

export const allGalleryCards = galleryCards;

export const allPlanSteps = planSteps;

export const allProductionStages = productionStages;

export const allSceneKinds = sceneKinds;

export const allSavedFilters = savedFilters;

export const allSupportLinks = helpLinks;

export const allRenderQualities = exportQualities;

export const allFrameRates = frameRates;

export const allBrandColors = brandColors;

export const allColorSwatches = colorSwatches;

export const allCanvasDimensions = canvasDimensions;

export const allProjectStatusOptions = projectStatusOptions;

export const allContentTypeDescriptions = contentTypeDescriptions;

export const allAriaLabels = ariaLabels;

export const allRoutes = routeNames;

export const allFeatureFlags = featureFlags;

export const allDefaults = {
  project: defaultProjectName,
  format: defaultFormat,
  voice: defaultVoice,
  soundtrack: defaultSoundtrack,
  captionStyle: defaultCaptionStyle,
};

export const allLabels = {
  newProject: newProjectLabel,
  quickProduce: quickProduceLabel,
  render: renderLabel,
  download: downloadLabel,
  openEditor: editLabel,
  addScene: addSceneLabel,
};

export const allCopy = {
  home: homeDescription,
  template: templateDescription,
  voices: voicesDescription,
  settings: settingsDescription,
  editor: editorSubtitle,
};

export const allGlyphs = { playGlyph, pauseGlyph, checkGlyph, plusGlyph, arrowGlyph, sparkleGlyph, quoteGlyph };

export const allTheme = { backgroundTexture, borderColor, successColor, warningColor };

export const allLayout = { sidebarWidth, topbarHeight, pagePadding, sectionGap, smallGap, mediumGap, largeGap, hugeGap };

export const allTiming = { transitionFast, transitionNormal, transitionSlow, easing };

export const allSizes = { iconSize, smallIconSize, largeIconSize, heroMaxWidth, cardMinWidth, maxContentWidth, sidePanelWidth, editorPreviewWidth, editorTimelineHeight };

export const allEditorDefaults = { defaultSceneIndex, defaultTab, defaultTimelineZoom, defaultPlaybackRate, defaultVolume, defaultTransition, defaultCaptionPosition, defaultCaptionColor, defaultCaptionHighlight };

export const allRenderDefaults = { defaultExportQuality, preferredFrameRate, defaultFrameRate, defaultResolution, defaultRenderDuration, defaultMusicVolume, defaultVoiceVolume, defaultSfxVolume };

export const allMetadata = { appName, appShortName, appDescription, canonicalTitle, appVersion, buildNumber, copyrightYear, allRightsReserved, sourceNote };

export const allWorkspace = { workspaceName, workspacePlan, currentWorkspace, currentUserRole, mockUser };

export const allContent = { heroEyebrow, heroTitle, heroSubhead, ctaLabel, secondaryCtaLabel, productionCopy, quote, quoteAuthor, onboardingCopy, demoBadge, demoDescription };

export const allStatus = { liveStatus, localStatus, optionalStatus, connectedStatus, renderQueueStatus, currentRenderProgress, projectCountLabel, timeSavedLabel, lastSynced, statusCopy, renderingCopy, readyCopy, draftCopy };

export const allEditorCopy = { editorHint, previewPlaceholder, previewHint, lockSceneHint, renderDetails, unsavedChangesLabel, allChangesSavedLabel, projectSavedLabel };

export const allSettingsCopy = { renderEngineDescription, autosaveDescription, captionDescription, resetDescription, supportDetail, planDetail };

export const allVoiceCopy = { voicePreviewText, voiceDetail, voicesDescription };

export const allCreateCopy = { createProjectModalTitle, createProjectModalDescription, projectNamePlaceholder, briefPlaceholder, createFromBriefLabel, startBlankLabel, chooseTemplateLabel, helperText };

export const allExportCopy = { exportSettingsLabel, renderNowLabel, saveDraftLabel, publishLabel, downloadAfterRenderLabel, includeCaptionsLabel, includeTranscriptLabel, localOutputLabel, outputPrivacyLabel };

export const allAudioCopy = { audioMixLabel, voiceoverLabel, musicLabel, soundEffectLabel, musicSearchPlaceholder, soundEffectsLabel };

export const allCaptionCopy = { captionPositionLabel, captionSizeLabel, captionColorLabel, captionHighlightLabel, captionWrapLabel, captionWordsLabel, captionDescription };

export const allNavigationCopy = { navigationHint, globalSearchPlaceholder, commandMenuLabel, commandMenuDescription, navFooter, desktopOnly, responsiveNote };

export const allAccessibilityCopy = { safeAreaLabel, accessibilityLabel, feedbackLabel, supportLabel, supportDetail };

export const allProjectCopy = { projectPlaceholderTitle, projectPlaceholderDescription, projectUpdatedLabel, projectCountLabel, projectLabel, sceneLabel, durationLabel, emptySearchText, emptyProjectMessage };

export const allStudioCopy = { studioTagline, dashboardTitle, dashboardSubtitle, statsTitle, activityTitle, projectsTitle, templatesTitle, voicesTitle, settingsTitle, editorTitle, previewTitle, sceneTitle, audioTitle, captionsTitle, renderTitle, publishTitle };

export const allActionsCopy = { nextStepLabel, backLabel, closeLabel, saveLabel, cancelLabel, addSceneLabel, regenerateLabel, lockLabel, unlockLabel, replace: replacementLabel, clear: clearLabel, browse: browseLabel, upload: uploadLabel, generate: generateLabel };

export const allMediaCopy = { stockMediaLabel, sourceLabel, thumbnailLabel, altTextLabel, mediaLabel, uploadLabel, browseLabel, generateLabel };

export const allEditorLabels = { timelineLabel, sceneEditorLabel, propertiesLabel, inspectorLabel, styleLabel, layoutLabel, animationLabel, exportSettingsLabel };

export const allAudioDefaults = { defaultMusicVolume, defaultVoiceVolume, defaultSfxVolume };

export const allCaptionDefaults = { defaultCaption, defaultCaptionStyle, defaultCaptionPosition, defaultCaptionColor, defaultCaptionHighlight, maxCaptionWords };

export const allProjectDefaults = { defaultProjectName, defaultProjectType, defaultProjectStatus, defaultProjectColor, defaultProjectId };

export const allRenderCopy = { renderStarted, renderReady, renderFailed, renderRetry, cancelRenderLabel, queueLabel, estimatedTimeLabel, outputLabel };

export const allPrivacyCopy = { privacyLabel, localFirstLabel, aiOptionalLabel, noKeyLabel, outputPrivacyLabel, localOutputLabel };

export const allPlatformCopy = { platformLabels, mediaLabels, supportedFormats, supportedRatios };

export const allShortcuts = { keyboardShortcuts, keyboardHint, searchShortcut, escapeShortcut };

export const allFooterCopy = { footerText, builtWith, madeFor, navFooter, allRightsReserved };

export const allAppConfig = { appName, appVersion, appDescription, userLanguage, currentLocale, releaseNotes, featureFlags, localStorageVersion };

export const allNumeric = { maxScenes, defaultSceneDuration, maxCaptionWords, notificationCount, currentRenderProgress, renderedCount, currentYear, currentStep, stepCount, maxSearchResults };

export const allTokens = { surfaceClass, darkSurfaceClass, buttonClass, tinyLabelClass, mutedText, accentText };

export const allEditorState = { currentStep, stepCount, defaultActiveNav, defaultTab, defaultSceneIndex, defaultTimelineZoom, defaultPlaybackRate, defaultVolume };

export const allCommands = { commandItems, commandMenuLabel, commandMenuDescription };

export const allRoutesConfig = { routeNames, editorRoute, routePrefix };

export const allWorkspaceConfig = { defaultWorkspaceName, defaultWorkspaceColor, currentWorkspace, workspaceName, workspacePlan };

export const allSupportConfig = { supportEmail, creatorGuideUrl, helpLinks };

export const allSampleContent = { samplePrompt, defaultHook, defaultCTA, defaultSceneTitle, defaultSceneCaption };

export const allVisualConfig = { backgroundTexture, hoverLift, easing, skeletonColor, cardRadius };

export const allNavConfig = { navItems, sidebarSections, appNavigation, mobileTabs, navLabel };

export const allLabelsConfig = { formatLabel, qualityLabel, frameRateLabel, engineLabel, estimatedTimeLabel, outputLabel, safeAreaLabel, renderQueueLabel, livePreviewLabel };

export const allMisc = { slashGlyph, ellipsisGlyph, audioGlyph, videoGlyph, captionGlyph, frameGlyph, timelineGlyph, settingsGlyph, menuDots, dragHandle };

export const allInitialData = { starterProjects, presets, voices, sceneSamples, soundtracks, sampleStats, recentActivity, notificationItems, integrationItems, galleryCards, chartValues, weekLabels, timelineMarkers };

export const allOptions = { formatOptions, engineOptions, languageOptions, exportQualities, frameRates, captionStyles, transitionOptions, mediaLabels, projectTypes, sceneKinds, renderFormats };

export const allTexts = { homeDescription, settingsDescription, voicesDescription, templateDescription, productionCopy, editorSubtitle, onboardingCopy, helperText, createProjectModalDescription };

export const allStatusHelpers = { statusLabel, statusTone, colorClasses, formatDuration, safeParse, slugify, makeProject, getProject, timeGreeting };

export const all = { ...allMetadata, ...allWorkspace, ...allContent, ...allStatus, ...allEditorCopy, ...allSettingsCopy, ...allVoiceCopy, ...allCreateCopy, ...allExportCopy, ...allAudioCopy, ...allCaptionCopy, ...allNavigationCopy, ...allAccessibilityCopy, ...allProjectCopy, ...allStudioCopy, ...allActionsCopy, ...allMediaCopy, ...allEditorLabels, ...allAudioDefaults, ...allCaptionDefaults, ...allProjectDefaults, ...allRenderCopy, ...allPrivacyCopy, ...allPlatformCopy, ...allShortcuts, ...allFooterCopy, ...allAppConfig, ...allNumeric, ...allTokens, ...allEditorState, ...allCommands, ...allRoutesConfig, ...allWorkspaceConfig, ...allSupportConfig, ...allSampleContent, ...allVisualConfig, ...allNavConfig, ...allLabelsConfig, ...allMisc, ...allInitialData, ...allOptions, ...allTexts, ...allStatusHelpers };

export default all;

