# -------- Stage 1: build --------
FROM node:20-alpine AS builder

WORKDIR /app

# copia dependências
COPY package*.json ./

# instala dependências
RUN npm install

# copia código
COPY . .

# build do vite
RUN npm run build


# -------- Stage 2: runtime --------
FROM node:20-alpine

WORKDIR /app

# instala servidor estático
RUN npm install -g serve

# copia apenas os arquivos buildados
COPY --from=builder /app/dist ./dist

CMD ["serve", "-s", "dist", "-l", "5173"]