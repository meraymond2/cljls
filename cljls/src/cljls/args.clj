(ns cljls.args
  (:require [clojure.string :as string]))

(def listings
  #{:one-per-line
    :columns
    :comma-separated})

(def show
  #{:all
    :all-but-implied
    :non-hidden})

(def defaults
  {:ignore          #""
   :listing         :comma-separated
   :path            "."
   :show            :all                                    ;; change to :non-hidden
   :readable-sizes? false
   :terminal-width  (Integer. (or (System/getenv "COLUMNS") "80"))})

(defn parse-args
  [args]
  defaults)
