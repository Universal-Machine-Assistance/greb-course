(ns greb-course.viewer
  "Document viewer construction and toolbar."
  (:require [greb-course.dom                :as d]
            [greb-course.i18n               :as i18n]
            [greb-course.state              :as state]
            [greb-course.ui                 :as ui]
            [greb-course.nav                :as nav]
            [greb-course.animation          :as anim]
            [greb-course.spacemouse         :as sm]
            [greb-course.templates.registry :as reg]
            [greb-course.sounds             :as sfx]))

;; ── Toolbar ──────────────────────────────────────────────────────
(defn toolbar [indicator toggle-toc! theme]
  (let [logo      (get theme :logo)
        brand     (get theme :brand-name "")
        back-btn  (doto (d/el :a {:href "/" :class "toolbar-catalog-link" :title "Catalog" :aria-label "Catalog"}
                              (d/ic "house" ""))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click"
                          (fn [e]
                            (.preventDefault e)
                            (set! (.-location js/window) "/"))))
        mobile?   (.-matches (js/matchMedia "(max-width: 840px)"))
        print-btn (doto (d/el :button {:class (str "toolbar-btn" (when mobile? " toolbar-desktop-only"))}
                              (d/ic "printer" "") (i18n/t :print))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(.print js/window)))
        idx-btn   (doto (d/el :button {:class "toolbar-ghost-btn"}
                              (d/ic "list" "") (i18n/t :index))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" toggle-toc!))
        pres-btn  (doto (d/el :button {:class (str "toolbar-ghost-btn" (when mobile? " toolbar-icon-only"))}
                              (d/ic "play" "") (when-not mobile? (i18n/t :present)))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(when-let [f @state/on-enter-presentation] (f))))
        sm-btn    (when (sm/available?)
                    (doto (d/el :button {:class "toolbar-ghost-btn sm-indicator toolbar-icon-only"
                                         :title "SpaceMouse"
                                         :aria-label "SpaceMouse"}
                                (d/ic "move-3d" "sm-icon"))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" #(sm/connect!))))]
    (apply d/el :nav {:class "toolbar"}
          (remove nil?
            [(doto (d/el :a {:href "#portada" :class "toolbar-logo"}
                    (when logo (d/src-img logo brand nil)))
               (.addEventListener "mouseenter" sfx/row-enter-handler))
             indicator idx-btn pres-btn sm-btn print-btn back-btn]))))

;; ── Build course viewer ──────────────────────────────────────────
(defn build-viewer [course]
  (let [theme      (:theme course)
        toc-groups (:toc course)
        pages-def  (:pages course)
        all-pages  (vec (map-indexed
                          (fn [i {:keys [template data]}]
                            (reg/render-page template data (inc i) theme))
                          pages-def))
        mobile?    (.-matches (js/matchMedia "(max-width: 840px)"))
        _          (reset! state/built-mobile? mobile?)
        groups     (if mobile?
                     (mapv vector all-pages)
                     (partition 2 2 nil all-pages))
        spread-ids (mapv #(.-id (first %)) groups)
        id->spread (into {} (mapcat (fn [[i group]]
                                       (keep (fn [pg] (when pg [(.-id pg) i])) group))
                                     (map-indexed vector groups)))
        spreads    (mapv (fn [group] (apply d/el :div {:class "spread"} group)) groups)
        n          (count spreads)
        spread->pages (into {} (map-indexed
                                 (fn [i group]
                                   [i (vec (keep #(when % (.-id %)) group))])
                                 groups))
        ;; One dot per individual page (not per spread)
        page-dots  (vec
                     (mapcat
                       (fn [si group]
                         (map-indexed
                           (fn [pi pg]
                             (when pg
                               (let [page-num (inc (if mobile? si (+ (* si 2) pi)))
                                     side     (if (zero? pi) :left :right)]
                                 {:el    (d/el :button {:class "spread-dot"
                                                        :data-page (str page-num)
                                                        :data-spread (str si)
                                                        :data-side (name side)}
                                               (str page-num))
                                  :spread si
                                  :side   side})))
                           group))
                       (range) groups))
        page-dots  (vec (remove nil? page-dots))
        dots       (mapv :el page-dots)
        total-pages (count all-pages)
        indicator  (d/el :span {:class "spread-indicator"} (str "1 / " total-pages))
        prev-btn   (d/el :button {:class "nav-btn nav-prev"} (d/ic "chevron-left" ""))
        next-btn   (d/el :button {:class "nav-btn nav-next"} (d/ic "chevron-right" ""))
        init-idx   (or (get id->spread (nav/current-hash)) 0)
        ;; Build navigator with empty dots (we handle dot clicks ourselves)
        spread-dots (mapv (fn [_] (d/el :span {:class "spread-dot-hidden"})) (range n))
        {:keys [go! nav-state]} (nav/build-navigator spreads spread-ids spread-dots indicator prev-btn next-btn init-idx
                                                    :total-pages total-pages :mobile? mobile?)
        ;; Update per-page dots — highlight only ONE page, not the whole spread
        active-page (atom nil) ;; tracks the single active dot element
        update-page-dots! (fn [spread-idx side]
                            (let [target-side (or side :left)]
                              (doseq [{:keys [el spread side]} page-dots]
                                (if (and (= spread spread-idx) (= side target-side))
                                  (do (.add (.-classList el) "active")
                                      (reset! active-page el))
                                  (.remove (.-classList el) "active")))))
        _ (add-watch nav-state ::page-dots
            (fn [_ _ old-si ni]
              (let [dir-side (if (< ni old-si) :right :left)]
                (update-page-dots! ni dir-side))))
        ;; Click handler: navigate to spread + highlight clicked page
        _ (doseq [{:keys [el spread side]} page-dots]
            (.addEventListener el "click"
              (fn []
                (go! spread (when (< spread @nav-state) "going-back"))
                (reset! state/selected-edit-side side)
                (update-page-dots! spread side))))
        doc-navigate! (fn [page-id]
                        (when-let [idx (get id->spread page-id)]
                          (go! idx nil)))
        {:keys [overlay panel toggle!]} (nav/build-toc-panel doc-navigate! toc-groups ui/doc-shortcuts)
        _          (reset! state/current-nav {:go! go! :nav-state nav-state :id->spread id->spread :spread-ids spread-ids
                                        :toc-groups toc-groups
                                        :toggle-toc! toggle!
                                        :spread->pages spread->pages})]
    (doseq [s spreads] (.remove (.-classList s) "active"))
    (.add (.-classList (nth spreads init-idx)) "active")
    (update-page-dots! init-idx :left)
    (let [first-pg (inc (if mobile? init-idx (* init-idx 2)))]
      (set! (.-textContent indicator) (str first-pg " / " total-pages)))
    (set! (.-disabled prev-btn) (= init-idx 0))
    (set! (.-disabled next-btn) (= init-idx (dec n)))
    (when-not (nav/current-hash) (nav/set-hash! (nth spread-ids init-idx "")))
    (anim/animate-spread! (nth spreads init-idx))
    (.addEventListener js/window "hashchange"
      (fn []
        (when-let [idx (get id->spread (nav/current-hash))]
          (go! idx nil))))
    ;; Highlight cursor for doc mode (same as pres mode spotlight)
    (let [doc-hl (d/el :div {:class "pres-highlight-cursor doc-highlight-cursor"})
          _      (.addEventListener js/document "mousemove"
                   (fn [e]
                     (.setProperty (.-style doc-hl) "--hl-x" (str (.-clientX e) "px"))
                     (.setProperty (.-style doc-hl) "--hl-y" (str (.-clientY e) "px"))))
          ;; Intercept in-page anchor clicks (e.g. index page links) so they navigate via go!
          reader-el (apply d/el :div {:class (str "reader" (when mobile? " reader--mobile"))}
                          (concat spreads [prev-btn next-btn
                                           (apply d/el :div {:class "spread-dots"} dots)]))]
      (.addEventListener reader-el "click"
        (fn [e]
          (when-let [a (.closest (.-target e) "a[href^='#']")]
            (let [target-id (subs (.getAttribute a "href") 1)]
              (when-let [idx (get id->spread target-id)]
                (.preventDefault e)
                (nav/set-hash! target-id)
                (go! idx nil))))))
      (d/el :div {}
            overlay panel
            (toolbar indicator toggle! theme)
            reader-el
            doc-hl))))
