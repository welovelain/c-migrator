(defproject c-migrator "0.1.0-SNAPSHOT"
  :description "Migrates and rollbacks cassandra migrations with the use of ragtime and ragtime-alia"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
		[org.clojure/clojure "1.8.0"]
		[ragtime/ragtime.core "0.6.3"]
		[com.welovelain.ragtime-alia "0.1.3-SNAPSHOT"]
		[cc.qbits/alia-all "4.0.0-beta4"]
		]
  :main ^:skip-aot c-migrator.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :aliases {"migrate"  ["run" "-m" "c-migrator.core/migrate"]
            "rollback" ["run" "-m" "c-migrator.core/rollback"]})
