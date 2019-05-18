(ns cljls.colours
  (:require [clojure.string :as string]))

(defn- chromaticise
  "Wrap a string in colour codes."
  [string codes]
  (if codes
    (str "\033[" codes "m"
         string
         "\033[0m")
    string))

(defn- system-colors
  []
  (System/getenv "LS_COLORS"))

(defn- parse-colours
  "Turns the list into a map of {pattern codes}."
  [ls-colours-string]
  (as-> ls-colours-string cs
        (string/split cs #":")
        (map #(string/split % #"=") cs)
        (map (fn [[pattern codes]] [(keyword (string/replace pattern "*." ".")) codes]) cs)
        (into {} cs)))

;; TODO Move this to main, and handle errors
(def colour-options
  (parse-colours (system-colors)))

(defn colourised-file-name
  [name extension type]
  (->> (or (get colour-options extension)
           (get colour-options type))
       (chromaticise name)))
