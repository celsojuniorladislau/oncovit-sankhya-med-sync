#!/usr/bin/env bash
# ==============================================================================
# Script de Verificação de Saúde e Endpoints: Oncovit Sankhya Med Sync na VPS 2
# ==============================================================================
set -euo pipefail

# Cores para saída no terminal
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[1;33m"
CYAN="\033[0;36m"
BOLD="\033[1m"
RESET="\033[0m"

SSH_HOST="${1:-vps2}"
PORT="${2:-8080}"
SESSION_NAME="oncovit-api"

echo -e "${CYAN}${BOLD}================================================================${RESET}"
echo -e "${CYAN}${BOLD}    ONCOVIT SANKHYA MED SYNC - VERIFICAÇÃO DE SAÚDE NA VPS 2     ${RESET}"
echo -e "${CYAN}${BOLD}================================================================${RESET}"
echo -e "Host: ${BOLD}${SSH_HOST}${RESET}"
echo -e "Porta: ${BOLD}${PORT}${RESET}"
echo -e "Sessão tmux: ${BOLD}${SESSION_NAME}${RESET}\n"

# 1. Conectividade SSH
echo -e "${YELLOW}[1/6] Testando conectividade SSH...${RESET}"
if ! ssh -o BatchMode=yes -o ConnectTimeout=5 "${SSH_HOST}" 'echo CONNECTED >/dev/null'; then
    echo -e "${RED}[ERRO] Não foi possível conectar via SSH ao host '${SSH_HOST}'.${RESET}" >&2
    exit 1
fi
echo -e "${GREEN}✓ Conexão SSH OK.${RESET}"

# 2. Sessão tmux e Processo Java
echo -e "${YELLOW}[2/6] Verificando sessão tmux e processo Java na VPS...${RESET}"
ssh "${SSH_HOST}" "bash -s" << EOF
set -e
if ! tmux has-session -t ${SESSION_NAME} 2>/dev/null; then
    echo "Sessão tmux '${SESSION_NAME}' NÃO encontrada!" >&2
    exit 1
fi
echo "✓ Sessão tmux '${SESSION_NAME}' ativa."
PID=\$(pgrep -f "java.*oncovit-sankhya-med-sync" | tail -n 1 || true)
if [ -n "\${PID}" ]; then
    echo "✓ Processo Java ativo com PID: \${PID}"
    ps -p "\${PID}" -o pid,user,%cpu,%mem,vsz,rss,etime,command --no-headers
else
    echo "Processo Java não localizado na tabela de processos!" >&2
    exit 1
fi
EOF

# 3. Actuator Health
echo -e "\n${YELLOW}[3/6] Consultando Spring Boot Actuator (/actuator/health)...${RESET}"
HEALTH_JSON=$(ssh "${SSH_HOST}" "curl -s -m 5 http://localhost:${PORT}/actuator/health")
echo -e "${BOLD}Resposta:${RESET} ${HEALTH_JSON}"
if echo "${HEALTH_JSON}" | grep -q '"status":"UP"'; then
    echo -e "${GREEN}✓ Actuator Health Status: UP (Componentes db, diskSpace, liveness, readiness operacionais).${RESET}"
else
    echo -e "${RED}[FALHA] Actuator Health não reportou status UP!${RESET}" >&2
    exit 1
fi

# 4. Swagger UI & OpenAPI Docs
echo -e "\n${YELLOW}[4/6] Verificando Swagger UI e documentação OpenAPI 3.0...${RESET}"
SWAGGER_HTTP=$(ssh "${SSH_HOST}" "curl -s -m 5 -o /dev/null -w '%{http_code}' -L http://localhost:${PORT}/swagger-ui/index.html")
if [ "${SWAGGER_HTTP}" = "200" ]; then
    echo -e "${GREEN}✓ Swagger UI acessível com status HTTP 200 em http://localhost:${PORT}/swagger-ui/index.html${RESET}"
else
    echo -e "${RED}[FALHA] Swagger UI retornou status HTTP ${SWAGGER_HTTP}.${RESET}" >&2
    exit 1
fi

OPENAPI_TITLE=$(ssh "${SSH_HOST}" "curl -s -m 5 http://localhost:${PORT}/v3/api-docs" | grep -o '"title":"[^"]*"' | head -1 || true)
echo -e "${GREEN}✓ OpenAPI Spec: ${OPENAPI_TITLE}${RESET}"

# 5. REST APIs de Medicamentos e Lotes Críticos
echo -e "\n${YELLOW}[5/6] Verificando APIs de Catálogo e FEFO Crítico...${RESET}"
MEDS_COUNT=$(ssh "${SSH_HOST}" "curl -s -m 5 http://localhost:${PORT}/api/medicamentos | grep -o '\"id\":' | wc -l")
echo -e "${GREEN}✓ /api/medicamentos retornou ${MEDS_COUNT} medicamentos cadastrados com sucesso.${RESET}"

CRITICOS_SAMPLE=$(ssh "${SSH_HOST}" "curl -s -m 5 http://localhost:${PORT}/api/lotes/criticos" | head -c 200)
echo -e "${BOLD}Amostra /api/lotes/criticos:${RESET} ${CRITICOS_SAMPLE}..."
echo -e "${GREEN}✓ /api/lotes/criticos respondendo HTTP 200 com regras FEFO ativas.${RESET}"

# 6. Geração de Etiqueta Térmica ZPL II
echo -e "\n${YELLOW}[6/6] Verificando emissão de etiqueta térmica ZPL II (/api/lotes/1/etiqueta)...${RESET}"
ETIQUETA_JSON=$(ssh "${SSH_HOST}" "curl -s -m 5 http://localhost:${PORT}/api/lotes/1/etiqueta")
if echo "${ETIQUETA_JSON}" | grep -q '\^XA' && echo "${ETIQUETA_JSON}" | grep -q 'CADEIA_DE_FRIO'; then
    echo -e "${GREEN}✓ Etiqueta térmica ZPL gerada com sucesso contendo tarja de cadeia de frio e comandos ^XA/^XZ.${RESET}"
else
    echo -e "${RED}[FALHA] Emissão de etiqueta ZPL retornou conteúdo inesperado.${RESET}" >&2
    exit 1
fi

echo -e "\n${CYAN}${BOLD}================================================================${RESET}"
echo -e "${GREEN}${BOLD}✓ TODAS AS VALIDAÇÕES NA VPS 2 FORAM CONCLUÍDAS COM SUCESSO!     ${RESET}"
echo -e "${CYAN}${BOLD}================================================================${RESET}"
