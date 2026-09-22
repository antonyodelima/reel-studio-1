# Referências Cartesia TTS

- https://docs.cartesia.ai/use-the-api/api-conventions — base URL `https://api.cartesia.ai`, autenticação server-side com `Authorization: Bearer <api_key>`, header `Cartesia-Version`.
- https://docs.cartesia.ai/api-reference/voices/list — endpoint de listagem de vozes, filtros de idioma e expansão de `preview_file_url`.
- https://docs.cartesia.ai/api-reference/tts/bytes — endpoint de síntese por bytes, resposta em arquivo e formatos WAV/MP3/RAW.
- https://docs.cartesia.ai/examples/tts-generate-to-file — exemplo TypeScript com `sonic-3.6`, saída WAV e `sample_rate: 44100`.
- https://github.com/cartesia-ai/cartesia-js — SDK oficial JavaScript/TypeScript, consultado para validar os parâmetros do cliente.

A implementação usa a API HTTP oficial no backend, mantém `CARTESIA_API_KEY` exclusivamente como segredo do servidor, salva WAVs no storage do projeto e persiste metadados na tabela `scenes`.
