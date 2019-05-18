(ns cljls.args
  (:require [clojure.string :as string]
            [clojure.set :as set]))

(def listings
  #{:one-per-line
    :columns
    :comma-separated})

(def show
  #{:all
    :all-but-implied
    :non-hidden})

(def defaults
  {:listing        :columns
   :show           :non-hidden
   :terminal-width (Integer. (or (System/getenv "COLUMNS") "80"))
   :bad-flag       nil})

(defn is-flag?
  "Won’t work for file-names beginning with hyphens. I’m ok with that."
  [arg]
  (string/starts-with? arg "-"))

(defn split-by-arg-type
  [args]
  (reduce (fn [acc arg]
            (if (is-flag? arg)
              (update acc :flags #(concat % (map keyword (string/split (subs arg 1) #""))))
              (update acc :paths #(concat % [arg]))))
          {:flags []
           :paths []}
          args))

(defn apply-flag
  [options flag]
  (case flag
    :a (assoc options :show :all)
    :A (assoc options :show :all-but-implied)
    :1 (assoc options :listing :one-per-line)
    :m (assoc options :listing :comma-separated)
    ;; else
    (update options :bad-flag #(or % flag))
    ))

(defn parse-args
  [args]
  (let [{:keys [flags paths]} (split-by-arg-type args)
        options (reduce apply-flag defaults flags)]
    (println options)
    (assoc options :paths paths)))
