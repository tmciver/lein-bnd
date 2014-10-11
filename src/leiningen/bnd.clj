(ns leiningen.bnd
  (:use leiningen.bnd.util
        [leiningen.uberjar :only [uberjar]])
  (:require [leiningen.pom :as pom]))

(defn help
  "Prints a help message to standard out."
  []
  (println "usage: lein bnd bundle"))

(defn bundle
  "Create an OSGi bundle from the current project's jar."
  [project]
  (when (not (uberjar-exists? project))
    (uberjar project))
  (if (pom/snapshot? project)
    (println "Can't create bundle: OSGi does not support snapshot versions.")
    (let [cmd-line (bnd-cmd-line project)
          process (. (Runtime/getRuntime) exec cmd-line (into-array String nil) (java.io.File. (:root project)))]
      (if (= 0 (.waitFor process))
        (read-lines (.getInputStream process))
        (read-lines (.getErrorStream process))))))

(defn ^{:subtasks [#'bundle #'help]}
  bnd
  "Main entry point to the bnd plugin."
  ([project]
     (help))
  ([project subtask & args]
     (case subtask
       "bundle" (bundle project)
       "help" (help))))
