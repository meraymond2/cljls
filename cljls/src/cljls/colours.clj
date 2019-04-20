(ns cljls.colours
  (:require [cljls.fs :as fs]
            [clojure.string :as string])
  (:import (java.io File)))


(defn- colourise
  "Wrap a string in colour codes."
  [string codes]
  (str "\033[" (or codes "0") "m"
       string
       "\033[0m"))

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
  [^File file]
  (let [extension (fs/get-extension file)]
    (->> (or (get colour-options extension)
             (get colour-options (fs/get-file-type file)))
         (colourise (.getName file)))))
