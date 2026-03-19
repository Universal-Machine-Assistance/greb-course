(ns greb-course.rich-text
  "**bold** *italic* `shortcut` → DOM inline. Theme shortcut pills on a parent via CSS:
  --pill-fg, --pill-bg, --pill-border, --pill-shadow, or utility classes
  .rich-surface--frost-pill | --amber-pill | --teal-pill | .rich-surface--dark."
  (:require [clojure.string :as str]
            [greb-course.dom :as d]))

(defn- next-token [rem]
  (or (when-let [m (re-find #"^\*\*(.+?)\*\*" rem)]
        {:type :bold :text (m 1) :len (count (m 0))})
      (when-let [m (re-find #"^`([^`]+)`" rem)]
        {:type :kbd :text (m 1) :len (count (m 0))})
      (when-let [m (re-find #"^\*(.+?)\*" rem)]
        {:type :italic :text (m 1) :len (count (m 0))})))

(defn- plain-until-special [^string s]
  (let [n (count s)]
    (if (zero? n)
      ""
      (loop [j 0]
        (cond
          (>= j n) s
          :else
          (let [subj (subs s j)]
            (if (or (str/starts-with? subj "**")
                    (str/starts-with? subj "`")
                    (and (str/starts-with? subj "*")
                         (or (= 1 (count subj))
                             (not= \* (nth subj 1)))))
              (subs s 0 j)
              (recur (inc j)))))))))

(defn- parse-segments-step [rem acc]
  (if (str/blank? rem)
    acc
    (if-let [{:keys [type text len]} (next-token rem)]
      (recur (subs rem len) (conj acc [type text]))
      (let [p (plain-until-special rem)]
        (if (seq p)
          (recur (subs rem (count p)) (conj acc [:plain p]))
          (recur (subs rem 1) (conj acc [:plain (subs rem 0 1)])))))))

(defn parse-segments
  "Vector of [ :plain | :bold | :italic | :kbd, string ]."
  [s]
  (if-not (and s (string? s))
    []
    (if (str/blank? s)
      [[:plain ""]]
      (parse-segments-step (str s) []))))

(defn- segment->child [[kind t]]
  (case kind
    :plain t
    :bold (d/el :strong {:class "rich-strong"} t)
    :italic (d/el :em {:class "rich-em"} t)
    :kbd (d/el :span {:class "shortcut-pill"} t)))

(defn inline-children [s]
  (mapv segment->child (parse-segments s)))

(defn rich-p [s class-name]
  (apply d/el :p {:class class-name} (inline-children s)))

(defn rich-heading [level s class-name]
  (let [tag (case level 1 :h1 2 :h2 3 :h3 :h3)]
    (apply d/el tag {:class class-name} (inline-children s))))
