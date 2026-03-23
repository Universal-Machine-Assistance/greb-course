(ns greb.course
  "Course definition for the Greb Wiki — architecture & developer reference."
  (:require [greb.content :as c]))

(def course
  {:meta  {:id          "greb"
           :org         "greb"
           :slug        "greb_wiki"
           :title       "Greb Wiki"
           :description "Architecture & developer reference for Greb — the Flow-Based Programming environment."
           :category    "Developer Documentation"
           :tags        ["architecture" "ClojureScript" "FBP" "developer"]
           :style       {:illustration "neo-brutalism style, bold vivid colors, thick black outlines, chunky geometric 3D shapes, playful isometric blocks, bright yellow magenta cyan, modern flat design with depth, no text"}
           :lang        :en
           :i18n-overrides {}}

   :theme {:brand-name  "Greb"
           :logo        nil
           :images-base nil
           :colors {:primary   "#0f172a"
                    :secondary "#06b6d4"
                    :accent    "#8b5cf6"
                    :ink       "#0f172a"
                    :paper     "#f1f5f9"
                    :page      "#ffffff"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Overview"
          :entries (subvec c/index-entries 0 6)}
         {:label "Frontend"
          :entries (subvec c/index-entries 6 11)}
         {:label "API Layer"
          :entries (subvec c/index-entries 11 15)}
         {:label "Code Execution"
          :entries (subvec c/index-entries 15 19)}
         {:label "Storage & Persistence"
          :entries (subvec c/index-entries 19 23)}
         {:label "Backend Servers"
          :entries (subvec c/index-entries 23 27)}
         {:label "Reference"
          :entries (subvec c/index-entries 27 32)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "cover.png"
            :title    "Greb Wiki"
            :subtitle "Architecture · API · Developer Reference"}}

    ;; 2. Table of Contents (cards)
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Index (detailed)
    {:template :index
     :data {:title   "Index"
            :entries c/index-entries
            :groups  [{:label "Overview"
                       :items (subvec c/index-entries 3 6)}
                      {:label "Frontend"
                       :items (subvec c/index-entries 6 11)}
                      {:label "API Layer"
                       :items (subvec c/index-entries 11 15)}
                      {:label "Code Execution"
                       :items (subvec c/index-entries 15 19)}
                      {:label "Storage & Persistence"
                       :items (subvec c/index-entries 19 23)}
                      {:label "Backend Servers"
                       :items (subvec c/index-entries 23 27)}
                      {:label "Reference"
                       :items (subvec c/index-entries 27 31)}]}}

    ;; 4. Introduction — drop cap
    {:template :blocks
     :data {:id "introduccion"
            :header {:icon "book-open" :kicker "Overview" :title "Introduction"}
            :intro c/intro-dropcap
            :blocks [{:type :callout :icon "zap" :style :accent
                      :title "Key Fact"
                      :text "Greb is a **Flow-Based Programming** environment where nodes on an infinite canvas can execute code in 5+ languages, display rich content, and connect through typed ports — all backed by a multi-tier storage architecture."}]}}

    ;; 5. Architecture
    {:template :hero-section
     :data {:id "arquitectura"
            :hero {:kicker "System Design"
                   :title  c/arch-title
                   :subtitle c/arch-subtitle
                   :hero-img "architecture.png"}
            :blocks [{:type :info-grid :icon "monitor" :title "Browser (SPA)"
                      :items c/arch-browser-items}
                     {:type :info-grid :icon "server" :title "Node.js Backend"
                      :items c/arch-backend-items}]}}

    ;; 6. Getting Started
    {:template :hero-section
     :data {:id "getting-started"
            :hero {:kicker "Setup"
                   :title  c/start-title
                   :subtitle c/start-subtitle
                   :hero-img "getting-started.png"}
            :blocks [{:type :info-grid :icon "play" :title "Development"
                      :items c/start-items}
                     {:type :info-grid :icon "package" :title "Production & REPL"
                      :items c/start-prod-items}]}}

    ;; 7. State Management
    {:template :blocks
     :data {:id "state-mgmt"
            :header {:icon "database" :kicker "Frontend" :title c/state-title}
            :blocks [{:type :callout :icon "atom" :style :accent
                      :title "Reagent Atoms"
                      :text "All application state lives in **Reagent atoms** (`defonce` for hot-reload preservation). Components automatically re-render when atoms they dereference change."}
                     {:type :info-grid :icon "layers" :title "Graph & Execution State"
                      :items c/state-graph-items}
                     {:type :info-grid :icon "eye" :title "UI State"
                      :items c/state-ui-items}]}}

    ;; 8. Component Tree
    {:template :blocks
     :data {:id "component-tree"
            :header {:icon "git-branch" :kicker "Frontend" :title c/tree-title}
            :blocks [{:type :info-grid :icon "layout" :title "Component Hierarchy"
                      :items c/tree-items}]}}

    ;; 9. Node System
    {:template :hero-section
     :data {:id "node-system"
            :hero {:kicker "Frontend"
                   :title  c/node-title
                   :subtitle c/node-subtitle
                   :hero-img "node-system.png"}
            :blocks [{:type :info-grid :icon "box" :title "Core Node Types"
                      :items c/node-type-items}
                     {:type :info-grid :icon "star" :title "Specialized Nodes"
                      :items c/node-special-items}]}}

    ;; 10. Overlay System
    {:template :blocks
     :data {:id "overlay-system"
            :header {:icon "layers" :kicker "Frontend" :title c/overlay-title}
            :blocks [{:type :info-grid :icon "layers" :title "Overlay Layers"
                      :items c/overlay-items}]}}

    ;; 11. Inspector & Omnibar
    {:template :blocks
     :data {:id "inspector-omnibar"
            :header {:icon "search" :kicker "Frontend" :title c/inspector-title}
            :blocks [{:type :info-grid :icon "settings" :title "Inspector & Command Palette"
                      :items c/inspector-items}]}}

    ;; 12. API: Graph & Node Ops
    {:template :blocks
     :data {:id "api-graph"
            :header {:icon "share-2" :kicker "API Layer" :title c/api-graph-title}
            :blocks [{:type :callout :icon "braces" :style :accent
                      :title "Unified API"
                      :text "The unified API (`src/app/api.cljs`) re-exports **100+ functions** from domain-specific submodules. All functions are REPL-accessible via the omnibar or browser console."}
                     {:type :info-grid :icon "share-2" :title "Graph Operations"
                      :items c/api-graph-items}
                     {:type :info-grid :icon "box" :title "Node Operations"
                      :items c/api-node-items}]}}

    ;; 13. API: Code, UI & Sync
    {:template :blocks
     :data {:id "api-code-ui"
            :header {:icon "terminal" :kicker "API Layer" :title c/api-code-title}
            :blocks [{:type :info-grid :icon "code-2" :title "Code, UI & Sync"
                      :items c/api-code-items}]}}

    ;; 14. API: Database & Sacred Texts
    {:template :blocks
     :data {:id "api-data"
            :header {:icon "book" :kicker "API Layer" :title c/api-db-title}
            :blocks [{:type :info-grid :icon "database" :title "Database APIs"
                      :items c/api-db-items}
                     {:type :info-grid :icon "book" :title "Sacred Texts & Icons"
                      :items c/api-sacred-items}]}}

    ;; 15. API: Seedream & Translation
    {:template :blocks
     :data {:id "api-media"
            :header {:icon "image" :kicker "API Layer" :title c/api-media-title}
            :blocks [{:type :info-grid :icon "image" :title "AI Image Generation & Translation"
                      :items c/api-seedream-items}]}}

    ;; 16. Code Execution: Dispatch
    {:template :blocks
     :data {:id "exec-dispatch"
            :header {:icon "play-circle" :kicker "Code Execution" :title c/exec-title}
            :blocks [{:type :info-grid :icon "play-circle" :title "Language Handlers"
                      :items c/exec-dispatch-items}
                     {:type :info-grid :icon "link" :title "Input Chain & Bindings"
                      :items c/exec-chain-items}]}}

    ;; 17. Code Execution: Clojure & Python
    {:template :blocks
     :data {:id "exec-clojure"
            :header {:icon "code-2" :kicker "Code Execution" :title c/exec-clj-title}
            :blocks [{:type :info-grid :icon "code-2" :title "SCI & Pyodide"
                      :items c/exec-clj-items}]}}

    ;; 18. Code Execution: SQL & Shell
    {:template :blocks
     :data {:id "exec-sql-shell"
            :header {:icon "hard-drive" :kicker "Code Execution" :title c/exec-sql-title}
            :blocks [{:type :info-grid :icon "database" :title "SQL Execution"
                      :items c/exec-sql-items}
                     {:type :info-grid :icon "terminal" :title "Shell Variables"
                      :items c/exec-shell-vars}]}}

    ;; 19. Code Execution: LLM & Display
    {:template :blocks
     :data {:id "exec-llm-display"
            :header {:icon "bot" :kicker "Code Execution" :title c/exec-llm-title}
            :blocks [{:type :info-grid :icon "bot" :title "Ollama & Display Rendering"
                      :items c/exec-llm-items}]}}

    ;; 20. Storage Architecture
    {:template :blocks
     :data {:id "storage-arch"
            :header {:icon "archive" :kicker "Storage" :title c/storage-title}
            :blocks [{:type :callout :icon "layers" :style :accent
                      :title "Priority"
                      :text "**In-memory** (fastest) → **localStorage** (persistent) → **PostgreSQL** (canonical) → **R2** (binary assets). Each tier has its own write strategy and failure handling."}
                     {:type :info-grid :icon "archive" :title "Storage Tiers"
                      :items c/storage-arch-items}]}}

    ;; 21. localStorage & PostgreSQL
    {:template :blocks
     :data {:id "storage-local-pg"
            :header {:icon "database" :kicker "Storage" :title c/storage-local-title}
            :blocks [{:type :info-grid :icon "database" :title "Browser & Remote Persistence"
                      :items c/storage-local-items}]}}

    ;; 22. R2/S3 & Versioning
    {:template :blocks
     :data {:id "storage-r2-ver"
            :header {:icon "cloud" :kicker "Storage" :title c/storage-r2-title}
            :blocks [{:type :info-grid :icon "cloud" :title "Binary Assets & Versions"
                      :items c/storage-r2-items}]}}

    ;; 23. Entity / Link Model
    {:template :blocks
     :data {:id "entity-link"
            :header {:icon "link" :kicker "Storage" :title c/entity-title}
            :blocks [{:type :info-grid :icon "link" :title "Relational Data Model"
                      :items c/entity-items}]}}

    ;; 24. Backend: Production & Dev
    {:template :blocks
     :data {:id "backend-prod"
            :header {:icon "server" :kicker "Backend" :title c/backend-prod-title}
            :blocks [{:type :info-grid :icon "server" :title "Production & Development"
                      :items c/backend-prod-items}]}}

    ;; 25. Backend: SQLite & PostgreSQL
    {:template :blocks
     :data {:id "backend-data"
            :header {:icon "hard-drive" :kicker "Backend" :title c/backend-db-title}
            :blocks [{:type :info-grid :icon "hard-drive" :title "Database Servers"
                      :items c/backend-db-items}]}}

    ;; 26. Backend: R2, Sync & Shell
    {:template :blocks
     :data {:id "backend-services"
            :header {:icon "cloud" :kicker "Backend" :title c/backend-svc-title}
            :blocks [{:type :info-grid :icon "cloud" :title "Services"
                      :items c/backend-svc-items}]}}

    ;; 27. Backend: MCP & Integrated
    {:template :blocks
     :data {:id "backend-mcp"
            :header {:icon "plug" :kicker "Backend" :title c/backend-mcp-title}
            :blocks [{:type :info-grid :icon "plug" :title "MCP & All-in-One"
                      :items c/backend-mcp-items}]}}

    ;; 28. Data Model
    {:template :blocks
     :data {:id "data-model"
            :header {:icon "layers" :kicker "Reference" :title c/datamodel-title}
            :blocks [{:type :callout :icon "info" :style :accent
                      :title "Two Models"
                      :text "Greb has **two distinct data models**: the **Graph Model** (React Flow nodes/edges on canvas, recursive) and the **Entity/Link Model** (relational data for markers, verses, datasets). Both persist to localStorage and PostgreSQL."}
                     {:type :info-grid :icon "layers" :title "Data Structures"
                      :items c/datamodel-items}]}}

    ;; 29. Keyboard Shortcuts
    {:template :blocks
     :data {:id "shortcuts"
            :header {:icon "keyboard" :kicker "Reference" :title c/shortcuts-title}
            :blocks [{:type :info-grid :icon "keyboard" :title "Global Shortcuts"
                      :items c/shortcuts-items}]}}

    ;; 30. Environment & Ports
    {:template :blocks
     :data {:id "env-ports"
            :header {:icon "settings" :kicker "Reference" :title c/env-title}
            :blocks [{:type :info-grid :icon "key" :title "Environment Variables"
                      :items c/env-pg-items}
                     {:type :info-grid :icon "server" :title "Port Map"
                      :items c/port-items}]}}

    ;; 31. Key File Reference
    {:template :blocks
     :data {:id "file-ref"
            :header {:icon "file-code" :kicker "Reference" :title c/fileref-title}
            :blocks [{:type :info-grid :icon "file-code" :title "Core"
                      :items c/fileref-core-items}
                     {:type :info-grid :icon "box" :title "Components"
                      :items c/fileref-component-items}
                     {:type :info-grid :icon "play-circle" :title "Code Execution"
                      :items c/fileref-exec-items}
                     {:type :info-grid :icon "server" :title "Backend"
                      :items c/fileref-backend-items}]}}

    ;; 32. Credits
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :logos [{:src "logo-greb.png" :alt "GREB" :dark? true}]
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
