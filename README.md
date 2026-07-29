# Protocolo AVC

Sistema de registro de atendimentos de AVC (frontend React/Vite + backend Spring Boot + MySQL).

Dev e produção usam **os mesmos arquivos de infraestrutura**. O que muda é o `.env` e um
arquivo de override do Compose — nunca o `docker-compose.yml` nem os `Dockerfile`.

---

## Começando

```bash
./protocolo setup dev     # cria o .env a partir de .env.example
./protocolo dev up        # sobe front + back + MySQL local
```

Pronto. Endereços:

| Serviço | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |
| MySQL | localhost:3306 |

As portas vêm de `AVC_FRONT_PORT` / `AVC_BACK_PORT` no `.env`. Se alguma estiver ocupada,
mude no `.env` e rode `./protocolo dev up` de novo.

---

## Comandos

Tudo passa pelo `./protocolo`. Rode sem argumentos para ver a ajuda completa.

### Desenvolvimento
```bash
./protocolo dev up [--build]    # sobe o ambiente
./protocolo dev down            # para (mantém o banco)
./protocolo dev restart [svc]   # reinicia tudo ou um serviço
./protocolo dev rebuild [svc]   # reconstrói as imagens do zero
./protocolo dev reset           # APAGA o banco local e sobe limpo
./protocolo dev logs [svc]      # logs
./protocolo dev shell <svc>     # shell dentro do container
```

### Produção
```bash
./protocolo prod check          # valida a configuração sem alterar nada
./protocolo prod deploy         # git pull + build + up + verificação
./protocolo prod logs           # logs
./protocolo prod down           # derruba (pede confirmação)
```

`prod deploy` aborta antes de tocar em qualquer container se o `.env` estiver inconsistente:
variável faltando, `JWT_SECRET` curto demais, `VITE_MODE=dev` em produção ou banco apontando
para o container de desenvolvimento.

### Configuração
```bash
./protocolo setup dev | prod    # gera o .env a partir do template
./protocolo setup show          # mostra a config (segredos mascarados)
./protocolo setup check         # valida o .env
./protocolo setup secret        # gera um JWT_SECRET novo
```

### Diagnóstico
```bash
./protocolo status              # panorama: containers, portas, saúde, banco, git
./protocolo status --watch      # atualiza a cada 5s
./protocolo doctor              # pré-requisitos e inconsistências entre branches
./protocolo logs erros          # só WARN/ERROR do backend
```

### Banco
```bash
./protocolo db info | ping      # dados e teste de conexão
./protocolo db shell            # cliente mysql (dev)
./protocolo db dump [arquivo]   # dump .sql em backups/
./protocolo db restore <arq>    # restaura (bloqueado fora de dev)
```

Variáveis úteis: `ASSUME_YES=1` pula confirmações (CI), `NO_COLOR=1` desliga cores.

---

## Como os dois ambientes se relacionam

```
docker-compose.yml            <- PRODUÇÃO (é o arquivo base)
docker-compose.dev.yml        <- override aplicado por cima só em dev
```

`docker compose up -d --build` sem nenhum `-f` extra continua subindo exatamente a produção,
como sempre foi. O ambiente de dev é `-f docker-compose.yml -f docker-compose.dev.yml`.

Os `Dockerfile` são multi-estágio e atendem os dois modos:

| Estágio | Backend | Frontend |
|---|---|---|
| `dev` | `mvn spring-boot:run`, código por bind mount | `vite --host`, hot reload |
| `prod` | jar sobre JRE, `SPRING_PROFILES_ACTIVE=prod` | build estático servido por `serve` |

O estágio final é o `prod`, então um `docker build` sem `--target` produz a imagem de produção.

### Diferenças que o override de dev aplica

- estágio `dev` das imagens (hot reload)
- código montado por bind mount
- container `mysql-db` local (**produção usa banco externo, fora do Compose**)
- `node_modules` e repositório Maven em volumes nomeados, independentes da máquina

---

## Configuração (`.env`)

Um único `.env` na raiz, gerado a partir de um template. Nunca é versionado.

| Template | Gera |
|---|---|
| `.env.example` | ambiente de desenvolvimento |
| `.env.prod.example` | ambiente de produção |

`APP_ENV=dev|prod` identifica o ambiente. Os scripts se recusam a rodar comandos de dev
num `.env` de produção e vice-versa.

### Duas armadilhas conhecidas

**`VITE_MODE` deve ficar vazio em produção.** O front (`vite-protocolo/src/settings.ts`)
usa `VITE_API_URL` quando `VITE_MODE=dev` e a origem da própria página caso contrário.
Com `VITE_MODE=dev` em produção, o acesso via domínio/proxy reverso quebra.

**As variáveis `VITE_*` são congeladas no build.** Mudar `VITE_API_URL` exige rebuild da
imagem do frontend (`./protocolo prod build`), não basta reiniciar o container.

### Perfis do Spring

| Arquivo | Quando | Credenciais |
|---|---|---|
| `application.properties` | sempre | — |
| `application-dev.properties` | perfil `dev` (padrão) | env vars com defaults locais |
| `application-prod.properties` | perfil `prod` (imagem de produção) | env vars, sem default |

Ambos os perfis são versionados e nenhum contém credencial real — os valores vêm do `.env`.

---

## Regras de branch para arquivos de infra

Estes arquivos precisam ser **idênticos** em todas as branches:

```
docker-compose.yml            spring-protocolo/Dockerfile
docker-compose.dev.yml        vite-protocolo/Dockerfile
.env.example                  spring-protocolo/src/main/resources/application*.properties
.env.prod.example             protocolo, scripts/
```

Se eles divergirem, um merge pode sobrescrever a configuração de produção — foi o que
aconteceu antes, quando `application-prod.properties` sumiu numa branch e o `.env` com
senha foi commitado em outra. `./protocolo doctor` checa isso e avisa.

Branches novas devem sair da branch normalizada e conter apenas mudanças de feature.

---

## Problemas comuns

**Porta ocupada** — `./protocolo doctor` mostra qual. Ajuste `AVC_FRONT_PORT`/`AVC_BACK_PORT`
no `.env`.

**Backend não sobe e o log mostra erro de conexão** — `./protocolo db ping`. Em dev,
`./protocolo dev logs db` mostra se o MySQL terminou de inicializar.

**Mudei dependência do front e o container não vê** — o `node_modules` fica em volume
nomeado: `./protocolo dev rebuild vite-protocolo`.

**Front carrega mas nenhuma chamada de API funciona** — confira `VITE_API_URL` e `VITE_MODE`
com `./protocolo setup show`. Em dev, `VITE_MODE` tem que ser `dev`.

**Banco de dev estranho depois de mudança de entidade** — `./protocolo dev reset` recria do zero.
