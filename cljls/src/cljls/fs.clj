(ns cljls.fs
  (:require [cljls.colours :as colour]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string])
  (:import (java.io File)
           (java.nio.file Paths Files LinkOption Path)
           (java.nio.file.attribute PosixFileAttributes)))

(defn- get-extension
  [file-name]
  (->> file-name
       (re-find #"^.+(\..*)$")
       (second)
       (keyword)))

(defn- get-attributes
  [^Path java-path]
  (Files/readAttributes java-path
                        PosixFileAttributes
                        (into-array LinkOption [LinkOption/NOFOLLOW_LINKS])))

(defn- is-executable?
  [permissions]
  (let [executable #{:OWNER_EXECUTE
                     :GROUP_EXECUTE
                     :OTHERS_EXECUTE}]
    (not (empty? (set/intersection permissions executable)))))

(defn get-file-type
  "I’m not sure that Java knows about the other Linux file types.

  The ‘executable’ is for colouring, and only applies to file, not
  executable directories."
  [^PosixFileAttributes attrs permissions]
  (cond
    (.isDirectory attrs)
    :di

    (.isSymbolicLink attrs)
    :ln

    (is-executable? permissions)
    :ex

    (.isRegularFile attrs)
    :fi

    (.isOther attrs)
    :??))

(comment
  (def file-data
    {:chromatic-name "\033[01;32mscript.sh\033[0m"
     :extension      :.sh
     :name           "script.sh"
     :permissions    #{:OWNER_EXECUTE :GROUP_EXECUTE :OTHERS_EXECUTE}
     :size           46
     :type           :ex}))

(defn file->file-data
  [^File file]
  (let [java-path (Paths/get (.getPath file) (into-array String []))
        attrs (get-attributes java-path)
        name (.getName file)
        extension (get-extension name)
        permissions (->> (.permissions attrs)
                         (map #(keyword (.toString %)))
                         (set))
        type (get-file-type attrs permissions)
        target (when (= :ln type)
                 (.toString (Files/readSymbolicLink java-path)))]
    {:chromatic-name (colour/colourised-file-name name extension type)
     :extension      extension
     :hidden         (.isHidden file)
     :name           name
     :permissions    permissions
     :size           (.size attrs)
     :target         target
     :type           type}))

(defn- normalised-name
  "For sorting, ignore capitalisation and hidden file marker."
  [file-data]
  (-> (:name file-data)
      (string/lower-case)
      ((fn [fn] (if (string/starts-with? fn ".")
                  (subs fn 1)
                  fn)))))

(defn- sort-files
  "Sort alphabetically, then put directories first."
  [files]
  (->> files
       (sort-by normalised-name)
       (sort-by #(not (= :di (:type %))))))

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
    (->> files
         (map file->file-data)
         (sort-files))))
