(ns spec2schema.core
  (:require [clojure.core.match :refer [match]]))


(def primitive-type #{int? uuid? float? double? boolean? keyword? symbol? string?})

(def schema-type {int?     :db.type/long
                  uuid?    :db.type/uuid
                  float?   :db.type/float
                  double?  :db.type/double
                  boolean? :db.type/boolean
                  keyword? :db.type/keyword
                  symbol?  :db.type/symbol
                  string?  :db.type/string})

(defn spec->schema
  ([spec] (spec->schema spec true))
  ([spec root?] (match spec
                       ;; Case for primitive type
                       (?type :guard primitive-type) {:db/valueType   (schema-type ?type)
                                                      :db/cardinality :db.cardinality/one}
                       ;; Case for Regexes
                       [:re _] {:db/valueType   :db.type/string
                                :db/cardinality :db.cardinality/one}

                       ;; Case for sequences
                       [(:or :sequential :vector :set) ?spec] (let [subspecs (spec->schema ?spec false)]
                                                                (cond
                                                                  (sequential? subspecs) (assoc-in subspecs [0 :db/cardinality] :db.cardinality/many)
                                                                  (map? subspecs) (assoc subspecs :db/cardinality :db.cardinality/many)))

                       ;; Case for maps
                       [:map & ?specs] (let [subspecs (flatten (mapv #(spec->schema % false) ?specs))]
                                         (if root?
                                           subspecs
                                           (into [{:db/valueType   :db.type/ref
                                                   :db/cardinality :db.cardinality/one}] subspecs)))


                       ;; Case for named specs
                       [?name ?spec] (let [subspecs (spec->schema ?spec false)]
                                       (cond
                                         ;; Case where the named spec is a ref, thus it's returned a collection.
                                         ;; The first attribute definition in the sequence should be the definition of the ref attribute
                                         (sequential? subspecs) (assoc-in (vec subspecs) [0 :db/ident] ?name)
                                         (map? subspecs) (assoc subspecs :db/ident ?name)))


                       :else (throw (ex-info "Don't know how to convert spec." {:quantit/error-type :quantit/invalid-schema
                                                                                :quantit/error-data spec})))))
