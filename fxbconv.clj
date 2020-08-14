(ns fxbconv.impl
  (:require
   [clojure.java.io :as io]
   [clojure.data.xml :as xml]))

;; Magic chunk char sequence: CcnK
(def magic-chunk '(67 99 110 75))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn fxb->fxp_offsets
  [fxb]
  ;; Ignore the fxb header and just keep the offsets of the included fxps
  (rest (map first
             ;; Keep only those list entries where (= (byteN ... byteN+3) magic-chunk)
             (filter #(= (rest %) magic-chunk)
                     ;; Create list entries of the form (pos byteN byteN+1 byteN+2 byteN+3)
                     (map list
                          (range)
                          fxb (rest fxb) (rest (rest fxb)) (rest (rest (rest fxb))))))))

(defn save-fxp
  [[file-path index fxb start end]]
  (with-open [ofile (io/output-stream (str file-path "_" index ".fxp"))]
    (.write ofile (byte-array (subvec fxb start end)))))

(defn fxb2fxp
  [file-path]
  (let [fxb (vec (file->bytes (io/file file-path)))
        offsets (fxb->fxp_offsets fxb)]
    (run! save-fxp (map #(list file-path %1 fxb %2 %3) (range) offsets (rest offsets)))))

;; Define byte offsets in individual preset headers
(def type-offset (* 2 4))
(def id-offset (* 4 4))
(def name-offset (* 7 4))
(def name-length 28)
(def data-offset (+ name-offset name-length))

(defn bytes->string
  [bytes start length]
  (clojure.string/trim (apply str (map char (subvec bytes start (+ start length))))))

;; TODO: do we have to handle the sign?!?
(defn bytes->int
  "Convert endianness (big to little) and then sum up the byte values."
  [bytes]
    (reduce + (map #(bit-shift-left (bit-and 0xff %2) (* %1 8)) (range) (reverse bytes))))

(defn bytes->float
  "Convert to int and from there to float using Java."
  [bytes]
  (Float/intBitsToFloat (bytes->int bytes)))

(defn bytes->values
  [bytes is-chunk]
  (if is-chunk
    ;; TODO
    (prn "Support for chunk presets is still missing!")
    ;; Start at data-offset and interpret each four bytes as float value.
    (map bytes->float (partition 4 (drop data-offset bytes)))))

(defn fxp->carxs
  [path index bytes]
  (let [unique-id (bytes->int (subvec bytes id-offset (+ id-offset 4)))
        name (bytes->string bytes name-offset name-length)
        values (bytes->values bytes (= (bytes->string bytes type-offset 4) "FPCh"))
        tags [:CARLA-PRESET {:VERSION "2.0"}
              [:Info
               [:Type "VST2"]
               [:UniqueID unique-id]]
              (vec (concat [:Data [:Active "Yes"]]
                           (map #(vector :Parameter
                                         [:Index %1]
                                         [:Value (str %2)]) (range) values)))]]
    ;; (println (str path "_" index "_" name ".carxs"))
    (with-open [out-file (io/writer (str path "_" index ".carxs"))] ;; "_" name
      (xml/emit (xml/sexp-as-element tags) out-file))))

(defn save-carxs
  [[file-path index fxb start end]]
  (fxp->carxs file-path index (subvec fxb start end)))

(defn fxb2carxs
  [file-path]
  (let [fxb (vec (file->bytes (io/file file-path)))
        offsets (fxb->fxp_offsets fxb)]
    (run! save-carxs (map #(list file-path %1 fxb %2 %3) (range) offsets (rest offsets)))))
