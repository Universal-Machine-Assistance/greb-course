(ns greb-course.templates.registry
  "Template keyword -> render fn map."
  (:require [greb-course.dom :as d]
            [greb-course.templates.cover        :as cover]
            [greb-course.templates.toc           :as toc]
            [greb-course.templates.page-index    :as page-index]
            [greb-course.templates.intro         :as intro]
            [greb-course.templates.hero-section  :as hero-section]
            [greb-course.templates.blocks        :as blocks]
            [greb-course.templates.risk          :as risk]
            [greb-course.templates.glossary      :as glossary]
            [greb-course.templates.credits       :as credits]
            [greb-course.templates.legal         :as legal]
            [greb-course.templates.full-image    :as full-image]
            [greb-course.templates.gn-page       :as gn-page]
            [greb-course.templates.gn-cover      :as gn-cover]
            [greb-course.templates.gn-toc        :as gn-toc]
            [greb-course.templates.gn-divider    :as gn-divider]
            [greb-course.templates.gn-quote      :as gn-quote]))

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
   :legal          legal/render
   :full-image     full-image/render
   ;; Greb-nue templates
   :gn-page        gn-page/render
   :gn-cover       gn-cover/render
   :gn-backcover   gn-cover/render-backcover
   :gn-toc         gn-toc/render
   :gn-divider     gn-divider/render
   :gn-quote       gn-quote/render})

(defn render-page
  "Look up template by keyword, call (render data page-num theme).
   If data contains :section-tag, a vertical sidebar tab is prepended.
   Even pages (left in spread) get left tab, odd pages (right) get right tab."
  [template-key data page-num theme]
  (if-let [render-fn (get templates template-key)]
    (let [el (render-fn data page-num theme)]
      (when-let [{:keys [label color]} (:section-tag data)]
        (let [side (if (even? page-num) "left" "right")]
          (.add (.-classList el) "page--has-section-tab" (str "section-tab-side--" side))
          (.prepend el
            (d/el :div {:class (str "section-tab section-tab--" (or color "default")
                                    " section-tab--" side)}
                  (d/el :span {:class "section-tab-label"} label)))))
      (when (= :landscape (:orientation data))
        (.add (.-classList el) "page--landscape")
        (.setProperty (.-style el) "page" "landscape-page")
        (.setAttribute el "data-orientation" "landscape"))
      el)
    (js/console.error "Unknown template:" (name template-key))))
