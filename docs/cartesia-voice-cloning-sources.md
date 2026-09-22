# Referências oficiais da clonagem Cartesia

A implementação usa o endpoint oficial de clone de voz: https://docs.cartesia.ai/api-reference/voices/clone. Ele recebe `multipart/form-data`, com o campo obrigatório `clip`, aceita WAV, MP3, OGG, FLAC e WEBM, e limita o upload a 16 MB. O idioma é normalizado para ISO 639-1, como `pt`.

O guia de qualidade da amostra está em https://docs.cartesia.ai/build-with-cartesia/capability-guides/clone-voices. Ele recomenda uma gravação de 10 a 60 segundos, uma única pessoa, ambiente silencioso e sem música ou eco. O app exige confirmação explícita de autorização antes do envio.

A remoção da voz usa o endpoint oficial https://docs.cartesia.ai/api-reference/voices/delete. Clones são privados por padrão e a chave Cartesia permanece exclusivamente no backend.
