(ns greb-course.viewer
  "Document viewer construction and toolbar."
  (:require [greb-course.dom                :as d]
            [greb-course.i18n               :as i18n]
            [greb-course.state              :as state]
            [greb-course.ui                 :as ui]
            [greb-course.nav                :as nav]
            [greb-course.omnirepl           :as omni]
            [greb-course.animation          :as anim]
            [greb-course.spacemouse         :as sm]
            [greb-course.templates.registry :as reg]
            [greb-course.sounds             :as sfx]))

(defn- landscape? [el]
  (= "landscape" (.getAttribute el "data-orientation")))

;; ── Toolbar ──────────────────────────────────────────────────────
(defn- trigger-print! []
  (omni/dismiss!)
  (js/requestAnimationFrame
    (fn [] (.print js/window))))

(defn- export-pdf-url [org slug]
  (str "/api/export-pdf?org=" (js/encodeURIComponent org)
       "&slug=" (js/encodeURIComponent slug)))

(defn- ensure-pdf-blob [resp]
  (if-not (.-ok resp)
    (.reject js/Promise (js/Error. (str "HTTP " (.-status resp))))
    (let [ctype (or (.get (.-headers resp) "content-type") "")]
      (-> (.blob resp)
          (.then (fn [blob]
                   (if (and (.includes (.toLowerCase ctype) "application/pdf")
                            (> (.-size blob) 10000))
                     blob
                     (.reject js/Promise
                       (js/Error.
                         (str "Invalid PDF response (" ctype ", " (.-size blob) " bytes)"))))))))))

