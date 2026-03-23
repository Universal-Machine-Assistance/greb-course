(ns greb-course.nav-spread-dots
  "Spread dot helpers (numbered circles — Dock magnification disabled).")

;; Dock magnification is disabled: dots are now numbered circles styled via CSS.
;; These functions are kept as no-ops so callers don't break.

(defn idle-scales! [_dots _active-idx])

(defn magnify-move! [_dots _active-idx _mx])

(defn attach-dock! [_bar _dots _state])
