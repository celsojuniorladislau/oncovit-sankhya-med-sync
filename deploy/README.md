# Guia de Implantação e Operação na VPS 2 (Hetzner)
## Microsserviço Oncovit Sankhya Med Sync (Java 17 + Spring Boot + tmux)

---

## 1. Visão Geral da Arquitetura

O microsserviço **Oncovit Sankhya Med Sync** é uma API REST desenvolvida em Java 17 e Spring Boot 3.2, projetada para a gestão de estoques farmacêuticos, rastreabilidade de medicamentos oncológicos e hospitalares sob diretrizes da **RDC 430/2020 da ANVISA**, controle rigoroso de cadeia de frio (2°C a 8°C), ordenação FEFO (*First Expire, First Out*), integração de baixa de estoque com o ERP Sankhya e geração de etiquetas térmicas em linguagem ZPL II (203 DPI).

Este guia documenta o procedimento padronizado de implantação, execução persistente em terminal desacoplado via **tmux**, abertura de portas no firewall UFW e validação ponta a ponta na **VPS 2 (Hetzner)**.

---

## 2. Topologia do Ambiente e Infraestrutura

| Parâmetro | Configuração Remota (VPS 2) |
|---|---|
| **Provedor / Data Center** | Hetzner Cloud (Nürnberg / Falkenstein - Alemanha) |
| **Hostname SSH** | `vps2` (configurado em `~/.ssh/config`) |
| **Endereço IPv6 Público** | `2a01:4f8:c014:395c::1` |
| **Endereço IPv4** | `100.65.183.25` (CGNAT) |
| **Sistema Operacional** | Ubuntu 26.04.1 LTS (Resolute Raccoon) x86_64 |
| **Kernel** | `Linux 7.0.0-38-generic` |
| **Recursos de Hardware** | 4 GB vRAM (3.1 GB livres), 25 GB de disco SSD NVMe livres |
| **Runtime Java** | OpenJDK Runtime Environment 17.0.20.1 64-Bit |
| **Multiplexador de Terminal** | `tmux 3.6` |
| **Sessão Persistente** | `oncovit-api` |
| **Porta da Aplicação** | `8080/tcp` (HTTP) |
| **Firewall** | UFW ativo com liberação explícita para `8080/tcp` |
| **Diretório de Instalação** | `/root/projetos/oncovit-sankhya-med-sync` |

---

## 3. Pré-requisitos para Execução

### No Ambiente Local (Desenvolvedor):
1. Chave SSH configurada em `~/.ssh/config`:
   ```ssh-config
   Host vps2
         HostName 2a01:4f8:c014:395c::1
         User root
         IdentityFile ~/.ssh/vps_hetzner
   ```
2. Pacote Fat JAR compilado em:
   `oncovit-sankhya-med-sync/target/oncovit-sankhya-med-sync-1.0.0.jar`
3. Utilitários locais: `ssh`, `scp`, `curl`, `python3`.

### No Host Remoto (VPS 2):
1. OpenJDK 17 JRE Headless (`apt-get install -y openjdk-17-jre-headless`)
2. tmux (`apt-get install -y tmux`)
3. Porta 8080 liberada no firewall:
   ```bash
   ufw allow 8080/tcp
   ```

---

## 4. Estrutura de Diretórios no Host Remoto

```
/root/projetos/oncovit-sankhya-med-sync/
├── oncovit-sankhya-med-sync-1.0.0.jar   # Fat JAR Spring Boot executável (56 MB)
├── database/                            # Scripts DDL, DML e auditoria SQL
│   ├── 01_schema_ddl.sql
│   ├── 02_seed_dml.sql
│   ├── 03_advanced_queries.sql
│   └── README.md
└── dossie-candidatura/                  # Material do candidato para a Oncovit
    ├── 01_curriculo_otimizado_gupy.md
    ├── 02_guia_abordagem_presencial.md
    ├── 03_guia_teste_tecnico_resolvido.md
    └── README.md
```

---

## 5. Scripts de Automação

O diretório `deploy/` contém scripts prontos para reprodução imediata:

