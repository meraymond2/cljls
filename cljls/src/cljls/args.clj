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
   :listing         :one-per-line                           ;; change to :columns
   :path            "."
   :show            :all                                    ;; change to :non-hidden
   :readable-sizes? false})

(defn parse-args
  [args]
  defaults)