(defn- trigger-pdf-download! []
  (omni/dismiss!)
  (if-let [{:keys [org slug]} (:meta @state/current-course)]
    (do
      (ui/show-toast! (str (i18n/t :pdf-download) "...") 1800)
      (-> (js/fetch (export-pdf-url org slug))
          (.then ensure-pdf-blob)
          (.then (fn [blob]
                   (let [dl-url (.createObjectURL js/URL blob)
                         a      (.createElement js/document "a")]
                     (set! (.-href a) dl-url)
                     (set! (.-download a) (str org "-" slug ".pdf"))
                     (.appendChild (.-body js/document) a)
                     (.click a)
                     (.remove a)
                     (js/setTimeout #(.revokeObjectURL js/URL dl-url) 1200)
                     (ui/show-toast! (i18n/t :pdf-download) 1400))))
          (.catch (fn [e]
                    (ui/show-toast! "PDF export failed. Restart server and try again." 4600)
                    (js/console.error "PDF export failed" e)))))
    (ui/show-toast! "No active course" 2200)))

(defn- sync-toolbar-offset! [toolbar-el]
  (let [root (.-documentElement js/document)
        apply-h! (fn []
                   (let [h (if toolbar-el (.-offsetHeight toolbar-el) 56)]
                     (.setProperty (.-style root) "--toolbar-h" (str h "px"))))]
    (apply-h!)
    (js/requestAnimationFrame apply-h!)
    (js/setTimeout apply-h! 80)
    (js/setTimeout apply-h! 260)
    (when (exists? js/ResizeObserver)
      (let [ro (js/ResizeObserver. (fn [_] (apply-h!)))]
        (.observe ro toolbar-el)))))

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
        pdf-btn   (doto (d/el :button {:class (str "toolbar-btn" (when mobile? " toolbar-desktop-only"))}
                              (d/ic "download" "") (i18n/t :pdf-download))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" trigger-pdf-download!))
        print-btn (doto (d/el :button {:class (str "toolbar-ghost-btn" (when mobile? " toolbar-desktop-only"))}
                              (d/ic "printer" "") (i18n/t :print))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" trigger-print!))
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
             indicator idx-btn pres-btn sm-btn pdf-btn print-btn back-btn]))))

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
                     (if (seq all-pages)
                       (loop [remaining (rest all-pages)
                              acc [[(first all-pages)]]]  ;; cover always solo
                         (if-not (seq remaining)
                           (vec acc)
                           (let [p (first remaining)]
                             (if (landscape? p)
                               (recur (rest remaining) (conj acc [p]))
                               ;; portrait: pair with next if it's also portrait
                               (let [nxt (second remaining)]
                                 (if (and nxt (not (landscape? nxt)))
                                   (recur (drop 2 remaining) (conj acc [p nxt]))
                                   (recur (rest remaining) (conj acc [p]))))))))
                       []))
        spread-first-pages
        (loop [gs groups
               next-page 1
               out []]
          (if (seq gs)
            (let [g (first gs)
                  cnt (count (keep identity g))]
              (recur (rest gs) (+ next-page cnt) (conj out next-page)))
            out))
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
                               (let [page-num (+ (nth spread-first-pages si 1) pi)
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
                                                    :total-pages total-pages
                                                    :mobile? mobile?
                                                    :spread-first-pages spread-first-pages)
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
        clear-dot-preview!
        (fn []
          (doseq [s spreads]
            (.remove (.-classList s) "spread-preview-hover" "spread-preview-dim"))
          (when-let [r (.querySelector js/document ".reader")]
            (doseq [p (array-seq (.querySelectorAll r ".page"))]
              (.remove (.-classList p) "page-preview-highlight" "page-preview-dim-peer"))))
        apply-dot-preview!
        (fn [target-spread target-side]
          (clear-dot-preview!)
          (let [cur   @nav-state
                sp    (nth spreads target-spread)
                pages (vec (array-seq (.querySelectorAll sp ".page")))
                pi    (if (= 1 (count pages))
                        0
                        (if (= target-side :left) 0 1))
                page-el (nth pages pi nil)]
            (when page-el
              (if (= target-spread cur)
                (do
                  (.add (.-classList page-el) "page-preview-highlight")
                  (doseq [[i p] (map-indexed vector pages)]
                    (when (and (> (count pages) 1) (not= i pi))
                      (.add (.-classList p) "page-preview-dim-peer"))))
                (do
                  (.add (.-classList (nth spreads cur)) "spread-preview-dim")
                  (.add (.-classList sp) "spread-preview-hover")
                  (.add (.-classList page-el) "page-preview-highlight"))))))
        ;; Click handler: navigate to spread + highlight clicked page
        _ (doseq [{:keys [el spread side]} page-dots]
            (.addEventListener el "click"
              (fn []
                (go! spread (when (< spread @nav-state) "going-back"))
                (reset! state/selected-edit-side side)
                (update-page-dots! spread side)))
            (.addEventListener el "mouseenter"
              (fn [] (apply-dot-preview! spread side)))
            (.addEventListener el "mouseleave"
              (fn [e]
                (let [rel (.-relatedTarget e)
                      still-in-dots? (when (instance? js/Element rel)
                                       (.closest rel ".spread-dots"))]
                  (when-not still-in-dots?
                    (clear-dot-preview!))))))
        doc-navigate! (fn [page-id]
                        (when-let [idx (get id->spread page-id)]
                          (go! idx nil)))
        {:keys [overlay panel toggle!]} (nav/build-toc-panel doc-navigate! toc-groups ui/doc-shortcuts)
        ;; Append spread dots as full-width bottom bar in the TOC panel
        dots-bar   (apply d/el :div {:class "spread-dots toc-spread-dots"} dots)
        _          (.appendChild panel dots-bar)
        _          (reset! state/current-nav {:go! go! :nav-state nav-state :id->spread id->spread :spread-ids spread-ids
                                        :toc-groups toc-groups
                                        :toggle-toc! toggle!
                                        :spread->pages spread->pages
                                        :spread-first-pages spread-first-pages
                                        :clear-dot-preview! clear-dot-preview!})]
    (doseq [s spreads] (.remove (.-classList s) "active"))
    (.add (.-classList (nth spreads init-idx)) "active")
    (update-page-dots! init-idx :left)
    (let [first-pg (or (nth spread-first-pages init-idx nil)
                       (inc (if mobile? init-idx (* init-idx 2))))]
      (set! (.-textContent indicator) (str first-pg " / " total-pages)))
    (set! (.-disabled prev-btn) (= init-idx 0))
    (set! (.-disabled next-btn) (= init-idx (dec n)))
    (when-not (nav/current-hash) (nav/set-hash! (nth spread-ids init-idx "")))
    (anim/animate-spread! (nth spreads init-idx))
    (.addEventListener js/window "hashchange"
      (fn []
        (when-let [idx (get id->spread (nav/current-hash))]
          (go! idx nil))))
    ;; 'i' key handled by core_boot.cljs keydown handler via state/current-nav :toggle-toc!
    ;; Highlight cursor for doc mode (same as pres mode spotlight)
    (let [doc-hl (d/el :div {:class "pres-highlight-cursor doc-highlight-cursor"})
          _      (.addEventListener js/document "mousemove"
                   (fn [e]
                     (.setProperty (.-style doc-hl) "--hl-x" (str (.-clientX e) "px"))
                     (.setProperty (.-style doc-hl) "--hl-y" (str (.-clientY e) "px"))))
          ;; Hide top bar + prev/next after mouse stops (desktop reader only)
          _      (when-not mobile?
                   (let [root    (.-documentElement js/document)
                         idle-ms 2800
                         tref    (atom nil)
                         toc-open? (fn []
                                     (when-let [tw (.querySelector js/document ".toc-wrapper")]
                                       (.contains (.-classList tw) "open")))
                         typing? (fn []
                                   (let [t (.-activeElement js/document)]
                                     (when t
                                       (or (#{"INPUT" "TEXTAREA" "SELECT"} (.-tagName t))
                                           (let [ce (.getAttribute t "contenteditable")]
                                             (and ce (not= ce "false")))))))
                         hide-chrome!
                         (fn []
                           (when (and (not @state/pres-state)
                                      (not (.contains (.-classList root) "ui-hidden"))
                                      (not (toc-open?))
                                      (not (typing?)))
                             (.add (.-classList root) "doc-chrome-idle")))
                         bump-chrome!
                         (fn []
                           (when-let [id @tref] (js/clearTimeout id))
                           (when-not (.contains (.-classList root) "ui-hidden")
                             (.remove (.-classList root) "doc-chrome-idle")
                             (reset! tref (js/setTimeout hide-chrome! idle-ms))))]
                     (.addEventListener js/window "mousemove" bump-chrome!)
                     (.addEventListener js/window "mousedown" bump-chrome!)
                     (bump-chrome!)))
          ;; Intercept in-page anchor clicks (e.g. index page links) so they navigate via go!
          reader-el (apply d/el :div {:class (str "reader" (when mobile? " reader--mobile"))}
                          (concat spreads [prev-btn next-btn]))]
      (.addEventListener reader-el "click"
        (fn [e]
          (when-let [a (.closest (.-target e) "a[href^='#']")]
            (let [target-id (subs (.getAttribute a "href") 1)]
              (when-let [idx (get id->spread target-id)]
                (.preventDefault e)
                (nav/set-hash! target-id)
                (go! idx nil))))))
      (let [tb (toolbar indicator toggle! theme)]
        (sync-toolbar-offset! tb)
        (d/el :div {}
              overlay panel
              tb
              reader-el
              doc-hl)))))
