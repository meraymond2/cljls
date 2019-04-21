(ns cljls.columns
  (:require [clojure.string :as string]))

;; This is mostly working, it mainly just needs re-arranging.
;; The valid-row? check needs to take padding into account, because
;; a row’s actual length depends on every other row. So you can
;; only check if a configuration is valid once you’ve padded them.


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
  [row width]
  ;; TODO Broken! needs to take padding into account
  (<= (count (str (string/join "  " row) " ")) width))

(defn into-rows
  [list n]
  (reduce (fn [acc i]
            (update acc (mod (:index i) n) #(conj % (:value i))))
          (vec (repeat n []))
          (map-indexed (fn [idx i] {:index idx
                                    :value i}) list)))

(defn calc-num-rows
  "Yes, this is just a for-loop with a break. Sue me."
  [list terminal-width]
  (let [length (count list)]
    (or (reduce (fn [_ idx]
                  (let [mat (into-rows list idx)]
                    (when (every? #(valid-row? % terminal-width) mat)
                      (reduced idx))))
                1
                (range 1 length))
        length)))

(defn calc-col-widths
  "Find the long string in each column."
  [mat]
  (let [cols-n (count (first mat))]
    (map (fn [col-i]
           (apply max (map (fn [row] (count (get row col-i))) mat)))
         (range 0 cols-n))))

(defn pad
  [string length]
  (format (str "%-" length "s") string))

(defn into-printable-columns
  [list terminal-width]
  (let [n-rows (calc-num-rows list terminal-width)
        mat (into-rows list n-rows)
        col-widths (calc-col-widths mat)]
    (->> mat
         (map (fn [row] (->> row
                             (map vector col-widths)
                             (map (fn [[width cell]] (pad cell width))))))
         (map (fn [row] (str (string/join "  " row) " ")))
         (string/join "\n"))))
