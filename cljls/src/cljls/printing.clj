(ns cljls.printing
  (:require [cljls.colours :refer [colourised-file-name]]
            [cljls.columns :refer [into-printable-columns]]))

(defn- one-per-line
  [files]
  (doseq [file files]
    (println (colourised-file-name file))))

(defn- columns
  [files width]
  (let [file-names (map colourised-file-name files)]
    (doseq [row (into-printable-columns file-names width)]
      (println row))))

(defn- comma-separated
  [files]
  (println "to do"))

(defn print-files
  [files options]
  (case (:listing options)
    :columns
    (columns files (:columns options))

    :comma-separated
    (comma-separated files)

    :one-per-line
    (one-per-line files)))
