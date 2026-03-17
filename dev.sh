#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

# Kill anything already on our port
echo "==> Clearing port 8021 ..."
lsof -ti :8021 | xargs kill -9 2>/dev/null || true
sleep 1

echo "==> Starting shadow-cljs watch ..."
npx shadow-cljs watch app &
SHADOW_PID=$!

cleanup() {
  echo ""
  echo "==> Shutting down ..."
  kill $SHADOW_PID 2>/dev/null
  lsof -ti :8021 | xargs kill -9 2>/dev/null || true
  echo "==> Done."
}
trap cleanup EXIT INT TERM

# Wait for build to be ready
echo "==> Waiting for build ..."
for i in $(seq 1 30); do
  if curl -s -o /dev/null http://localhost:8021/ 2>/dev/null; then
    break
  fi
  sleep 2
done

echo ""
echo "  Server → http://localhost:8021"
echo ""
echo "  1. Open http://localhost:8021/valentino/guia_de_higiene_alimentaria/"
echo "  2. Press Enter here to connect the REPL"
echo ""
read -r

echo "==> Connecting CLJS REPL ..."
echo "  Type: (require '[greb-course.repl :as r])"
echo ""
rlwrap --always-readline --no-children npx shadow-cljs cljs-repl app
