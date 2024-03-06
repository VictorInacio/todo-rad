(ns com.victorinacio.components.datomic
  (:require
    [com.fulcrologic.rad.database-adapters.datomic :as datomic]
    [mount.core :refer [defstate]]
    [com.victorinacio.model :refer [all-attributes]]
    [com.victorinacio.components.config :refer [config]]))

(defstate ^{:on-reload :noop} datomic-connections
  :start
  (datomic/start-databases all-attributes config))
