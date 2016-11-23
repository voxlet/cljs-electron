(defproject hello-electron "0.1.0-SNAPSHOT"
  :description "A hello world application for electron"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [figwheel-sidecar "0.5.8"]
                 [reagent "0.5.1"]]
  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} ["resources/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"]
  :cljsbuild
  {:builds
   [{:source-paths ["src/main"]
     :id "main-dev"
     :compiler {:output-to "resources/main.js"
                :output-dir "resources/out"
                :optimizations :simple
                :pretty-print true
                :cache-analysis true}}
    {:source-paths ["src/ui"]
     :id "ui-dev"
     :figwheel true
     :compiler {:output-to "resources/public/js/ui-core.js"
                :output-dir "resources/public/js/ui-out"
                :source-map true
                :asset-path "js/ui-out"
                :optimizations :none
                :cache-analysis true
                :main "ui.core"}}
    {:source-paths ["src/main"]
     :id "main-release"
     :compiler {:output-to "resources/main.js"
                :optimizations :simple
                :pretty-print true
                :cache-analysis true}}
    {:source-paths ["src/ui"]
     :id "ui-release"
     :compiler {:output-to "resources/public/js/ui-core.js"
                :output-dir "resources/public/js/ui-release-out"
                :source-map "resources/public/js/ui-core.js.map"
                :optimizations :simple
                :cache-analysis true
                :main "ui.core"}}]})
