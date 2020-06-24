(ns fxb2fxp
  (:require
   [clojure.java.io :as io]))

(def file-path (first *command-line-args*))
;; Magic chunk char sequence: CcnK
(def magic-chunk '(67 99 110 75))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(def fxb (vec (file->bytes (io/file file-path))))

(def positions (rest (map first
                          (filter #(= (rest %) magic-chunk)
                                  (map list
                                       (range)
                                       fxb (rest fxb) (rest (rest fxb)) (rest (rest (rest fxb))))))))

(defn save-file
  [[index start end]]
  (with-open [ofile (io/output-stream (str file-path "_" index ".fxp"))]
    (.write ofile (byte-array (subvec fxb start end)))))

(run! save-file (map list (range) positions (rest positions)))
