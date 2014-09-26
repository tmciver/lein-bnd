# lein-bnd

A Leiningen plugin to facilitate creation of an OSGi bundle.  Uses [BND](http://www.aqute.biz/Bnd).

## Usage

This plugin requires some data to be added to the project file.  Add the
following to your project file changing the example data to suit your project:

```clojure
:osgi {:bnd {:bundle-symbolicName 'com.example.mybundle
               :bundle-activator 'com.example.mybundle.MyActivator
               :export-package '(com.example.mybundle)
               :import-package '(org.osgi.framework
                                ;; other packages
                                )}}
```

Run:

    $ lein bnd bundle

to create the OSGi bundle.  The bundle jar will be placed in the project root.

## License

Copyright © 2014 Tim McIver

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
