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
  [row width]
  (< (count (str (string/join "  " row) " ")) width))

(defn into-rows
  [list n]
  (reduce (fn [acc i]
            (update acc (mod (:index i) n) #(conj % (:value i))))
          (vec (repeat n []))
          (map-indexed (fn [idx i] {:index idx
                                    :value i}) list)))

(defn calc-num-rows
  "Yes, this is just a for loop with a break. Sue me."
  [list terminal-width]
  (let [length (count list)]
    (reduce (fn [_ idx]
              (let [mat (into-rows list idx)]
                (when (every? #(valid-row? % terminal-width) mat)
                  (reduced idx))))
            (range 1 length))))

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

(defn print-in-columns
  [list terminal-width]
  (let [n-rows (calc-num-rows list terminal-width)
        mat (into-rows list n-rows)
        col-widths (calc-col-widths mat)]
    (doseq [row mat]
      (let [zipped-with-width (map vector row col-widths)
            padded (map (fn [[s width]] (pad s width)) zipped-with-width)
            lines (str (string/join "  " padded) " ")]

        (println lines))
      )))
