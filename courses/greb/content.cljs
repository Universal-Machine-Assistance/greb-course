(ns greb.content
  "All content data for the Greb Wiki — architecture & developer reference.")

;; ── Index ───────────────────────────────────────────────────────
(def index-entries
  [{:id "portada"           :label "Portada"                        :page 1}
   {:id "contenido"         :label "Contenido"                      :page 2}
   {:id "indice"            :label "Índice"                         :page 3}
   {:id "introduccion"      :label "Introducción"                   :page 4  :icon "book-open"}
   {:id "arquitectura"      :label "Arquitectura"                   :page 5  :icon "cpu"}
   {:id "getting-started"   :label "Getting Started"                :page 6  :icon "play"}
   {:id "state-mgmt"        :label "State Management"               :page 7  :icon "database"}
   {:id "component-tree"    :label "Component Tree"                 :page 8  :icon "git-branch"}
   {:id "node-system"       :label "Node System"                    :page 9  :icon "box"}
   {:id "overlay-system"    :label "Overlay System"                 :page 10 :icon "layers"}
   {:id "inspector-omnibar" :label "Inspector & Omnibar"            :page 11 :icon "search"}
   {:id "api-graph"         :label "API: Graph & Node Ops"          :page 12 :icon "share-2"}
   {:id "api-code-ui"       :label "API: Code, UI & Sync"           :page 13 :icon "terminal"}
   {:id "api-data"          :label "API: Database & Sacred Texts"   :page 14 :icon "book"}
   {:id "api-media"         :label "API: Seedream & Translation"    :page 15 :icon "image"}
   {:id "exec-dispatch"     :label "Code Execution: Dispatch"       :page 16 :icon "play-circle"}
   {:id "exec-clojure"      :label "Code Execution: Clojure & Python" :page 17 :icon "code-2"}
   {:id "exec-sql-shell"    :label "Code Execution: SQL & Shell"    :page 18 :icon "hard-drive"}
   {:id "exec-llm-display"  :label "Code Execution: LLM & Display"  :page 19 :icon "bot"}
   {:id "storage-arch"      :label "Storage Architecture"           :page 20 :icon "archive"}
   {:id "storage-local-pg"  :label "localStorage & PostgreSQL"      :page 21 :icon "database"}
   {:id "storage-r2-ver"    :label "R2/S3 & Versioning"             :page 22 :icon "cloud"}
   {:id "entity-link"       :label "Entity / Link Model"            :page 23 :icon "link"}
   {:id "backend-prod"      :label "Backend: Production & Dev"      :page 24 :icon "server"}
   {:id "backend-data"      :label "Backend: SQLite & PostgreSQL"   :page 25 :icon "hard-drive"}
   {:id "backend-services"  :label "Backend: R2, Sync & Shell"      :page 26 :icon "cloud"}
   {:id "backend-mcp"       :label "Backend: MCP & Integrated"      :page 27 :icon "plug"}
   {:id "data-model"        :label "Data Model"                     :page 28 :icon "layers"}
   {:id "shortcuts"         :label "Keyboard Shortcuts"             :page 29 :icon "keyboard"}
   {:id "env-ports"         :label "Environment & Ports"            :page 30 :icon "settings"}
   {:id "file-ref"          :label "Key File Reference"             :page 31 :icon "file-code"}
   {:id "creditos"          :label "Créditos"                       :page 32 :icon "heart"}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Greb Wiki")
(def contenido-subtitle "Architecture & Developer Reference")

(def contenido-sections
  [{:id "introduccion" :title "Overview" :img "overview.png"
    :items [{:label "What is Greb?"           :ok true}
            {:label "Architecture diagram"    :ok true}
            {:label "Getting started"         :ok true}]}
   {:id "state-mgmt" :title "Frontend" :img "frontend.png"
    :items [{:label "State management"    :ok true}
            {:label "Component tree"      :ok true}
            {:label "Node & overlay system" :ok true}
            {:label "Inspector & Omnibar"  :ok true}]}
   {:id "api-graph" :title "API Layer" :img "api-layer.png"
    :items [{:label "Graph & node ops"       :ok true}
            {:label "Code, UI & sync ops"    :ok true}
            {:label "Database & sacred texts" :ok true}
            {:label "Seedream & translation" :ok true}]}
   {:id "exec-dispatch" :title "Code Execution" :img "code-execution.png"
    :items [{:label "Executor dispatch"       :ok true}
            {:label "Clojure (SCI) & Python"  :ok true}
            {:label "SQL & Shell"             :ok true}
            {:label "LLM & Display"           :ok true}]}
   {:id "storage-arch" :title "Storage & Backend" :img "storage.png"
    :items [{:label "Storage architecture"    :ok true}
            {:label "Entity/Link model"       :ok true}
            {:label "Backend servers"         :ok true}
            {:label "MCP & Integrated"        :ok true}]}
   {:id "shortcuts" :title "Reference" :img "reference.png"
    :items [{:label "Keyboard shortcuts"      :ok true}
            {:label "Environment variables"   :ok true}
            {:label "Port map"                :ok true}
            {:label "Key file reference"      :ok true}]}])

;; ── Intro ───────────────────────────────────────────────────────
(def intro-title "What is Greb?")

(def intro-lead
  "Greb is a Flow-Based Programming (FBP) environment built as a ClojureScript single-page application with a Node.js backend — a visual, node-based canvas for code, data and rich content.")

(def intro-dropcap
  ["**Greb** is a Flow-Based Programming (FBP) environment built as a ClojureScript single-page application with a Node.js backend. It provides a visual, node-based canvas where users can create and connect nodes on an infinite canvas, write and execute code in multiple languages (Clojure, Python, JavaScript, SQL, Shell), and display rich content (HTML, maps, images, PDFs, videos) inside nodes."
   "The system supports navigating into nodes to find nested graphs (recursive graphs), connecting to databases (SQLite, PostgreSQL, Datomic), generating images with AI (Seedream), chatting with local LLMs (Ollama), synchronizing graphs across devices over LAN, and versioning graphs with save/rollback support."
   "The frontend is built with **Reagent** (ClojureScript React wrapper) and **React Flow** (graph canvas library). The backend is a set of Node.js/Express microservices handling persistence, code execution, and file storage."])

(def intro-text
  "Greb combines the flexibility of a visual programming canvas with the power of a multi-language code execution environment. Nodes can contain code, display rich HTML, embed web pages, show maps, generate images, and query databases — all connected through a flow-based data pipeline with typed input/output ports.")

(def intro-sistema
  [{:title "React Flow"    :icon "git-branch"
    :text "Visual node-based canvas with infinite zoom and pan, drag-and-drop connections, and nested recursive graphs."}
   {:title "Multi-Language" :icon "code-2"
    :text "Execute Clojure (SCI), Python (Pyodide), JavaScript, SQL, and Shell commands directly in nodes."}
   {:title "Rich Display"   :icon "monitor"
    :text "Nodes render HTML/Hiccup, maps (Leaflet), PDFs, videos, images, and AI-generated content."}
   {:title "Microservices"  :icon "server"
    :text "Node.js backend with PostgreSQL, R2/S3 storage, LAN sync, shell execution, and MCP integration."}])

;; ── Architecture ────────────────────────────────────────────────
(def arch-title "Architecture")
(def arch-subtitle "Browser SPA + Node.js microservices")

(def arch-browser-items
  [{:title "React Flow"       :icon "git-branch"
    :text "Graph canvas library for node layout, connections, zoom/pan, and minimap."}
   {:title "Monaco Editor"    :icon "code-2"
    :text "VS Code's editor component embedded in overlay panels for in-node code editing."}
   {:title "Leaflet Maps"     :icon "map"
    :text "Geographic visualization with GeoJSON rendering, custom markers, and multiple base layers."}
   {:title "Pyodide"          :icon "braces"
    :text "Python runtime in the browser (v0.24.1, ~150MB). Lazy-loaded on first Python execution."}])

(def arch-backend-items
  [{:title "PostgreSQL"        :icon "database"
    :text "Canonical storage for graphs and entities. Connection pooling with SSL, Railway-ready."}
   {:title "R2 / S3"           :icon "cloud"
    :text "Binary asset storage (images, graph backups). Supports both Cloudflare R2 and AWS S3."}
   {:title "Shell Executor"    :icon "terminal"
    :text "Execute shell commands from nodes. Variable substitution, multi-line scripts, SSE streaming."}
   {:title "Sync Server"       :icon "refresh-cw"
    :text "LAN device synchronization. Push/pull/list operations with device tracking and timestamps."}])

;; ── Getting Started ─────────────────────────────────────────────
(def start-title "Getting Started")
(def start-subtitle "Prerequisites, install & run")

(def start-items
  [{:title "Prerequisites"     :icon "check-circle"
    :text "Node.js (v18+), Java (for shadow-cljs/ClojureScript compilation), and npm."}
   {:title "npm install"       :icon "download"
    :text "Install all dependencies with `npm install`."}
   {:title "npm run dev"       :icon "play"
    :text "Start development: shadow-cljs watch + dev proxy. App at `localhost:8081` or `localhost:8083`."}
   {:title "npm run launch"    :icon "rocket"
    :text "Dev stack + R2 local storage server for full offline workflow."}])

(def start-prod-items
  [{:title "npm run build"     :icon "package"
    :text "Compile ClojureScript release build for production deployment."}
   {:title "npm run start"     :icon "play-circle"
    :text "Start production server on port 3000. Serves built frontend + all API endpoints."}
   {:title "npm run repl"      :icon "terminal"
    :text "ClojureScript REPL (requires dev server running). Connect for interactive development."}
   {:title "launch-repl.sh"    :icon "file-code"
    :text "`./scripts/launch-repl.sh` — dev stack + auto-connect REPL in a single command."}])

;; ── State Management ────────────────────────────────────────────
(def state-title "State Management")
(def state-subtitle "Reagent atoms — all app state in one place")

(def state-graph-items
  [{:title "nodes"           :icon "box"
    :text "`[{:id :type :position :data}...]` — Current graph's React Flow nodes."}
   {:title "edges"           :icon "git-merge"
    :text "`[{:id :source :target}...]` — Current graph's edges (connections between nodes)."}
   {:title "current-graph"   :icon "file"
    :text "String — name of the active graph (default: `\"home\"`)."}
   {:title "database"        :icon "database"
    :text "`{:entities {...} :links {...}}` — Entity/link store, separate from graph model."}
   {:title "viewport"        :icon "maximize"
    :text "`{:x :y :zoom}` — Canvas zoom/pan state tracked in real time."}
   {:title "undo-stack"      :icon "rotate-ccw"
    :text "`[state...]` — History stack capped at 20 entries for undo support."}])

(def state-ui-items
  [{:title "show-inspector"     :icon "eye"
    :text "Inspector panel visibility toggle."}
   {:title "show-omnibar"       :icon "terminal"
    :text "Command palette visibility toggle."}
   {:title "show-terminal"      :icon "monitor"
    :text "Terminal/debug window visibility toggle."}
   {:title "display-outputs"    :icon "layout"
    :text "`{node-id -> {:hiccup [...] :error \"...\"}}` — Cached display renders per node."}
   {:title "alt-key-pressed"    :icon "hand"
    :text "Alt key held state — enables pan mode on the canvas."}
   {:title "edge-type"          :icon "minus"
    :text "Edge visual style: `:solid`, `:dashed`, or `:trailing-ants`."}])

;; ── Component Tree ──────────────────────────────────────────────
(def tree-title "Component Tree")
(def tree-subtitle "Reagent component hierarchy")

(def tree-items
  [{:title "ui/app → layout/app"     :icon "layout"
    :text "Root component mounts the full layout, including canvas, overlays, and panels."}
   {:title "flow-component"          :icon "git-branch"
    :text "React Flow canvas with greb-node (memo'd per node), Background grid, MiniMap, and Controls."}
   {:title "display-overlay"         :icon "layers"
    :text "Custom HTML for display nodes — Hiccup rendering, positioned and synced to node viewport."}
   {:title "code-overlay"            :icon "code-2"
    :text "Monaco editor instances for nodes with `:has-code true`. z-index: 1001."}
   {:title "web-overlay"             :icon "globe"
    :text "Iframe elements for web nodes. Viewport culling: only visible nodes render."}
   {:title "omnibar-overlay"         :icon "search"
    :text "Command palette (Cmd+K). Yellow panel with search, suggestions, and Clojure REPL."}
   {:title "inspector-overlay"       :icon "settings"
    :text "Property panel: code editor, port editor, property fields, history timeline. z-index: 10000."}
   {:title "terminal-overlay"        :icon "terminal"
    :text "Debug window with tab filtering (All, Clojure, Python, SQL...). Dark theme, resizable."}])

;; ── Node System ─────────────────────────────────────────────────
(def node-title "Node System")
(def node-subtitle "The fundamental unit in Greb")

(def node-type-items
  [{:title "greb"          :icon "box"
    :text "Standard node — code execution, display rendering, or plain label. The default type."}
   {:title "web"           :icon "globe"
    :text "Embedded iframe showing a URL. Rendered as web-overlay with viewport culling."}
   {:title "code"          :icon "code-2"
    :text "Dedicated code editor node with Monaco. Full language support and syntax highlighting."}
   {:title "display"       :icon "monitor"
    :text "Custom HTML/Hiccup rendering. Execute Clojure display code in SCI sandbox."}
   {:title "group"         :icon "square"
    :text "Container for grouping child nodes. Parent-child relationships for organization."}
   {:title "charta"        :icon "map"
    :text "Leaflet map visualization with GeoJSON, custom icons, and clickable markers."}])

(def node-special-items
  [{:title "pdf"           :icon "file-text"
    :text "PDF viewer node for embedding and navigating PDF documents within the graph."}
   {:title "image-gen"     :icon "image"
    :text "AI image generation via Seedream API. Text-to-image, inpaint, outpaint, upscale."}
   {:title "ollama-chat"   :icon "bot"
    :text "LLM chat interface connected to local Ollama server. Conversation history stored on node."}
   {:title "data-query"    :icon "database"
    :text "Database query builder for structured SQL queries against SQLite or PostgreSQL."}
   {:title "book-selector" :icon "book"
    :text "Sacred text chapter/verse picker for Vulgate (Bible) and Bhagavad Gita."}
   {:title "translator"    :icon "languages"
    :text "Text translation node via LibreTranslate integration with language detection."}])

;; ── Overlay System ──────────────────────────────────────────────
(def overlay-title "Overlay System")
(def overlay-subtitle "Fixed-position layers synced to canvas viewport")

(def overlay-items
  [{:title "Code Overlay"     :icon "code-2"
    :text "Monaco editor instances for nodes with `:has-code true`. Positioned at node coordinates, transformed by canvas zoom/pan. z-index: 1001."}
   {:title "Display Overlay"  :icon "layout"
    :text "Custom Hiccup/HTML for nodes with `:has-display true`. Executes display code in SCI sandbox, caches output. Re-runs on refresh trigger, input changes, or manual execution."}
   {:title "Web Overlay"      :icon "globe"
    :text "Renders `<iframe>` elements for web nodes. Viewport culling ensures only visible nodes are rendered for performance."}
   {:title "Viewport Sync"    :icon "maximize"
    :text "All overlays are fixed-position layers rendered on top of React Flow, synced to node positions via viewport transforms."}])

;; ── Inspector & Omnibar ─────────────────────────────────────────
(def inspector-title "Inspector & Omnibar")
(def inspector-subtitle "Panels for editing and commanding")

(def inspector-items
  [{:title "Node Editor"     :icon "edit"
    :text "Edit label, code, language, colors, and icons. Add/remove/rename input and output ports."}
   {:title "Tabs"            :icon "folder"
    :text "Code, Display, Properties, and Markers tabs — context-dependent based on node type."}
   {:title "History"         :icon "clock"
    :text "View execution history timeline. Draggable, resizable panel (z-index: 10000)."}
   {:title "Omnibar (Cmd+K)" :icon "search"
    :text "Yellow floating command palette. Matches commands, graphs, and ClojureScript expressions. Top 10 suggestions, draggable."}])

;; ── API: Graph & Node Ops ───────────────────────────────────────
(def api-graph-title "API: Graph & Node Operations")
(def api-graph-subtitle "100+ functions in the unified API layer")

(def api-graph-items
  [{:title "create-graph / open-graph"   :icon "plus"
    :text "Create new empty graphs or load and display existing ones. Navigate into node's nested graph with `navigate-into-node!`."}
   {:title "save / rename / delete"      :icon "save"
    :text "`save-current-graph!` persists to all backends. `rename-graph!` and `delete-graph` for lifecycle management."}
   {:title "export / import"             :icon "download"
    :text "Export/import graphs as JSON. `copy-graph-as-json!` and `paste-graph-as-json!` for clipboard operations."}
   {:title "versioning"                  :icon "git-branch"
    :text "`list-graph-versions`, `switch-version!`, `version-up-major!`, `version-up-minor!`. Format: `001.000`."}])

(def api-node-items
  [{:title "Node Creation"              :icon "plus-circle"
    :text "`create-node`, `create-code`, `create-data`, `create-web`, `create-charta`, `create-pdf`, `create-image-gen`, `create-chat`, `create-translator`."}
   {:title "Node Manipulation"          :icon "edit"
    :text "`rename-node!`, `delete-node!`, `set-node-value!`, `set-node-color!`, `set-node-icon!`, `bring-node-to-front!`."}
   {:title "Port Management"            :icon "git-merge"
    :text "`add-input-port!`, `add-output-port!`, `add-both-ports!`. Typed ports for data flow connections."}
   {:title "Group Operations"           :icon "square"
    :text "`create-group!`, `parent-node!`, `unparent-node!`, `ungroup-node!`. Parent-child grouping."}])

;; ── API: Code, UI & Sync ────────────────────────────────────────
(def api-code-title "API: Code, UI & Sync Operations")
(def api-code-subtitle "Code injection, UI toggles, and LAN sync")

(def api-code-items
  [{:title "Code Ops"          :icon "code-2"
    :text "`paste-code-to-selected!`, `paste-shell-to-selected!`, `create-shell-node!`, `create-pdf-node!`. Set code on nodes programmatically."}
   {:title "UI Ops"            :icon "monitor"
    :text "`show-inspector!` / `hide-inspector!`, `show-terminal!` / `toggle-terminal!`, `copy-selected-nodes!` / `paste-nodes!`, `undo!`."}
   {:title "Sync Ops"          :icon "refresh-cw"
    :text "`sync-push!`, `sync-pull!`, `sync-status!`, `sync-list!`, `sync-all!`, `sync-conflicts!`. LAN device synchronization."}
   {:title "Edge Visuals"      :icon "minus"
    :text "`switch-edge-type!` cycles between `:solid`, `:dashed`, and `:trailing-ants` styles. `hide-inputs!` collapses port handles."}])

;; ── API: Database & Sacred Texts ────────────────────────────────
(def api-db-title "API: Database & Sacred Texts")
(def api-db-subtitle "SQL queries, entity CRUD, and text APIs")

(def api-db-items
  [{:title "Generic SQL"       :icon "database"
    :text "`query-db`, `list-tables`, `select-all`, `select-where`. Execute SQL queries against connected databases."}
   {:title "SQLite"            :icon "hard-drive"
    :text "Browser HTTP interface (port 8083). `list-databases`, `open-database!`, `init-sqlite!`. Entity/link CRUD, import/export."}
   {:title "PostgreSQL"        :icon "server"
    :text "Railway backend. `enable-postgres-persistence!`, `load-from-postgres!`, `health-check`. Connection pooling with SSL."}
   {:title "Datomic"           :icon "layers"
    :text "Immutable database interface (port 8085). `enable-datomic-persistence!`, `load-from-datomic!`."}])

(def api-sacred-items
  [{:title "Unified API"       :icon "book"
    :text "`get-verse`, `get-chapter`, `search-text`. Queries the Latin Vulgate (Bible) and Bhagavad Gita through a single interface."}
   {:title "Vulgate"           :icon "book-open"
    :text "`vulgate-verse`, `vulgate-chapter`. Direct access to the Latin Vulgate text by book, chapter, and verse."}
   {:title "Gita"              :icon "bookmark"
    :text "`gita-verse`, `gita-chapter`. Direct access to the Bhagavad Gita by chapter and verse."}
   {:title "Icons & Commands"  :icon "search"
    :text "`search-icons`, `get-icons-by-category`, `search-commands`, `get-command-suggestions`. Powers the omnibar autocomplete."}])

;; ── API: Seedream & Translation ─────────────────────────────────
(def api-media-title "API: Seedream & Translation")
(def api-media-subtitle "AI image generation and language tools")

(def api-seedream-items
  [{:title "Models"            :icon "cpu"
    :text "Seedream 4.0, Seedream 4.5, Nano Banana Pro. Kie.ai API client for text-to-image and edit operations."}
   {:title "Operations"        :icon "image"
    :text "Text-to-image, inpaint, outpaint, and upscale. Aspect ratio and size mapping built-in."}
   {:title "Verse Cache"       :icon "bookmark"
    :text "Associates generated images with sacred text verses. Multiple images per verse tracked as history."}
   {:title "Translation"       :icon "languages"
    :text "LibreTranslate integration. `detect-language` (Sanskrit, Latin, English, etc.) and `translate-text` between languages."}])

;; ── Code Execution: Dispatch ────────────────────────────────────
(def exec-title "Code Execution: Dispatch")
(def exec-subtitle "Language routing based on node :language field")

(def exec-dispatch-items
  [{:title "clojure / clojurescript" :icon "braces"
    :text "`execute-clojure` → SCI (in-browser). Sandboxed Clojure evaluation with 80+ core functions."}
   {:title "python"                  :icon "code-2"
    :text "`execute-python` → Pyodide (in-browser). Input bindings translated from Clojure maps to Python dicts."}
   {:title "sql"                     :icon "database"
    :text "`execute-sql` → SQLite or PostgreSQL backend. Variable substitution with `${input_1}` placeholders."}
   {:title "javascript"              :icon "braces"
    :text "Inline `eval` in browser. Direct execution without sandboxing for quick JavaScript snippets."}
   {:title "shell / bash / terminal" :icon "terminal"
    :text "`execute-shell-command` → Backend HTTP/SSE. Variable substitution and multi-line script support."}
   {:title "ollama-query / chat"     :icon "bot"
    :text "Local Ollama server. Query (single response) or chat (conversation history) patterns."}])

(def exec-chain-items
  [{:title "Input Chain"        :icon "link"
    :text "Before executing a node, the executor runs all connected input nodes sequentially. Each input node's output becomes a named binding."}
   {:title "Port Bindings"      :icon "git-merge"
    :text "Named ports: `'prompt`, `'image`. Auto-numbered fallback: `'input_1`, `'input_2`. Always: `'self`, `'self-id`."}
   {:title "Port Routing"       :icon "share-2"
    :text "When an edge specifies a `sourceHandle` (output port name), the value is extracted from that specific port key on the source node."}
   {:title "image-gen"          :icon "image"
    :text "`execute-image-gen` → Seedream API. Per-node execution lock prevents double-clicks, auto-releases after 10min."}])

;; ── Code Execution: Clojure & Python ────────────────────────────
(def exec-clj-title "Code Execution: Clojure & Python")
(def exec-clj-subtitle "SCI sandbox and Pyodide runtime")

(def exec-clj-items
  [{:title "Code Context"       :icon "code-2"
    :text "80+ core Clojure functions (map, filter, reduce, atom ops), `clojure.string` namespace, input node bindings, and print capture."}
   {:title "Display Context"    :icon "layout"
    :text "Everything from code context plus: Reagent integration, safe widgets (SafeInput, SafeButton, MessageList), state access, and 60+ Greb API functions."}
   {:title "Python (Pyodide)"   :icon "braces"
    :text "Runs entirely in browser via Pyodide v0.24.1 (~150MB first load). Captures stdout/stderr. Lazy loading on first execution."}
   {:title "Input Translation"  :icon "repeat"
    :text "Clojure maps → Python dicts. Returns both raw Python object and string representation for terminal display."}])

;; ── Code Execution: SQL & Shell ─────────────────────────────────
(def exec-sql-title "Code Execution: SQL & Shell")
(def exec-sql-subtitle "Dual database and three-tier shell execution")

(def exec-sql-items
  [{:title "SQLite"             :icon "hard-drive"
    :text "Via backend HTTP server (localhost:8083). Variable substitution: `${input_1}`, `${column_name}` from input bindings."}
   {:title "PostgreSQL"         :icon "database"
    :text "Via Railway backend. Database selection with comment syntax: `-- DB: database-name`."}
   {:title "SSE Streaming"      :icon "radio"
    :text "Primary shell method: Server-Sent Events via `/api/execute-stream` for real-time streaming output."}
   {:title "HTTP POST"          :icon "send"
    :text "Fallback: full request/response via `/api/execute`. Simulation mode when backend unavailable."}])

(def exec-shell-vars
  [{:title "${GREB_FILES}"      :icon "folder"
    :text "Points to `~/GREB_FILES/` — the root directory for all graph-related files and databases."}
   {:title "${GRAPH_DIR}"       :icon "folder-open"
    :text "Points to `~/GREB_FILES/{graphName}/` — the working directory for the current graph."}
   {:title "${HOME}"            :icon "home"
    :text "User's home directory. All input bindings are also available as `${binding_name}` variables."}
   {:title "Multi-line"         :icon "file-text"
    :text "Multi-line scripts are written to temporary `.sh` files and executed. Working directory: `~/GREB_FILES/{graphName}/`."}])

;; ── Code Execution: LLM & Display ───────────────────────────────
(def exec-llm-title "Code Execution: LLM & Display")
(def exec-llm-subtitle "Ollama integration and Hiccup rendering")

(def exec-llm-items
  [{:title "Ollama Chat"        :icon "message-circle"
    :text "Local Ollama server (default: `localhost:11434`). Conversation history stored on node data. System prompt + message history + new input."}
   {:title "Ollama Query"       :icon "search"
    :text "Single query-response pattern using node's prompt + system field. No conversation history."}
   {:title "Display Rendering"  :icon "layout"
    :text "Clojure code → SCI evaluation → Hiccup output → `display-outputs[node-id]` → React render in overlay."}
   {:title "Safe Widgets"       :icon "shield"
    :text "SafeInput, SafeNumberInput, SafeButton, MessageList, StyledSelect. Specialized components for ollama-chat, image-gen, pdf, invoice."}])

;; ── Storage Architecture ────────────────────────────────────────
(def storage-title "Storage Architecture")
(def storage-subtitle "Multi-tier persistence strategy")

(def storage-arch-items
  [{:title "In-Memory"         :icon "zap"
    :text "Fastest tier — Reagent atoms (`@state/nodes`, `@state/edges`). All reads are instant from memory."}
   {:title "localStorage"      :icon "hard-drive"
    :text "Persistent browser storage. Debounced saves (2s delay) via `requestIdleCallback`. EDN serialization."}
   {:title "PostgreSQL"        :icon "database"
    :text "Canonical remote storage. Graph save: POST debounced 800ms. Non-blocking: failures logged, don't halt UI."}
   {:title "R2 / S3"           :icon "cloud"
    :text "Binary assets (images, graph backups). Exponential backoff when unavailable (caps at 5min). 5-minute timeout."}])

;; ── localStorage & PostgreSQL ───────────────────────────────────
(def storage-local-title "localStorage & PostgreSQL")
(def storage-local-subtitle "Browser persistence and canonical storage")

(def storage-local-items
  [{:title "Debounced Saves"    :icon "clock"
    :text "2-second delay via `requestIdleCallback`. `flush-save-to-storage!` for synchronous save before page unload."}
   {:title "Quota Tracking"     :icon "alert-triangle"
    :text "Retries after 10 minutes if quota exceeded. Automatic recovery from corrupted data."}
   {:title "Cached Promises"    :icon "zap"
    :text "PostgreSQL graph loads use cached promises to prevent duplicate fetches. 30-second timeout."}
   {:title "Fallback Strategy"  :icon "shield"
    :text "If PostgreSQL API returns 404, falls back to localStorage. Merges local verse-cache/image-gen data into API response."}])

;; ── R2/S3 & Versioning ─────────────────────────────────────────
(def storage-r2-title "R2/S3 & Graph Versioning")
(def storage-r2-subtitle "Binary assets and version management")

(def storage-r2-items
  [{:title "Image Upload"       :icon "image"
    :text "Single and batch image upload to Cloudflare R2 or AWS S3. Async graph backup support."}
   {:title "Backoff"            :icon "clock"
    :text "Exponential backoff when R2/S3 unavailable, caps at 5 minutes. 5-minute timeout for large payloads."}
   {:title "Version Format"     :icon "tag"
    :text "`001.000` (3-digit major, 3-digit minor). Auto-save every 90s increments minor: `001.000` → `001.001`."}
   {:title "Version Lifecycle"  :icon "rotate-ccw"
    :text "Manual major bump: `001.005` → `002.000`. Keeps latest 20 versions, older auto-deleted. `switch-version!` to rollback."}])

;; ── Entity / Link Model ─────────────────────────────────────────
(def entity-title "Entity / Link Model")
(def entity-subtitle "Two-table relational model separate from graphs")

(def entity-items
  [{:title "Entity"             :icon "box"
    :text "`{:id :type :name :properties :created-at :updated-at}`. Types: `:marker`, `:dataset`, `:institution`, `:verse`, etc."}
   {:title "Link"               :icon "link"
    :text "`{:id :from-entity-id :to-entity-id :type :properties :created-at}`. Types: `:belongs-to`, `:contains`, `:references`."}
   {:title "Use Cases"          :icon "map-pin"
    :text "Map markers with lat/lon, sacred text verses, datasets, institutional data. Properties are open-ended maps."}
   {:title "Storage"            :icon "database"
    :text "Stored in both localStorage and PostgreSQL. Entity CRUD, import/export, and migration support via SQLite and PostgreSQL servers."}])

;; ── Backend: Production & Dev ───────────────────────────────────
(def backend-prod-title "Backend: Production & Dev Servers")
(def backend-prod-subtitle "Main servers for deployment and development")

(def backend-prod-items
  [{:title "Production (3000)"  :icon "server"
    :text "`production-server.js` — Main server. PostgreSQL pooling with SSL, S3/R2 proxy, HTTP proxy for external APIs, image proxy for CORS."}
   {:title "Health & Proxy"     :icon "activity"
    :text "`GET /api/health`, `POST /api/proxy` (external APIs), `GET /api/image-proxy?url=` (CORS-safe images)."}
   {:title "Dev Proxy (8083)"   :icon "code-2"
    :text "`dev-proxy-server.js` — Routes API calls to local services or remote Railway backend. Graph CRUD, multi-device notifications."}
   {:title "Railway Routing"    :icon "cloud"
    :text "Reads `GREB_REMOTE_API_BASE` for Railway routing. Seamless local/remote backend switching."}])

;; ── Backend: SQLite & PostgreSQL ────────────────────────────────
(def backend-db-title "Backend: SQLite & PostgreSQL Servers")
(def backend-db-subtitle "Dedicated database interfaces")

(def backend-db-items
  [{:title "SQLite (8083)"       :icon "hard-drive"
    :text "`db-server.js` — SQLite HTTP interface. Databases in `~/GREB_FILES/databases/`. Multi-database, entity/link CRUD, raw SQL."}
   {:title "PostgreSQL (8084)"   :icon "database"
    :text "`postgres-server.js` — Dedicated PostgreSQL interface with connection pooling and indexed queries."}
   {:title "Entity Schema"       :icon "table"
    :text "Both servers implement entity/link schema. Import/export support for data migration between backends."}
   {:title "Raw SQL"             :icon "terminal"
    :text "Execute arbitrary SQL queries through the HTTP interface. Results returned as JSON column-name objects."}])

;; ── Backend: R2, Sync & Shell ───────────────────────────────────
(def backend-svc-title "Backend: R2, Sync & Shell")
(def backend-svc-subtitle "Storage, synchronization, and command execution")

(def backend-svc-items
  [{:title "R2 Storage (3002)"   :icon "cloud"
    :text "`r2-server.js` — S3/R2 compatible storage. Optional API key auth. Image upload (single/batch), graph sync."}
   {:title "Sync Server (3001)"  :icon "refresh-cw"
    :text "`sync-server.js` — LAN device sync. Graphs as JSON in `./sync-data/graphs/`. Push/pull/list with device tracking."}
   {:title "Shell Executor (8082)" :icon "terminal"
    :text "`shell-executor.js` — Execute shell from nodes. Variable substitution, multi-line scripts, working dir: `~/GREB_FILES/{graph}/`."}
   {:title "SSE Streaming"       :icon "radio"
    :text "Real-time output via `/api/execute-stream`. Fallback to HTTP POST or simulation mode when unavailable."}])

;; ── Backend: MCP & Integrated ───────────────────────────────────
(def backend-mcp-title "Backend: MCP & Integrated Server")
(def backend-mcp-subtitle "AI tool integration and all-in-one deployment")

(def backend-mcp-items
  [{:title "MCP Server (stdio)"  :icon "plug"
    :text "`mcp-server.js` — Model Context Protocol for AI tool integration (Cursor, etc.). 12 tools exposed."}
   {:title "MCP Tools"           :icon "wrench"
    :text "`list_graphs`, `get_graph`, `create_graph`, `delete_graph`, `list_nodes`, `get_node`, `create_node`, `update_node`, `delete_node`, `list_edges`, `create_edge`, `delete_edge`."}
   {:title "Integrated (8080)"   :icon "server"
    :text "`integrated-server.js` — All-in-one: frontend serving, graph CRUD, node/edge management, PostgreSQL, R2, shell, Seedream API."}
   {:title "Single Process"      :icon "package"
    :text "Useful for single-process deployments where running separate microservices is impractical."}])

;; ── Data Model ──────────────────────────────────────────────────
(def datamodel-title "Data Model")
(def datamodel-subtitle "Two distinct data models coexist in Greb")

(def datamodel-items
  [{:title "Graph Model"         :icon "git-branch"
    :text "Visual nodes and edges on canvas. Recursive — nodes can contain nested graphs. `Graph → Nodes + Edges + Viewport`."}
   {:title "Node Structure"      :icon "box"
    :text "`{:id :type :position :data}`. Data includes `:label`, `:color`, `:icon`, `:code`, `:language`, `:input-ports`, `:output-ports`."}
   {:title "Entity/Link Model"   :icon "link"
    :text "Relational model for non-graph content. `Entities: {id → {:type :name :properties}}`, `Links: {id → {:from :to :type}}`."}
   {:title "Dual Storage"        :icon "database"
    :text "Both models stored in localStorage and PostgreSQL. Graph model also synced to R2/S3 for binary assets."}])

;; ── Keyboard Shortcuts ──────────────────────────────────────────
(def shortcuts-title "Keyboard Shortcuts")

(def shortcuts-items
  [{:title "Cmd+K / Ctrl+K"     :icon "terminal"
    :text "Toggle omnibar (command palette / REPL). The primary way to navigate, search, and execute commands."}
   {:title "Cmd+S / Ctrl+S"     :icon "save"
    :text "Save current graph to all backends (localStorage + PostgreSQL + R2)."}
   {:title "Cmd+Z / Ctrl+Z"     :icon "rotate-ccw"
    :text "Undo last action. History stack capped at 20 entries."}
   {:title "Cmd+Shift+I"        :icon "settings"
    :text "Toggle inspector panel for viewing and editing the selected node."}
   {:title "Cmd+Shift+T"        :icon "terminal"
    :text "Toggle terminal/debug window with execution output and tab filtering."}
   {:title "Delete"              :icon "trash-2"
    :text "Delete selected nodes and their connected edges."}
   {:title "Shift+Click"        :icon "mouse-pointer"
    :text "Multi-select nodes on the canvas."}
   {:title "Alt+Drag"           :icon "move"
    :text "Pan canvas in any direction."}])

;; ── Environment & Ports ─────────────────────────────────────────
(def env-title "Environment Variables & Port Map")
(def env-subtitle "Configuration reference")

(def env-pg-items
  [{:title "PGHOST / PGPORT"    :icon "server"
    :text "PostgreSQL host (default: `localhost`) and port (default: `5432`)."}
   {:title "PGDATABASE / PGUSER" :icon "database"
    :text "Database name (default: `greb`) and user. `PGPASSWORD` or `DATABASE_URL` for auth."}
   {:title "GREB_REMOTE_API_BASE" :icon "cloud"
    :text "Railway URL for API proxying. Fallback: `RAILWAY_API_BASE`."}
   {:title "S3 / R2 Config"     :icon "key"
    :text "`S3_ENDPOINT`, `S3_ACCESS_KEY_ID`, `S3_SECRET_ACCESS_KEY`, `S3_BUCKET_NAME`, `S3_PUBLIC_URL`, plus R2 equivalents."}])

(def port-items
  [{:title "3000 — Production"   :icon "server"
    :text "`production-server.js` — Main production server serving frontend + API."}
   {:title "3001 / 3002"         :icon "cloud"
    :text "Sync server (LAN device sync) and R2 storage server (S3/R2 images)."}
   {:title "8080 / 8081"         :icon "layout"
    :text "Integrated all-in-one server and shadow-cljs dev server."}
   {:title "8082 / 8083 / 8084"  :icon "hard-drive"
    :text "Shell executor, dev proxy (or SQLite), and PostgreSQL interface."}
   {:title "9630"                :icon "settings"
    :text "shadow-cljs dashboard for build monitoring and debugging."}
   {:title "11434"               :icon "bot"
    :text "Ollama local LLM server (external). Default endpoint for ollama-chat and ollama-query."}])

;; ── Key File Reference ──────────────────────────────────────────
(def fileref-title "Key File Reference")
(def fileref-subtitle "Core, components, execution, storage, backend")

(def fileref-core-items
  [{:title "core.cljs / init.cljs"  :icon "file-code"
    :text "REPL-accessible exports and app initialization sequence (URL parse → load → render)."}
   {:title "layout.cljs / flow_component.cljs" :icon "layout"
    :text "Root component tree and React Flow canvas with nodes, background, minimap, controls."}
   {:title "state.cljs / db.cljs"   :icon "database"
    :text "All application state atoms and entity/link database operations."}
   {:title "api.cljs"               :icon "share-2"
    :text "Unified API barrel — re-exports 100+ functions from domain-specific submodules."}])

(def fileref-component-items
  [{:title "node/view.cljs"          :icon "box"
    :text "Main node renderer with handles.cljs for port connection points."}
   {:title "inspector/window.cljs"   :icon "settings"
    :text "Inspector panel: code editor, port editor, properties, history timeline."}
   {:title "omnibar/view.cljs"       :icon "search"
    :text "Command palette with search input, suggestions, and Clojure REPL."}
   {:title "overlays"                :icon "layers"
    :text "`display_overlay.cljs`, `code_overlay.cljs`, `web_overlay.cljs` — layered rendering."}])

(def fileref-exec-items
  [{:title "executor.cljs"           :icon "play-circle"
    :text "Language dispatch routing. Routes to clojure.cljs, python.cljs, sql.cljs, terminal.cljs."}
   {:title "clojure.cljs / python.cljs" :icon "code-2"
    :text "SCI evaluation (two contexts: code + display) and Pyodide runtime (lazy-loaded)."}
   {:title "context.cljs"            :icon "link"
    :text "Input bindings and data flow — resolves connected node outputs into named symbols."}
   {:title "display.cljs"            :icon "monitor"
    :text "Display rendering — Clojure → SCI → Hiccup → React. Safe widgets for interactive UI."}])

(def fileref-backend-items
  [{:title "production-server.js"    :icon "server"
    :text "Production server (3000). PostgreSQL + R2 + proxy + CORS image loading."}
   {:title "dev-proxy-server.js"     :icon "code-2"
    :text "Dev proxy (8083). Routes to local or Railway backend."}
   {:title "shell-executor.js"       :icon "terminal"
    :text "Shell execution (8082). SSE streaming, variable substitution, multi-line scripts."}
   {:title "mcp-server.js"           :icon "plug"
    :text "MCP server (stdio). 12 tools for AI integration: graph, node, and edge CRUD."}])

;; ── Credits ─────────────────────────────────────────────────────
(def credits-title "Credits")
(def credits-by "Built with Greb")
(def credits-orgs "GREBDev — Flow-Based Programming Environment")
(def credits-legal "This document was generated as a developer reference for the Greb system. It may be freely redistributed.")
