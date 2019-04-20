(ns cljls.printing
  (:require [cljls.colours :refer [colourised-file-name]]))

(defn- one-per-line
  [files]
  (doseq [file files]
    (println (colourised-file-name file))))

(defn- columns
  [files]
  (println "to do"))

(defn- comma-separated
  [files]
  (println "to do"))

(defn print-files
  [files style]
  (case style
    :columns
    (columns files)

    :comma-separated
    (comma-separated files)

    :one-per-line
    (one-per-line files)))
