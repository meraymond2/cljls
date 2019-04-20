(ns cljls.core
  (:require [cljls.args :refer [parse-args]]
            [cljls.colours :refer [colourised-file-name]]
            [cljls.fs :refer [get-files]])
  (:gen-class))

(defn no-file
  [path]
  (println (str path " does not exist."))
  (System/exit 2))

(defn -main
  [& args]
  (let [options (parse-args args)
        files (get-files (:path options))]
    (if files
      (doseq [file files]
        (println (colourised-file-name file)))

      (no-file (:path options)))))
