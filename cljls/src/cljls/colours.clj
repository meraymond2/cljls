(ns cljls.colours
  (:require [clojure.string :as string])
  (:import (java.io File)
           (java.nio.file Paths Files LinkOption)
           (java.nio.file.attribute BasicFileAttributes)))


(defn- colourise
  "Wrap a string in colour codes."
  [string codes]
  (str "\033[" (or codes "0") "m"
       string
       "\033[0m"))

(defn- system-colors
  []
  (System/getenv "LS_COLORS"))

(defn- parse-colours
  "The env string is separated by colons. It is a mixture of
  general rules, which are represented by two-letter codes, and
  file-extension specific colours. The syntax looks like general
  Unix wildcards, but it’s not possible to use arbitrary matches,
  only extensions.

  Turns the list into a map of {pattern codes}."
  [ls-colours-string]
  (as-> ls-colours-string cs
        (string/split cs #":")
        (map #(string/split % #"=") cs)
        (map (fn [[pattern codes]] [(string/replace pattern "*." ".") codes]) cs)
        (into {} cs)))

;; TODO Handle errors.
(def colour-options
  (parse-colours (system-colors)))

(defn get-extension
  [file-name]
  (second (re-find #"^.+(\..*)$" file-name)))

(defn get-attributes
  [^File file]
  (let [java-path (Paths/get (.getPath file) (into-array String []))
        attrs (Files/readAttributes java-path
                                    BasicFileAttributes
                                    (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))]
    attrs))

(defn colourised-file-name
  [^File file]
  (let [file-name (.getName file)
        extension (when (.isFile file) (get-extension file-name))
        attrs (get-attributes file)]
    (->> (cond
           (get colour-options extension)
           extension

           (.isDirectory file)
           "di"

           (.canExecute file)
           "ex"

           (.isSymbolicLink attrs)
           "ln"

           :else
           "fi")
         (get colour-options)
         (colourise file-name))))


(def ^:private defaults-colours
  {
   "pi" "0"                                                 ; fifo file
   "so" "0"                                                 ; socket file
   "bd" "0"                                                 ; block (buffered) special file
   "cd" "0"                                                 ; character (unbuffered) special file
   "or" "0"                                                 ; symbolic link pointing to a non-existent file (orphan)
   "mi" "0"                                                 ; non-existent file pointed to by a symbolic link (visible when you type ls -l)
   "ex" "0"                                                 ; file which is executable (ie. has 'x' set in permissions)
   "st" "0"                                                 ; ?
   "mh" "0"                                                 ; ?
   "rs" "0"                                                 ; ?
   "ow" "0"                                                 ; ?
   "tw" "0"                                                 ; ?
   "ca" "0"                                                 ; ?
   "sg" "0"                                                 ; ?
   "do" "0"                                                 ; ?
   "su" "0"                                                 ; ?
   })

"
Text attributes
0	All attributes off
1	Bold on
4	Underscore (on monochrome display adapter only)
5	Blink on
7	Reverse video on
8	Concealed on

Foreground colors
30	Black
31	Red
32	Green
33	Yellow
34	Blue
35	Magenta
36	Cyan
37	White

Background colors
40	Black
41	Red
42	Green
43	Yellow
44	Blue
45	Magenta
46	Cyan
47	White
"
