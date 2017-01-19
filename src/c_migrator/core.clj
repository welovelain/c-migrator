(ns c-migrator.core
  (:use qbits.hayt)
  (:require [ragtime.repl :as repl]
	    [com.welovelain.ragtime-alia.core :as ra]
	    [qbits.alia :as alia]
            [ragtime.core :as rcore]
	    [clojure.java.io :as io])
  (:import [java.util Properties]))

(defn- read-properties 
"Read properties file from properties folder. Name of passed configname should be the same as properties filename (without extension).
 File contains properties: 'host' for db host, 'migrations_dir' for migrations directory, 'keyspace' for keyspace."
  [configname]
    (let [path (str "properties/" configname ".properties")
 	resource (io/resource path)
 	is (io/input-stream resource)
	props (Properties.)]
     (.load props is) 
      props))

(defn- connect [session keyspace & opts]
  "Opts are: ragtime migrations table name. 'ragtime_migrations' is default value"
  (alia/execute session (use-keyspace keyspace))
  (apply ra/alia-database session opts))

(defn- getconfig [session keyspace migrations-dir]
  {:datastore (connect session keyspace)
   :migrations (ra/load-directory migrations-dir)})

(defn- parse-amount-or-id 
"Returns as integer if int, else as string"
  [amount-or-id]
  (try (Integer. amount-or-id)
  (catch Exception _ (str amount-or-id))))

(defn- found-id? [id session] 
  "Find id in 'ragtime_migrations' table. Return false if not found"
  (let [foundrecord (alia/execute session (select :ragtime_migrations
                            		    (where {:id id})
					    (columns :id "id")))]
  (not(empty? foundrecord))))

(defn- disconnect [cluster session]
  (println "All done, closing connection...")
  (alia/shutdown session)
  (alia/shutdown cluster))

(defn migrate 
"Migrate all. If db contains migration ids, it starts from next one from last found"
  [configname]
  (let [props (read-properties configname)
	host (get props "host")
	port (Integer. (get props "port"))
	keyspace (get props "keyspace")
	migrations-dir (get props "migrations_dir")
	cluster (alia/cluster {:contact-points [host] :port port})
	session (alia/connect cluster)
	config (getconfig session keyspace migrations-dir)]
     (repl/migrate config)
     (disconnect cluster session)))

(defn rollback
  "Rollbacks 1 migration, N migrations or rollbacks until migration with proposed id."
  ([configname]
    (rollback configname 1))
  ([configname amount-or-id] 
   (let [props (read-properties configname)
	host (get props "host")
	port (Integer. (get props "port"))
	keyspace (get props "keyspace")
	migrations-dir (get props "migrations_dir")
	cluster (alia/cluster {:contact-points [host] :port port})
	session (alia/connect cluster)
	config (getconfig session keyspace migrations-dir)
	parsed-amount-or-id (parse-amount-or-id amount-or-id)]
     (if (integer? parsed-amount-or-id) 
       (repl/rollback config parsed-amount-or-id) ;; if amount-or-id is integer then just call repl/rollback function
       (if (found-id? parsed-amount-or-id session) ;; else check if such id exists in database. if not done, ragtime may erase the whole db
	  (repl/rollback config parsed-amount-or-id)
	  (println "Error: id not found in database")))
     (disconnect cluster session))))

