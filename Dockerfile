# Stage 1: Build ClojureScript (needs both Node and Java/Clojure)
FROM clojure:temurin-21-tools-deps-bookworm-slim AS builder
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY deps.edn shadow-cljs.edn ./
RUN clojure -P
COPY src/ src/
COPY courses/ courses/
RUN npx shadow-cljs release app

# Stage 2: Run Clojure server (slim)
FROM clojure:temurin-21-tools-deps-alpine AS runner
RUN apk add --no-cache imagemagick libwebp-tools tiff-tools imagemagick-libs
WORKDIR /app
COPY deps.edn ./
RUN clojure -P
COPY src/ src/
COPY courses/ courses/
COPY public/ public/
COPY --from=builder /app/public/js/ public/js/
EXPOSE 8020
CMD ["clojure", "-M:server"]
