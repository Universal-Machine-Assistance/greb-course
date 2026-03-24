(ns greb-course.templates.components-wash
  "Handwash-style steps and carousel (video + text)."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [clojure.string :as str]))

(defn wash-step [{:keys [step title text icon img]}]
  (let [mp4      (when img (str/replace img #"\.png$" ".mp4"))
        has-vid? (boolean mp4)]
    (d/el :article {:class (str "wash-step animate" (when has-vid? " key-visual"))}
          (cond
            img
            (let [wrap   (d/el :div {:class "wash-step-img-wrap"})
                  vid    (d/el :video {:src (str @d/current-images-base mp4)
                                       :poster (str @d/current-images-base img)
                                       :loop true :muted true :playsinline true
                                       :class "wash-step-img wash-step-vid"})
                  poster (d/el :img {:src (str @d/current-images-base img)
                                     :alt (or title "")
                                     :class "wash-step-img wash-step-poster"})
                  play-btn (d/el :div {:class "wash-step-play"} "▶")
                  playing? (atom false)]
              (.addEventListener wrap "click"
                (fn [_]
                  (if @playing?
                    (do (.pause vid)
                        (reset! playing? false)
                        (.remove (.-classList wrap) "wash-step--playing"))
                    (do (.play vid)
                        (reset! playing? true)
                        (.add (.-classList wrap) "wash-step--playing")))))
              (.appendChild wrap poster)
              (.appendChild wrap vid)
              (.appendChild wrap play-btn)
              wrap)
            :else
            (d/el :div {:class "wash-step-num"}
                  (if icon (d/ic icon "wash-step-icon") step)))
          (d/el :div {:class "wash-step-body"}
                (apply d/el :h3 {:class "wash-step-title"}
                       (rich/inline-children (str title)))
                (apply d/el :p {:class "wash-step-text"}
                       (rich/inline-children (str text)))))))

(defn wash-carousel
  "Carousel showing one handwash step at a time with video + description alternating sides."
  [items]
  (let [n         (count items)
        container (d/el :div {:class "wash-carousel"})
        slides    (atom [])
        current   (atom 0)
        show!     (fn [idx]
                    (let [idx (mod idx n)]
                      (reset! current idx)
                      (doseq [[i sl] (map-indexed vector @slides)]
                        (if (= i idx)
                          (do (.remove (.-classList sl) "wc-slide--hidden")
                              (.add (.-classList sl) "wc-slide--active")
                              (when-let [vid (.querySelector sl "video")]
                                (.play vid)))
                          (do (.remove (.-classList sl) "wc-slide--active")
                              (.add (.-classList sl) "wc-slide--hidden")
                              (when-let [vid (.querySelector sl "video")]
                                (.pause vid)
                                (set! (.-currentTime vid) 0)))))))]
    (doseq [[i {:keys [step title text icon img]}] (map-indexed vector items)]
      (let [mp4   (when img (str/replace img #"\.png$" ".mp4"))
            even? (even? i)
            slide (d/el :div {:class (str "wc-slide" (if even? " wc-slide--video-left" " wc-slide--video-right")
                                         (when (pos? i) " wc-slide--hidden"))})]
        (let [video-panel (d/el :div {:class "wc-video-panel"})
              vid-el      (if img
                            (d/el :video {:src (str @d/current-images-base mp4)
                                          :poster (str @d/current-images-base img)
                                          :loop true :muted true :playsinline true
                                          :class "wc-video"})
                            (d/el :div {:class "wc-video-placeholder"}
                                  (d/el :div {:class "wash-step-num"}
                                        (if icon (d/ic icon "wash-step-icon") (str step)))))
              print-img   (when img
                            (d/el :img {:src (str @d/current-images-base img)
                                        :alt (or title "") :class "wc-video wc-video--print"}))]
          (.appendChild video-panel vid-el)
          (when print-img (.appendChild video-panel print-img))
          (let [text-panel (d/el :div {:class "wc-text-panel"})
                step-num   (d/el :span {:class "wc-step-num"} (str "Paso " (inc i)))
                title-el   (apply d/el :h3 {:class "wc-title"} (rich/inline-children (str title)))
                desc-el    (apply d/el :p {:class "wc-desc"} (rich/inline-children (str text)))]
            (.appendChild text-panel step-num)
            (.appendChild text-panel title-el)
            (.appendChild text-panel desc-el)
            (if even?
              (do (.appendChild slide video-panel) (.appendChild slide text-panel))
              (do (.appendChild slide text-panel) (.appendChild slide video-panel)))))
        (.appendChild container slide)
        (swap! slides conj slide)))
    (let [nav-bar (d/el :div {:class "wc-nav"})]
      (doseq [i (range n)]
        (let [dot (d/el :button {:class (str "wc-dot" (when (zero? i) " wc-dot--active"))
                                 :title (str "Paso " (inc i))}
                        (str (inc i)))]
          (.addEventListener dot "click" (fn [_] (show! i)))
          (.appendChild nav-bar dot)))
      (let [prev-btn (d/el :button {:class "wc-arrow wc-arrow--prev" :title "Anterior"} "‹")
            next-btn (d/el :button {:class "wc-arrow wc-arrow--next" :title "Siguiente"} "›")]
        (.addEventListener prev-btn "click" (fn [_] (show! (dec @current))))
        (.addEventListener next-btn "click" (fn [_] (show! (inc @current))))
        (.appendChild container prev-btn)
        (.appendChild container next-btn))
      (.appendChild container nav-bar)
      (add-watch current ::dots
        (fn [_ _ _ new-idx]
          (let [dots (.querySelectorAll nav-bar ".wc-dot")]
            (doseq [j (range (.-length dots))]
              (let [d (.item dots j)]
                (if (= j new-idx)
                  (.add (.-classList d) "wc-dot--active")
                  (.remove (.-classList d) "wc-dot--active")))))))
      (js/setTimeout #(show! 0) 100))
    container))
