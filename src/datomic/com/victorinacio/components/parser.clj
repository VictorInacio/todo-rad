(ns com.victorinacio.components.parser
  (:require
    [com.victorinacio.components.auto-resolvers :refer [automatic-resolvers]]
    [com.victorinacio.components.blob-store :as bs]
    [com.victorinacio.components.config :refer [config]]
    [com.victorinacio.components.datomic :refer [datomic-connections]]
    [com.victorinacio.components.delete-middleware :as delete]
    [com.victorinacio.components.save-middleware :as save]
    [com.victorinacio.model :refer [all-attributes]]
    [com.victorinacio.model.account :as account]
    [com.victorinacio.model.invoice :as invoice]
    [com.victorinacio.model.item :as item]
    [com.victorinacio.model.sales :as sales]
    [com.victorinacio.model.timezone :as timezone]
    [com.fulcrologic.rad.attributes :as attr]
    [com.fulcrologic.rad.blob :as blob]
    [com.fulcrologic.rad.database-adapters.datomic-common :as common]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.pathom3 :as pathom3]
    [datomic.api :as d]
    [mount.core :refer [defstate]]))

(defstate parser
  :start
  (let [env-middleware (-> (attr/wrap-env all-attributes)
                         (form/wrap-env save/middleware delete/middleware)
                         (common/wrap-env (fn [env] {:production (:main datomic-connections)}) d/db)
                         (blob/wrap-env bs/temporary-blob-store {:files         bs/file-blob-store
                                                                 :avatar-images bs/image-blob-store}))]
    (pathom3/new-processor config env-middleware []
      [automatic-resolvers
       form/resolvers
       (blob/resolvers all-attributes)
       account/resolvers
       invoice/resolvers
       item/resolvers
       sales/resolvers
       timezone/resolvers])))

(comment

  (parser {} '[(com.victorinacio.model.account/login)])
  (parser {} [{:account/all-accounts [:account/id :account/role {:account/primary-address [:address/street]}]}]))