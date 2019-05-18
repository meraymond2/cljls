(ns cljls.printing
  (:require [cljls.colours :refer [colourised-file-name]]
            [cljls.columns :refer [into-printable-matrix]]
            [clojure.string :as string]))

(defn- one-per-line
  [files]
  (doseq [file files]
    (println (:chromatic-name file))))

(defn- columns
  [files width]
  (let [mat (into-printable-matrix (map :chromatic-name files) width)]
    (doseq [row mat]
      (println row))))

(defn- comma-separated
  [files]
  (->> files
       (map :chromatic-name)
       (string/join ", ")
       (println)))

(defn print-files
  [files options]
  (case (:listing options)
    :columns
    (columns files (:terminal-width options))

    :comma-separated
    (comma-separated files)

    :one-per-line
    (one-per-line files)))
