;; (ns fxb2fxp.carxs
;;   (:require
;;    [clojure.data.xml :refer :all]))

;; (def type-offset (* 2 4))
;; (def id-offset (* 4 4))
;; (def name-offset (* 7 4))
;; (def name-length 28)
;; (def chunk-offset (+ name-offset name-length 4))

;; (defn bytes->string
;;   [bytes start length]
;;   (str (map char (subvec bytes start (+ start length)))))

;; ;; TODO: do we have to handle the sign?!?
;; (defn bytes->int
;;   "Convert endianness (big to little) and then sum up the byte values."
;;   [bytes]
;;     (reduce + (map #(bit-shift-left %2 (* %1 8)) (range) (reverse bytes))))

;; (defn bytes->float
;;   "Convert to int and from there to float using Java."
;;   [bytes]
;;   (Float/intBitsToFloat (bytes->int bytes)))

;; (defn bytes->values
;;   [bytes is-chunk]
;;   (if is-chunk
;;     ;; Start at chunk offset and interpret each four bytes as float value.
;;     (map bytes->float (partition 4 (drop chunk-offset bytes)))
;;     (
;;      ;; TODO
;;      )))

;; ;; (def test-id [0x39 0x32 0x30 0x31])
;; (defn fxp->carxs
;;   [path index bytes]
;;   (let [unique-id (bytes->int (subvec bytes id-offset 4))
;;         name (bytes->string bytes name-offset name-length)
;;         values (bytes->values bytes (= "FPCh" (bytes->string bytes type-offset 4)))
;;         tags (element :CARLA-PRESET {:VERSION "2.0"}
;;                       (element :Info
;;                                (element :Type "VST2")
;;                                (element :UniqueID unique-id))
;;                       (doseq [i (range (count values)) v values]
;;                              (element :Data
;;                                       (element :Parameter
;;                                                (element :Index (str i))
;;                                                (element :value (str v))))))]
;;     (with-open [out-file (java.io.FileWriter. (str path "_" index "_" name ".carxs"))]
;;       (emit tags out-file))))

;; ;; [:CARLA-PRESET {:VERSION "2.0"}
;; ;;  [:Info
;; ;;   [:Type "VST2"]
;; ;;   [:UniqueID unique-id]]
;; ;;  [:Data
;; ;;   (doseq (range) values
;; ;;          [:Parameter
;; ;;           [:Index (str %1)]
;; ;;           [:Value (str %2)]]
;; ;;          ]]]


;; ;; (defn reorder
;; ;;   [bytes]
;; ;;   "Reverse a sequence of pairs of bytes (middle-endian to big endian)"
;; ;;   (flatten (map (juxt second first) (partition 2 bytes))))

;; ;; (defn bytes->int
;; ;;   [bytes]
;; ;;   (let [sign (= 128 (bit-and (last bytes) 128))
;; ;;         v (concat (drop-last bytes) (list (bit-and (last bytes) 127)))]
;; ;;     (reduce + (map #(bit-shift-left %2 (* %1 8)) (range) v))))

(ns fxbconv.carxs)

(load-file "fxbconv.clj")

(def file-path (first *command-line-args*))
(fxbconv.impl/fxb2carxs file-path)
