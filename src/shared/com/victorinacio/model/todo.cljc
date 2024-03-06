(ns com.victorinacio.model.todo
  (:refer-clojure :exclude [name])
  (:require
    #?@(:clj
        [[com.victorinacio.model.authorization :as exauth]
         [com.victorinacio.components.database-queries :as queries]]
        :cljs
        [[com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]])
    [clojure.string :as str]
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.authorization :as auth]
    [com.fulcrologic.rad.middleware.save-middleware :as save-middleware]
    [com.fulcrologic.rad.blob :as blob]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [com.fulcrologic.rad.type-support.date-time :as datetime]))

(defattr id :todo/id :uuid
  {ao/identity?                                     true
   ;; NOTE: These are spelled out so we don't have to have either on classpath, which allows
   ;; independent experimentation. In a normal project you'd use ns aliasing.
   ao/schema                                        :production
   :com.fulcrologic.rad.database-adapters.sql/table "account"})


(defattr completed? :todo/completed? :boolean
  {ao/identities                                          #{:todo/id}
   ao/schema                                              :production
   :com.fulcrologic.rad.database-adapters.sql/column-name "completed"
   fo/default-value                                       true})

(defattr name :todo/name :string
  {fo/field-label "Name"
   ;::report/field-formatter (fn [v] (str "ATTR" v))
   ao/identities  #{:todo/id}
   ;ao/valid?      (fn [v] (str/starts-with? v "Bruce"))
   ;::attr/validation-message                                 (fn [v] "Your name's not Bruce then??? How 'bout we just call you Bruce?")
   ao/schema      :production

   ao/required?   true})

(defattr all-todos :todo/all-todos :ref
  {ao/target     :todo/id
   ao/pc-output  [{:todo/all-todos [:todo/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   #?(:clj
                      {:todo/all-todos (queries/get-all-todos env query-params)}))})

(def attributes [id name completed? all-todos])
