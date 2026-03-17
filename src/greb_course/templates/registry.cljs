(ns greb-course.templates.registry
  "Template keyword -> render fn map."
  (:require [greb-course.templates.cover        :as cover]
            [greb-course.templates.toc           :as toc]
            [greb-course.templates.page-index    :as page-index]
            [greb-course.templates.intro         :as intro]
            [greb-course.templates.hero-section  :as hero-section]
            [greb-course.templates.blocks        :as blocks]
            [greb-course.templates.risk          :as risk]
            [greb-course.templates.glossary      :as glossary]
            [greb-course.templates.credits       :as credits]
            [greb-course.templates.full-image    :as full-image]))

(def templates
  {:cover          cover/render
   :toc-card-grid  toc/render
   :index          page-index/render
   :intro          intro/render
   :hero-section   hero-section/render
   :blocks         blocks/render
   :risk           risk/render
   :glossary-index glossary/render-index
   :glossary-detail glossary/render-detail
   :credits        credits/render
   :full-image     full-image/render})

(defn render-page
  "Look up template by keyword, call (render data page-num theme)."
  [template-key data page-num theme]
  (if-let [render-fn (get templates template-key)]
    (render-fn data page-num theme)
    (js/console.error "Unknown template:" (name template-key))))
