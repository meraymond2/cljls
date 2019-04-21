(ns cljls.core
  (:require [cljls.args :refer [parse-args]]
            [cljls.fs :refer [get-files]]
            [cljls.printing :refer [print-files]])
  (:gen-class))

(defn no-file
  [path]
  (println (str path " does not exist."))
  (System/exit 2))

(defn -main
  [& args]
  (let [start (System/currentTimeMillis)
        options (parse-args args)
        files (get-files (:path options))]
    (if files
      (print-files files options)

      (no-file (:path options)))
    (println "Elapsed time: " (- (System/currentTimeMillis) start))))
