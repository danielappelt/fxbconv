(ns fxbconv.fxp)

(load-file "fxbconv.clj")

(def file-path (first *command-line-args*))
(fxbconv.impl/fxb2fxp file-path)
