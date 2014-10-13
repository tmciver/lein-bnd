(defproject lein-bnd "0.1.1-SNAPSHOT"
  :description "A Leiningen plugin to facilitate creation of an OSGi bundle."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[biz.aQute.bnd/bnd "2.2.0"]]
  :eval-in-leiningen true
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]])
