(ns cljls.columns
  (:require [clojure.string :as string]))

(def width
  70)

;; for now, just sorted alphabetically
(def words
  ["cljls.iml" "project.clj" "README.md" "resources" "shell.sh" "src" "target" "test"])

(def expected-2
  [["cljls.iml" "README.md" "shell.sh" "target"]
   ["project.clj" "resources" "src" "test"]])

(def actual-3
  [["cljls.iml" "resources" "target"]
   ["project.clj" "shell.sh" "test"]
   ["README.md" "src"]]
  )

(defn valid-row?
  "Not the final row-string, just checks if it is within the max-width.
  If all rows in a layout are valid, then the actual row can be constructed."
  [row]
  (< (count (str (string/join "  " row) " ")) width))

(defn into-rows
  [list n]
  (reduce (fn [acc i]
            (update acc (mod (:index i) n) #(conj % (:value i))))
          (vec (repeat n []))
          (map-indexed (fn [idx i] {:index  idx
                                    :value i}) list)))
