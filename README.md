# lein-bnd

A Leiningen plugin to facilitate creation of an OSGi bundle.  Uses
[BND](http://www.aqute.biz/Bnd).

## Usage

This plugin requires some data to be added to the project file.  Add the
following to your project file changing the example data to suit your project:

```clojure
:bnd {"Bundle-SymbolicName" ~'com.example.mybundle
      "Bundle-Activator" ~'com.example.mybundle.MyActivator
      "Export-Package" [com.example.mybundle]
      "Import-Package" [org.osgi.framework
                       ;; other packages
                       ]}
```

Run:

    $ lein bnd bundle

to create the OSGi bundle.  The bundle jar will be placed in the project root.

Note that currently the bnd plugin uses the [*wrap* version of the BND command
line application](http://www.aqute.biz/Bnd/CommandLine).  Also note that this
command operates on an uberjar of the project code and it will be created if it
does not already exist.

## License

Copyright © 2014 Tim McIver

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
