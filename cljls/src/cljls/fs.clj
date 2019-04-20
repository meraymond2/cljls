(ns cljls.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))


(defn- normalised-name
  "For sorting, ignore capitalisation and hidden file marker."
  [file]
  (-> file
      (.getName)
      (string/lower-case)
      ((fn [fn] (if (string/starts-with? fn ".")
                  (subs fn 1)
                  fn)))))

(defn- sort-files
  "Sort alphabetically, then put directories first."
  [files]
  (->> files
       (sort-by normalised-name)
       (sort-by #(.isFile %))))

(defn get-files
  "Return a list of file objects, sorted by type and name."
  [path]
  (let [dir (io/file path)
        files (cond
                (not (.exists dir))
                nil

                (.isFile dir)
                [dir]

                (.isDirectory dir)
                (vec (.listFiles dir)))]
    (when files
      (sort-files files))))
