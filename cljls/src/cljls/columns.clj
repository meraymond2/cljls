(ns cljls.columns
  (:require [clojure.string :as string]))

(defn- raw-length
  "Get the length, minus the colour-codes."
  [string]
  (count (string/replace string #"(\e\[.*?m|\e\[0m)" "")))

(defn- pad
  [string length]
  (let [to-pad (- length (raw-length string))]
    (apply str string (repeat to-pad " "))))

(defn into-mat
  "First sorts the list into columns, so it can find the width, then
  flips it into rows, for printing."
  [list n-rows]
  (let [columns (partition n-rows n-rows nil list)
        padded (vec (map (fn [col]
                           (let [col-width (apply max (map raw-length col))]
                             (vec (map (fn [cell] (pad cell col-width)) col))))
                         columns))]
    (vec (reduce (fn [acc col-idx]
                   (map-indexed (fn [row-idx row]
                                  (conj row (get (get padded col-idx) row-idx)))
                                acc))
                 (repeat n-rows [])
                 (range 0 (count padded))))))

(defn row-to-line
  "Elements are separated by two spaces, and each line ends with one extra space."
  [row]
  (str (string/join "  " row) " "))

(defn valid-row?
  "Not the final row-string, just checks if it is within the max-width.
  If all rows in a layout are valid, then the actual row can be constructed."
  [row max-width]
  (<= (raw-length (row-to-line row)) max-width))

(defn calc-num-rows
  "Yes, this is just a for-loop with a break. Sue me."
  [list max-width]
  (let [length (count list)]
    (or (reduce (fn [_ idx]
                  (let [mat (into-mat list idx)]
                    (when (every? #(valid-row? % max-width) mat)
                      (reduced idx))))
                1
                (range 1 length))
        length)))

(defn into-printable-matrix
  [list max-width]
  (let [n-rows (calc-num-rows list max-width)
        mat (into-mat list n-rows)]
    (map row-to-line mat)))
