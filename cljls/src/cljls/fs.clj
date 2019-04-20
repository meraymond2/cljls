(ns cljls.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.set :as set])
  (:import (java.io File)
           (java.nio.file Paths Files LinkOption)
           (java.nio.file.attribute PosixFileAttributes)))


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


(defn get-extension
  [^File file]
  (->> (.getName file)
       (re-find #"^.+(\..*)$" )
       (second)
       (keyword)))

(defn- get-attributes
  [^File file]
  (let [java-path (Paths/get (.getPath file) (into-array String []))
        attrs (Files/readAttributes java-path
                                    PosixFileAttributes
                                    (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))]
    attrs))

(defn- is-executable?
  [^PosixFileAttributes attrs]
  (let [permissions (->> (.permissions attrs)
                         (map #(.toString %))
                         (set))
        executable #{"OWNER_EXECUTE"
                     "GROUP_EXECUTE"
                     "OTHERS_EXECUTE"}]
    (not (empty? (set/intersection permissions executable)))))

(defn get-file-type
  "I’m not sure that Java knows about the other Linux file types."
  [^File file]
  (let [attrs (get-attributes file)]
    (cond
      (.isDirectory attrs)
      :di

      (.isSymbolicLink attrs)
      :ln

      (is-executable? attrs)
      :ex

      (.isRegularFile attrs)
      :fi

      (.isOther attrs)
      :??)))
