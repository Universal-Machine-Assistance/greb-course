# Stage 1: Build ClojureScript
FROM node:20-slim AS js-build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY shadow-cljs.edn deps.edn ./
COPY src/ src/
COPY courses/ courses/
RUN npx shadow-cljs release app

# Stage 2: Run Clojure server
FROM clojure:temurin-21-tools-deps-alpine AS runner
WORKDIR /app
COPY deps.edn ./
RUN clojure -P
COPY src/ src/
COPY courses/ courses/
COPY public/ public/
COPY --from=js-build /app/public/js/ public/js/
EXPOSE 8020
CMD ["clojure", "-M:server"]
