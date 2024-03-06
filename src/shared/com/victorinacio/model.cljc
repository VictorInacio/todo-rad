(ns com.victorinacio.model
  (:require
    [com.victorinacio.model.timezone :as timezone]
    [com.victorinacio.model.account :as account]
    [com.victorinacio.model.item :as item]
    [com.victorinacio.model.invoice :as invoice]
    [com.victorinacio.model.line-item :as line-item]
    [com.victorinacio.model.address :as address]
    [com.victorinacio.model.category :as category]
    [com.victorinacio.model.file :as m.file]
    [com.victorinacio.model.sales :as sales]
    [com.fulcrologic.rad.attributes :as attr]))

(def all-attributes (vec (concat
                           account/attributes
                           address/attributes
                           category/attributes
                           item/attributes
                           invoice/attributes
                           line-item/attributes
                           m.file/attributes
                           sales/attributes
                           timezone/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
