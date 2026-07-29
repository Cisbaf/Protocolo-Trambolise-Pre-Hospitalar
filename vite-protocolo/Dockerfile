# ==========================================================
# Dockerfile multi-estagio: serve dev e producao.
#
#   dev  -> vite dev server (--host) com codigo por volume
#   prod -> build estatico servido por `serve` (estagio final)
#
# O estagio final e' o "prod", entao um `docker build` sem
# --target continua produzindo a imagem de producao.
# ==========================================================

# ----------------------------------------------------------
# deps: node_modules (compartilhado entre dev e build)
# ----------------------------------------------------------
FROM node:20-alpine AS deps
WORKDIR /app

COPY package*.json ./
RUN npm install


# ----------------------------------------------------------
# dev: hot reload. O codigo vem de bind mount (compose).
# node_modules fica em volume nomeado para nao ser encoberto.
# ----------------------------------------------------------
FROM deps AS dev
WORKDIR /app

EXPOSE 5173

CMD ["npm", "run", "dev", "--", "--host", "--port", "5173"]


# ----------------------------------------------------------
# build: compila os assets estaticos
# ----------------------------------------------------------
FROM deps AS build
WORKDIR /app

COPY . .

# Vite congela as VITE_* no momento do build
ARG VITE_API_URL
ARG VITE_MODE
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_MODE=$VITE_MODE

RUN npm run build


# ----------------------------------------------------------
# prod: servidor estatico (estagio final)
# ----------------------------------------------------------
FROM node:20-alpine AS prod
WORKDIR /app

RUN npm install -g serve

COPY --from=build /app/dist ./dist

EXPOSE 5173

CMD ["serve", "-s", "dist", "-l", "5173"]
