(ns cljls.core
  (:require [cljls.args :refer [parse-args]]
            [cljls.fs :refer [get-files]]
            [cljls.printing :refer [print-files]])
  (:gen-class))

(defn no-file
  [path]
  (println (str path " does not exist."))
  (System/exit 2))

(defn reject-bad-flag
  [flag]
  (println (str "ls: invalid option -- '" (name flag) "'"))
  ;(System/exit 2)
  )

(defn -main
  [& args]
  (let [start (System/currentTimeMillis)
        options (parse-args args)
        bad-flag (:bad-flag options)]
    (if bad-flag
      (reject-bad-flag bad-flag)
      (let [files (first (map get-files (:paths options)))] ;; first only for now
        (print-files files options)))
    (println "Elapsed time: " (- (System/currentTimeMillis) start))))
