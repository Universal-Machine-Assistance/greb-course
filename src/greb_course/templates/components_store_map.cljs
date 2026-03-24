(ns greb-course.templates.components-store-map
  "Leaflet store locator + list (Valentino)."
  (:require [greb-course.dom :as d]))

(defn store-map [{:keys [locations]}]
  (let [map-el  (d/el :div {:id "valentino-map" :class "store-map-container"})
        list-el (d/el :div {:class "store-map-list"})
        wrapper (d/el :div {:class "store-map"} map-el list-el)]
    (doseq [{:keys [name addr tel hours lat lng]} locations]
      (let [card (d/el :div {:class "store-map-card" :data-lat (str lat) :data-lng (str lng) :data-name name}
                   (d/el :div {:class "store-map-card-hdr"}
                         (d/ic "ice-cream-cone" "store-map-card-logo")
                         (d/el :strong {:class "store-map-card-name"} name))
                   (d/el :p {:class "store-map-card-addr"} addr)
                   (d/el :p {:class "store-map-card-detail"}
                         (d/ic "phone" "store-map-card-icon") tel)
                   (d/el :p {:class "store-map-card-detail"}
                         (d/ic "clock" "store-map-card-icon") hours))]
        (.appendChild list-el card)))
    (js/setTimeout
      (fn []
        (when (and (.-L js/window) (.getElementById js/document "valentino-map"))
          (let [L    (.-L js/window)
                m    (.setView (.map L "valentino-map" #js {:attributionControl false})
                               #js [18.47 -69.93] 9)
                icon (.icon L #js {:iconUrl "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'%3E%3Ccircle cx='12' cy='12' r='8' fill='%238B4513' stroke='white' stroke-width='2'/%3E%3Ctext x='12' y='16' text-anchor='middle' fill='white' font-size='10' font-weight='bold'%3EV%3C/text%3E%3C/svg%3E"
                               :iconSize #js [24 24] :iconAnchor #js [12 12] :popupAnchor #js [0 -14]})
                mkrs (atom [])]
            (.addTo (.tileLayer L "https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
                                #js {:maxZoom 18}) m)
            (doseq [{:keys [name addr tel hours lat lng]} locations]
              (let [html   (str "<div style='font-size:12px;line-height:1.4'>"
                                "<strong style='color:#8B4513'>" name "</strong><br>"
                                addr "<br>📞 " tel "<br>🕐 " hours "</div>")
                    marker (.addTo (.marker L #js [lat lng] #js {:icon icon}) m)]
                (.bindPopup marker html #js {:maxWidth 220})
                (swap! mkrs conj marker)))
            (let [cards  (.querySelectorAll list-el ".store-map-card")
                  select (fn [idx sname]
                           (.setView m #js [(js/parseFloat (.getAttribute (.item cards idx) "data-lat"))
                                            (js/parseFloat (.getAttribute (.item cards idx) "data-lng"))] 15)
                           (.openPopup (nth @mkrs idx))
                           (doseq [j (range (.-length cards))]
                             (.remove (.-classList (.item cards j)) "is-active"))
                           (.add (.-classList (.item cards idx)) "is-active")
                           (.dispatchEvent js/document
                             (js/CustomEvent. "valentino-branch-select"
                               #js {:detail #js {:name sname}})))]
              (doseq [i (range (.-length cards))]
                (let [card  (.item cards i)
                      sname (.getAttribute card "data-name")]
                  (.addEventListener card "click" (fn [_] (select i sname)))
                  (.on (nth @mkrs i) "click" (fn [_] (select i sname)))))))))
      300)
    wrapper))
