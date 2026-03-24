(ns greb-course.templates.components
  "Public API — re-exports all sub-component namespaces so callers
   can keep using greb-course.templates.components unchanged."
  (:require [greb-course.templates.components-cards    :as cards]
            [greb-course.templates.components-products :as prod]
            [greb-course.templates.components-hygiene  :as hyg]
            [greb-course.templates.components-proposal :as prop]
            [greb-course.templates.components-store-map  :as comp-sm]
            [greb-course.templates.components-registro   :as comp-reg]
            [greb-course.templates.components-cleaning   :as comp-cln]
            [greb-course.templates.components-wash       :as comp-wash]))

;; ── cards ─────────────────────────────────────────────────────
(def mission-card    cards/mission-card)
(def info-card       cards/info-card)
(def stat-card       cards/stat-card)
(def timeline-entry  cards/timeline-entry)
(def timeline        cards/timeline)
(def mission-chip    cards/mission-chip)
(def image-card      cards/image-card)
(def image-grid      cards/image-grid)
(def visitor-zone    cards/visitor-zone)

;; ── products ──────────────────────────────────────────────────
(def product-showcase  prod/product-showcase)
(def product-timeline  prod/product-timeline)
(def product-card      prod/product-card)

;; ── hygiene ───────────────────────────────────────────────────
(def risk-familias-bolas-grid  hyg/risk-familias-bolas-grid)
(def highlight-bar             hyg/highlight-bar)
(def uniform-checklist         hyg/uniform-checklist)
(def section-bar               hyg/section-bar)
(def omnibar-embed-block       hyg/omnibar-embed-block)
(def criteria-table            hyg/criteria-table)
(def sched-row                 hyg/sched-row)

;; ── proposal ──────────────────────────────────────────────────
(def ref-table     prop/ref-table)
(def pricing-table prop/pricing-table)
(def steps-block   prop/steps-block)
(def two-col-block prop/two-col-block)
(def callout-block prop/callout-block)
(def feature-list  prop/feature-list)
(def text-block-el prop/text-block-el)
(def quote-table   prop/quote-table)
(def bank-card     prop/bank-card)

;; ── interactive / complex ─────────────────────────────────────
(def store-map        comp-sm/store-map)
(def registro-sheet   comp-reg/registro-sheet)
(def cleaning-gantt   comp-cln/cleaning-gantt)
(def cleaning-calendar comp-cln/cleaning-calendar)
(def wash-step        comp-wash/wash-step)
(def wash-carousel    comp-wash/wash-carousel)
