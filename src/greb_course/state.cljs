(ns greb-course.state
  "Shared mutable state atoms used across modules.")

(defonce ^:private scale-resize-bound?* (atom false))
(defonce built-mobile? (atom nil))
(defonce current-courses (atom nil))
(defonce current-course (atom nil))
(defonce current-nav (atom nil))
(defonce pres-state (atom nil))
(defonce doc-view (atom {:zoom 1.0 :text-scale 1.0}))
(defonce ui-status-el (atom nil))
(defonce toast-el (atom nil))
(defonce toast-timer (atom nil))
(defonce hint-state (atom nil))
(defonce shortcuts-overlay (atom nil))

;; Cached HTMLAudioElements for /sounds/*.mp3
(defonce sfx-audio-cache (atom {}))

;; SpaceMouse (3Dconnexion) state
(defonce sm-device (atom nil))
(defonce sm-translate (atom {:x 0 :y 0 :z 0}))
(defonce sm-rotate (atom {:pitch 0 :roll 0 :yaw 0}))
(defonce sm-ensure-raf! (atom nil))
(defonce sm-doc-ensure-raf! (atom nil))
;; Navigation callbacks set by active mode (pres or doc)
(defonce sm-on-prev (atom nil))
(defonce sm-on-next (atom nil))
(defonce sm-on-reset (atom nil))

;; Canvas zoom — visual-only scale via transform (no layout change)
(defonce canvas-zoom (atom 1.0))
(defonce canvas-zoom-active? (atom true))
(defonce canvas-commit-timer (atom nil))

;; Callback to enter presentation mode (set by presentation module to avoid circular deps)
(defonce on-enter-presentation (atom nil))

;; Late-bound callbacks set by core.cljs so core-boot can call them without circular deps
(defonce ^:dynamic doc-apply-view!*       (atom nil))
(defonce ^:dynamic save-doc-text-scale!*  (atom nil))
(defonce ^:dynamic restore-doc-text-scale* (atom nil))
(defonce ^:dynamic commit-canvas-zoom!*   (atom nil))
(defonce ^:dynamic preflight-debounced!*  (atom nil))
(defonce ^:dynamic reload!*               (atom nil))
(defonce ^:dynamic course-path*           (atom nil))

;; Selected edit side from bottom page dots (for editor)
(defonce selected-edit-side (atom nil))

;; Saved illustration style for current course (editable in doc detail modal)
(defonce illustration-style (atom nil))

(defn scale-resize-bound? [] @scale-resize-bound?*)
(defn set-scale-resize-bound! [] (reset! scale-resize-bound?* true))
