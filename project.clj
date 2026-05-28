(defproject calculadora-calorias "0.1.0-SNAPSHOT"
  :description "API Back-end para Calculadora de Calorias baseada em MVC funcional"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [compojure "1.7.0"]
                 [ring/ring-json "0.5.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.0"]
                 [environ "1.2.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot calculadora-calorias.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})