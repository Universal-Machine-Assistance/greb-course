(ns greb-course.sounds
  "UI micro-sfx (MP3 in /sounds/). :drop open, :hover skim, :pop confirm."
  (:require [greb-course.state :as state]))

(defonce ^:private last-hover-ms (atom 0))
(def ^:private hover-ms 110)

(defn- path [file]
  (str (.. js/window -location -origin) "/sounds/" file ".mp3"))

(defn- audio! [kw]
  (or (get @state/sfx-audio-cache kw)
      (let [a (js/Audio. (case kw :drop (path "drop") :hover (path "hover") (path "pop")))]
        (set! (.-volume a)
              (case kw :drop 0.4 :hover 0.2 :pop 0.36 0.3))
        (swap! state/sfx-audio-cache assoc kw a)
        a)))

(defn play! [kw]
  (try
    (let [a (audio! kw)]
      (set! (.-currentTime a) 0)
      (-> (.play a) (.catch (fn [_] nil))))
    (catch :default _ nil)))

(defn hover-throttled! []
  (let [t (js/Date.now)]
    (when (> (- t @last-hover-ms) hover-ms)
      (reset! last-hover-ms t)
      (play! :hover))))

(defn row-enter-handler [_e]
  (hover-throttled!))
