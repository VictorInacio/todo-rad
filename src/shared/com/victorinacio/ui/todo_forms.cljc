(ns com.victorinacio.ui.todo-forms
  (:require
    [clojure.string :as str]
    [taoensso.timbre :as log]
    [com.victorinacio.model :as model]
    [com.victorinacio.model.todo :as todo]
    [com.victorinacio.model.timezone :as timezone]
    [com.victorinacio.ui.address-forms :refer [AddressForm]]
    [com.victorinacio.ui.file-forms :refer [FileForm]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [com.fulcrologic.rad.semantic-ui-options :as suo]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div label input]]
       :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div label input]])
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [com.fulcrologic.rad.attributes :as attr]))

;; NOTE: Limitation: Each "storage location" requires a form. The ident of the component matches the identity
;; of the item being edited. Thus, if you want to edit things that are related to a given entity, you must create
;; another form entity to stand in for it so that its ident is represented.  This allows us to use proper normalized
;; data in forms when "mixing" server side "entities/tables/documents".
(form/defsc-form TodoForm [this props]
  {fo/id             todo/id
   fo/attributes     [todo/name
                      todo/completed?]
   fo/default-values {:todo/name         ""
                      :todo/completed? false}
   fo/route-prefix   "todo"
   fo/title          "Edit Todo"})


(report/defsc-report TodoList [this props]
  {ro/title               "All Todos"
   suo/rendering-options  {
                           ;suo/action-button-render      (fn [this {:keys [key onClick label]}]
                           ;                                (when (= key ::new-account)
                           ;                                  (dom/button :.ui.tiny.basic.button {:onClick onClick}
                           ;                                    (dom/i {:className "icon user"})
                           ;                                    label)))
                           suo/body-class                ""
                           suo/controls-class            ""
                           suo/layout-class              ""
                           suo/report-table-class        "ui very compact celled selectable table"
                           suo/report-table-header-class (fn [this i] (case i
                                                                        0 ""
                                                                        1 "center aligned"
                                                                        "collapsing"))
                           suo/report-table-cell-class   (fn [this i] (case i
                                                                        0 ""
                                                                        1 "center aligned"
                                                                        "collapsing"))}
   ro/form-links          {todo/name TodoForm}
   ro/column-headings     {:todo/name "Todo Name"}
   ro/columns             [todo/name todo/completed?]
   ro/row-pk              todo/id
   ro/source-attribute    :todo/all-todos
   ro/row-visible?        (fn [{::keys [filter-name]} {:todo/keys [name]}]
                            (let [nm     (some-> name (str/lower-case))
                                  target (some-> filter-name (str/trim) (str/lower-case))]
                              (or
                                (nil? target)
                                (empty? target)
                                (and nm (str/includes? nm target)))))
   ro/run-on-mount?       true

   ro/initial-sort-params {:sort-by          :todo/name
                           :ascending?       false
                           :sortable-columns #{:todo/name :todo/completed?}}

   ro/controls            {::new-todo   {:type   :button
                                            :local? true
                                            :label  "New Todo"
                                            :action (fn [this _] (form/create! this TodoForm))}
                           ::search!       {:type   :button
                                            :local? true
                                            :label  "Filter"
                                            :class  "ui basic compact mini red button"
                                            :action (fn [this _] (report/filter-rows! this))}
                           ::filter-name   {:type        :string
                                            :local?      true
                                            :placeholder "Type a partial name and press enter."
                                            :onChange    (fn [this _] (report/filter-rows! this))}
                           :show-completed? {:type          :boolean
                                            :local?        true
                                            :style         :toggle
                                            :default-value false
                                            :onChange      (fn [this _] (control/run! this))
                                            :label         "Show Completed Todos?"}}

   ro/control-layout      {:action-buttons [::new-todo]
                           :inputs         [[::filter-name ::search! :_]
                                            [:show-inactive?]]}



   ro/route               "Todos"})

(comment

  (comp/get-query TodoList-Row))
