(ns fxbconv.carxs)

(load-file "fxbconv.clj")

(def file-path (first *command-line-args*))
(fxbconv.impl/fxb2carxs file-path)
