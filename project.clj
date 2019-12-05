(defproject almoxarifado "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :main almoxarifado.handler
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]
                 [korma "0.4.3"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [clj-pdf "2.4.0"]
                 [org.martinklepsch/s3-beam "0.6.0-alpha5"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-try "0.4.3"]]
  :ring {:handler almoxarifado.handler/app
         :auto-reload? true}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})