### 5.1. `deploy/deploy_vps2.sh` (Implantação Automatizada)
Realiza o ciclo completo de entrega contínua:
1. Testa conectividade SSH com a VPS 2.
2. Verifica e provisiona dependências remotas (Java 17, tmux, UFW).
3. Transfere o Fat JAR e pastas de referência (`database/` e `dossie-candidatura/`).
4. Encerra com segurança qualquer instância legada da sessão `oncovit-api`.
5. Inicia a nova sessão desacoplada com parâmetros JVM otimizados (`-Xms128m -Xmx512m`).
6. Executa pooling no Spring Boot Actuator até confirmação de HTTP 200 `UP`.

**Execução:**
```bash
./deploy/deploy_vps2.sh
# Ou especificando outro host ssh:
./deploy/deploy_vps2.sh vps2
```

### 5.2. `deploy/verify_vps2.sh` (Diagnóstico e Teste de Saúde)
Valida a integridade da aplicação em produção:
1. Conexão SSH e presença da sessão persistente no tmux.
2. Identificação do PID Java, uso de CPU e consumo de memória RAM.
3. Consulta ao status de saúde (`/actuator/health`).
4. Verificação da documentação interativa Swagger UI (`/swagger-ui/index.html`).
5. Validação da especificação OpenAPI 3.0 (`/v3/api-docs`).
6. Amostragem funcional das rotas `/api/medicamentos`, `/api/lotes/criticos` e `/api/lotes/1/etiqueta`.

**Execução:**
```bash
./deploy/verify_vps2.sh
```

---

## 6. Comandos Operacionais de Manutenção (tmux & SSH)

Para operar a aplicação diretamente na VPS 2, utilize os comandos abaixo:

### Inspecionar a saída ao vivo do console:
```bash
ssh vps2 'tmux capture-pane -pt oncovit-api:0'
```

### Conectar-se interativamente ao terminal da aplicação (Attach):
```bash
ssh -t vps2 'tmux attach-session -t oncovit-api'
# Para desconectar sem parar a aplicação: pressione Ctrl + B e em seguida D (Detach)
```

### Reiniciar o microsserviço:
```bash
ssh vps2 'tmux kill-session -t oncovit-api && tmux new-session -d -s oncovit-api "cd /root/projetos/oncovit-sankhya-med-sync && exec java -Xms128m -Xmx512m -jar oncovit-sankhya-med-sync-1.0.0.jar"'
```

### Encerrar a aplicação:
```bash
ssh vps2 'tmux kill-session -t oncovit-api'
```

### Verificar consumo de recursos:
```bash
ssh vps2 'ps -eo pid,user,%cpu,%mem,vsz,rss,etime,comm | grep java'
```

---

## 7. Catálogo de Endpoints Remotos

A aplicação disponibiliza as seguintes rotas públicas na porta `8080`:

| Funcionalidade | Endpoint HTTP | Descrição |
|---|---|---|
| **Health Check** | `GET /actuator/health` | Estado dos componentes internos (H2, disco, liveness/readiness) |
| **Swagger UI** | `GET /swagger-ui/index.html` | Interface gráfica interativa para teste e documentação de APIs |
| **OpenAPI Spec** | `GET /v3/api-docs` | Especificação OpenAPI 3.0 em formato JSON |
| **Medicamentos** | `GET /api/medicamentos` | Catálogo completo com tipo de armazenamento e estoque total |
| **Lotes Críticos** | `GET /api/lotes/criticos` | Lotes a vencer em até 60 dias ordenados por FEFO |
| **Etiqueta ZPL II** | `GET /api/lotes/{id}/etiqueta` | Dados em JSON e comandos ZPL II 203 DPI para impressão térmica |
| **Webhook Sankhya** | `POST /api/integracao/sankhya/baixa-estoque` | Baixa de estoque transacional simulando o ERP Sankhya |

---

## 8. Homologação com a Suíte E2E (`test_e2e_suite.py`)

A homologação final do microsserviço em execução na VPS 2 é validada através do script automatizado de 83 testes ponta a ponta:

```bash
# Execução padrão contra a VPS 2 via SSH e portas remotas
python3 test_e2e_suite.py --target vps2 --host vps2 --port 8080

# Execução direcionada via IPv6 com teste live contra o Tomcat remoto
python3 test_e2e_suite.py --target vps2 --host "[2a01:4f8:c014:395c::1]" --port 8080 --live
```

**Resultado Homologado:** 83/83 testes aprovados (100% de sucesso).
