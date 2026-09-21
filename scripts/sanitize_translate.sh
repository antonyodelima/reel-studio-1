#!/usr/bin/env bash
set -euo pipefail
sed -i -E '/^[[:space:]]+"[^" ]+": "[^"]*",$/d' scripts/translate_ui.py
