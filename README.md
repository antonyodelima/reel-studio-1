# Reel Studio (Android)

Um estúdio local-first para criação, edição e renderização de vídeos curtos (Reels, Shorts, TikTok, LinkedIn), desenvolvido nativamente em **Android** com **Jetpack Compose**, **Material 3** e persistência **Room**.

## Recursos Principais

- **Espaço de Trabalho & Projetos**:
  - Painel com métricas de produção mensal, fila de renderização e atividade recente.
  - Filtro e busca de projetos por status (Rascunho, Renderizando, Pronto).
  - Criação rápida de projetos com presets profissionais (Lançamento de produto, Impacto do criador, Explicador editorial, História com dados, Demo para desenvolvedores, Marca cinematográfica).

- **Editor Interativo & Storyboard**:
  - Prévia ao vivo do Reel em formato vertical 9:16 com reprodução contínua e automatizada das cenas.
  - Linha do tempo scrubbable com blocos de cena numerados e controle de duração.
  - Storyboard para adicionar, reordenar e excluir cenas.
  - Inspector completo com 3 abas:
    - **Cenas**: edição de texto e legendas, tratamento de mídia (Gradient, Video, Image, None), estilos de legenda e transições.
    - **Legendas**: faixas de legendas automatizadas e guias de área segura.
    - **Áudio**: narração com vozes Cartesia Sonic, geração de narração, seleção de trilha musical e controle de mixagem de volume.
  - Modal de produção e renderização com múltiplos formatos (9:16, 16:9, 1:1), qualidades (720p, 1080p, 4K) e motores (Remotion, FFmpeg, HyperFrames).

- **Biblioteca de Templates**:
  - Presets prontos com estruturas de 5 cenas otimizadas para retenção visual.
  - Galeria de templates para início rápido a partir de briefs.

- **Vozes & Clonagem**:
  - Integração com vozes Cartesia Sonic em diversos idiomas (Português BR, Inglês EUA/UK, Espanhol).
  - Prévia auditiva em tempo real com equalizador animado.
  - Estúdio de gravação de voz proprietária com microfone ao vivo (10s a 60s) e consentimento ético.

- **Configurações & Governança**:
  - Preferências salvas localmente via Room Database.
  - Configurações gerais, motores de renderização, legendagem e integrações (Cartesia, Unsplash, Ollama).
  - Opção para restaurar dados de demonstração a qualquer momento.

## Tecnologias

- Kotlin & Jetpack Compose
- Material Design 3
- Room Database (Local-first)
- Coroutines & Flow
- Navigation Compose
