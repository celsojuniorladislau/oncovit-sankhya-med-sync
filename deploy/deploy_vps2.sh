#!/usr/bin/env bash
# ==============================================================================
# Script de Deploy Automatizado: Oncovit Sankhya Med Sync na VPS 2 (Hetzner)
# Execução da aplicação em sessão persistente do tmux (oncovit-api)
# ==============================================================================
set -euo pipefail

# Definições de Cores
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[1;33m"
CYAN="\033[0;36m"
BOLD="\033[1m"
RESET="\033[0m"

SSH_HOST="${1:-vps2}"
REMOTE_DIR="/root/projetos/oncovit-sankhya-med-sync"
SESSION_NAME="oncovit-api"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
JAR_LOCAL="${PROJECT_ROOT}/oncovit-sankhya-med-sync/target/oncovit-sankhya-med-sync-1.0.0.jar"

echo -e "${CYAN}${BOLD}================================================================${RESET}"
echo -e "${CYAN}${BOLD}    ONCOVIT SANKHYA MED SYNC - DEPLOY NA VPS 2 (TMUX)           ${RESET}"
echo -e "${CYAN}${BOLD}================================================================${RESET}"
echo -e "Host Remoto: ${BOLD}${SSH_HOST}${RESET}"
echo -e "Diretório Remoto: ${BOLD}${REMOTE_DIR}${RESET}"
echo -e "Sessão tmux: ${BOLD}${SESSION_NAME}${RESET}\n"

# 1. Verificar conectividade SSH
echo -e "${YELLOW}[1/6] Testando conectividade SSH com ${SSH_HOST}...${RESET}"
if ! ssh -o BatchMode=yes -o ConnectTimeout=8 "${SSH_HOST}" 'echo CONNECTED >/dev/null'; then
    echo -e "${RED}[ERRO] Falha ao conectar via SSH ao host '${SSH_HOST}'. Verifique ~/.ssh/config e chaves.${RESET}" >&2
    exit 1
fi
echo -e "${GREEN}✓ Conexão SSH estabelecida com sucesso.${RESET}"

# 2. Verificar pré-requisitos na VPS (Java 17, tmux, UFW)
echo -e "${YELLOW}[2/6] Verificando pré-requisitos no host remoto...${RESET}"
ssh "${SSH_HOST}" 'bash -s' << 'EOF'
set -e
if ! command -v java >/dev/null 2>&1; then
    echo "Instalando OpenJDK 17..."
    export DEBIAN_FRONTEND=noninteractive
    apt-get update -qq && apt-get install -y -qq openjdk-17-jre-headless
fi
if ! command -v tmux >/dev/null 2>&1; then
    echo "Instalando tmux..."
    export DEBIAN_FRONTEND=noninteractive
    apt-get update -qq && apt-get install -y -qq tmux
fi
if command -v ufw >/dev/null 2>&1; then
    ufw allow 8080/tcp >/dev/null 2>&1 || true
fi
EOF
echo -e "${GREEN}✓ Pré-requisitos remotos verificados (Java 17, tmux, UFW 8080/tcp).${RESET}"

# 3. Verificar artefato JAR local
echo -e "${YELLOW}[3/6] Verificando artefato compilado...${RESET}"
if [ ! -f "${JAR_LOCAL}" ]; then
    echo -e "${RED}[ERRO] Fat JAR não encontrado em ${JAR_LOCAL}.${RESET}" >&2
    echo -e "Compile o projeto previamente com: docker run --rm -v '${PROJECT_ROOT}/oncovit-sankhya-med-sync':/app -w /app maven:3.9.9-eclipse-temurin-17 mvn clean package -DskipTests" >&2
    exit 1
fi
JAR_SIZE=$(du -h "${JAR_LOCAL}" | cut -f1)
echo -e "${GREEN}✓ Artefato local encontrado: ${JAR_LOCAL} (${JAR_SIZE})${RESET}"

# 4. Sincronizar arquivos para a VPS
echo -e "${YELLOW}[4/6] Sincronizando artefato e recursos para ${SSH_HOST}:${REMOTE_DIR}...${RESET}"
ssh "${SSH_HOST}" "mkdir -p '${REMOTE_DIR}'"
scp "${JAR_LOCAL}" "${SSH_HOST}:${REMOTE_DIR}/oncovit-sankhya-med-sync-1.0.0.jar"
if [ -d "${PROJECT_ROOT}/database" ]; then
    scp -r "${PROJECT_ROOT}/database" "${SSH_HOST}:${REMOTE_DIR}/"
fi
if [ -d "${PROJECT_ROOT}/dossie-candidatura" ]; then
    scp -r "${PROJECT_ROOT}/dossie-candidatura" "${SSH_HOST}:${REMOTE_DIR}/"
fi
echo -e "${GREEN}✓ Sincronização de arquivos concluída com sucesso.${RESET}"

# 5. Iniciar aplicação dentro de sessão persistente do tmux
echo -e "${YELLOW}[5/6] Reiniciando sessão persistente do tmux ('${SESSION_NAME}')...${RESET}"
ssh "${SSH_HOST}" "bash -s" << EOF
set -e
# Encerrar sessão anterior caso exista
tmux kill-session -t ${SESSION_NAME} 2>/dev/null || true

# Iniciar nova sessão desacoplada executando o Fat JAR com JVM flags otimizadas
tmux new-session -d -s ${SESSION_NAME} "cd ${REMOTE_DIR} && exec java -Xms128m -Xmx512m -jar oncovit-sankhya-med-sync-1.0.0.jar"

# Confirmar existência da sessão
tmux has-session -t ${SESSION_NAME}
EOF
echo -e "${GREEN}✓ Sessão tmux '${SESSION_NAME}' criada e rodando na VPS.${RESET}"

# 6. Aguardar prontidão (Health Check Poll)
echo -e "${YELLOW}[6/6] Aguardando prontidão da aplicação (Actuator Health Check)...${RESET}"
ATTEMPTS=0
MAX_ATTEMPTS=30
READY=0

while [ ${ATTEMPTS} -lt ${MAX_ATTEMPTS} ]; do
    HTTP_CODE=$(ssh "${SSH_HOST}" 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || true')
    if [ "${HTTP_CODE}" = "200" ]; then
        READY=1
        break
    fi
    ATTEMPTS=$((ATTEMPTS + 1))
    echo -n "."
    sleep 2
done
echo ""

if [ ${READY} -eq 1 ]; then
    echo -e "${GREEN}${BOLD}✓ APLICAÇÃO ATIVA E RESPONDENDO COM SUCESSO! (HTTP 200)${RESET}"
    echo -e "Health: ${BOLD}http://localhost:8080/actuator/health${RESET}"
    echo -e "Swagger UI: ${BOLD}http://localhost:8080/swagger-ui/index.html${RESET}"
    echo -e "OpenAPI JSON: ${BOLD}http://localhost:8080/v3/api-docs${RESET}"
else
    echo -e "${RED}[AVISO] Aplicação não respondeu HTTP 200 no tempo limite.${RESET}"
    echo -e "${YELLOW}Logs da sessão tmux:${RESET}"
    ssh "${SSH_HOST}" "tmux capture-pane -pt ${SESSION_NAME}:0 || true"
    exit 1
fi

echo -e "\n${CYAN}================================================================${RESET}"
echo -e "${GREEN}${BOLD}Deploy finalizado com 100% de sucesso!${RESET}"
echo -e "${CYAN}================================================================${RESET}"
