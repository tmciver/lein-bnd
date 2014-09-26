(ns leiningen.bnd.util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private sep java.io.File/separator)
(def ^:private bnd-version "2.2.0")

;; shamelessly stolen from Clojure contrib:
;; http://clojuredocs.org/clojure_contrib/clojure.contrib.io/read-lines
(defn read-lines
  "Like clojure.core/line-seq but opens f with reader.  Automatically
  closes the reader AFTER YOU CONSUME THE ENTIRE SEQUENCE."
  [f]
  (let [read-line (fn this [^java.io.BufferedReader rdr]
                    (lazy-seq
                     (if-let [line (.readLine rdr)]
                       (cons line (this rdr))
                       (.close rdr))))]
    (read-line (io/reader f))))

(defn local-maven-path
  "Returns the path as string of the user's local Maven repository."
  []
  (let [user-home (System/getProperty "user.home")]
    (apply str (interpose sep [user-home ".m2" "repository"]))))

(defn project->uberjar-name
  "Returns the name of the uberjar file as string."
  [project]
  (->> (-> (map project [:name :version])
                          vec
                          (conj "standalone.jar"))
       (interpose "-")
       (apply str)))

(defn project->uberjar-path
  "Returns the path as string to the project's uberjar."
  [project]
  (let [ubername (project->uberjar-name project)
        uberpath (str (:target-path project) sep ubername)]
    uberpath))

(defn uberjar-exists?
  "Returns true if uberjar file exists; false otherwise."
  [project]
  (.exists (java.io.File. (project->uberjar-path project))))

(defn bundle-jar-exists?
  "Returns true if the bundle jar file exists, false otherwise."
  [project]
  (.exists (java.io.File. (project->uberjar-name project))))

(defn project->bnd-jar-path
  "Returns the path as string of the bnd OSGi bundler jar."
  [project]
  (let [repo-path (local-maven-path)
        bnd-id 'biz.aQute.bnd/bnd
        bnd-jar-name (str "bnd-" bnd-version ".jar")
        parent-path (->> (-> (str/split (namespace bnd-id) #"\.")
                             vec
                             (conj (name bnd-id)))
                         (interpose sep)
                         (apply str))]
    (apply str (interpose sep [repo-path parent-path bnd-version bnd-jar-name]))))

(defn project->bundle-map
  "Returns a map containing all data necessary for creating an OSGi bundle."
  [{{user-bundle-map :bnd} :osgi :as project}]
  ;; take only the stuff before the first dash (used mainly to drop "SNAPSHOT")
  (let [ver-str (-> (:version project)
                    (str/split #"-")
                    first)]
    (merge user-bundle-map {"Bundle-Version" ver-str})))

(defn keyword->bundle-key
  "Converts a Clojure keyword to a form acceptable as a Manifest key."
  [kw]
  (str (->> (-> kw name (clojure.string/split #"-"))
            (map str/capitalize)
            (interpose "-")
            (apply str))
       ":"))

(defn update-to-csv
  "Updates the map m so that the value of key k is converted to a CSV string."
  [m k]
  (update-in m [k] #(apply str (interpose "," %))))

(defn bundle-map->manifest-string
  "Returns a string representing the contents of a bnd bundle file
  created from the given bundle map."
  [bundle-map]
  (when-let [[[k v] & more] (seq bundle-map)]
    (str k ": " v (System/getProperty "line.separator")
         (bundle-map->manifest-string more))))

(defn create-tmp-bnd-properties-file
  "Creates a temporary bnd properties file from the given project map and returns its
  path."
  [project]
  (let [bundle-str (-> (project->bundle-map project)
                       (update-to-csv "Import-Package")
                       (update-to-csv "Export-Package")
                       bundle-map->manifest-string)
        tmp-file (java.io.File/createTempFile "bnd-" ".bnd")]
    (with-open [wrtr (io/writer tmp-file)]
      (.write wrtr bundle-str))
    (.getAbsolutePath tmp-file)))

(defn project->bnd-cmd-line
  "Creates the full command line needed to run the bnd OSGi bundler."
  [project]
  (apply str (interpose " "
                        ["java -jar"
                         (project->bnd-jar-path project)
                         "wrap --properties"
                         (create-tmp-bnd-properties-file project)
                         (project->uberjar-path project)])))
