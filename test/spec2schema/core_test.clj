(ns spec2schema.core-test
  (:require [clojure.test :refer :all]
            [spec2schema.core :refer [spec->schema] :as sut]))

(deftest spec->schema-test
  (testing "Unnamed Schemas"
    (testing "Simple schemas"
      (testing "String"
        (is (= {:db/valueType   :db.type/string
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema string?))))
      (testing "Integer"
        (is (= {:db/valueType   :db.type/long
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema int?))))
      (testing "Float"
        (is (= {:db/valueType   :db.type/float
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema float?))))
      (testing "Boolean"
        (is (= {:db/valueType   :db.type/boolean
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema boolean?))))
      (testing "UUID"
        (is (= {:db/valueType   :db.type/uuid
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema uuid?))))
      (testing "Keyword"
        (is (= {:db/valueType   :db.type/keyword
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema keyword?))))
      (testing "Symbol"
        (is (= {:db/valueType   :db.type/symbol
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema symbol?))))
      (testing "Regex"
        (is (= {:db/valueType   :db.type/string
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:re #"[A-Z][a-z]+"])))))
    (testing "Collection Schemas"
      (testing "Sequence"
        (is (= {:db/valueType   :db.type/long
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:sequential int?]))))
      (testing "Set"
        (is (= {:db/valueType   :db.type/string
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:set string?]))))
      (testing "Vector"
        (is (= {:db/valueType   :db.type/keyword
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:vector keyword?]))))
      (testing "Sequence of Regexes"
        (is (= {:db/ident       :names
                :db/valueType   :db.type/string
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:names [:vector [:re #"[A-Z][a-z]+"]]]))))
      (testing "Map"
        (is (= [{:db/ident       :id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :name
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}]
               (sut/spec->schema [:map
                                  [:id uuid?]
                                  [:name string?]])))))
    (testing "Nested Collections"
      (testing "Sequence of maps"
        (is (= [{:db/cardinality :db.cardinality/many
                 :db/valueType   :db.type/ref}
                {:db/ident       :id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :name
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}]

               (sut/spec->schema [:vector [:map
                                           [:id uuid?]
                                           [:name string?]]]))))))
  (testing "Named Schemas"
    (testing "Simple schemas"
      (testing "String"
        (is (= {:db/ident       :name
                :db/valueType   :db.type/string
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:name string?]))))
      (testing "Integer"
        (is (= {:db/ident       :age
                :db/valueType   :db.type/long
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:age int?]))))
      (testing "Float"
        (is (= {:db/ident       :money
                :db/valueType   :db.type/float
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:money float?]))))
      (testing "Boolean"
        (is (= {:db/ident       :registered?
                :db/valueType   :db.type/boolean
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:registered? boolean?]))))
      (testing "UUID"
        (is (= {:db/ident       :id
                :db/valueType   :db.type/uuid
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:id uuid?]))))
      (testing "Keyword"
        (is (= {:db/ident       :kw
                :db/valueType   :db.type/keyword
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:kw keyword?]))))
      (testing "Symbol"
        (is (= {:db/ident       :sym
                :db/valueType   :db.type/symbol
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:sym symbol?]))))
      (testing "Regex"
        (is (= {:db/ident       :name
                :db/valueType   :db.type/string
                :db/cardinality :db.cardinality/one}
               (sut/spec->schema [:name [:re #"[A-Z][a-z]+"]])))))
    (testing "Collection Schemas"
      (testing "Sequence"
        (is (= {:db/ident       :numbers
                :db/valueType   :db.type/long
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:numbers [:sequential int?]]))))
      (testing "Set"
        (is (= {:db/ident       :nicknames
                :db/valueType   :db.type/string
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:nicknames [:set string?]]))))
      (testing "Vector"
        (is (= {:db/ident       :items
                :db/valueType   :db.type/keyword
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:items [:vector keyword?]]))))
      (testing "Map"
        (is (= [{:db/ident       :id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :name
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}]
               (sut/spec->schema [:map
                                  [:id uuid?]
                                  [:name string?]]))))
      (testing "Sequence of Regexes"
        (is (= {:db/ident       :names
                :db/valueType   :db.type/string
                :db/cardinality :db.cardinality/many}
               (sut/spec->schema [:names [:vector [:re #"[A-Z][a-z]+"]]])))))
    (testing "Ref Types"
      (testing "Single Cardinality"
        (is (= [{:db/ident       :child
                 :db/valueType   :db.type/ref
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :name
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}]
               (sut/spec->schema [:child [:map
                                          [:id uuid?]
                                          [:name string?]]]))))
      (testing "Multi Cardinality"
        (is (= [{:db/ident       :child
                 :db/valueType   :db.type/ref
                 :db/cardinality :db.cardinality/many}
                {:db/ident       :id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one}
                {:db/ident       :name
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}]
               (sut/spec->schema [:child [:vector [:map
                                                   [:id uuid?]
                                                   [:name string?]]]])))))
    (testing "Nested Schemas"
      (is (= [{:db/ident       :id
               :db/valueType   :db.type/uuid
               :db/cardinality :db.cardinality/one}
              {:db/ident       :name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :cars
               :db/valueType   :db.type/ref
               :db/cardinality :db.cardinality/many}
              {:db/ident       :brand
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :model
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :engines
               :db/valueType   :db.type/ref
               :db/cardinality :db.cardinality/many}
              {:db/ident       :engine-code
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :displacement
               :db/valueType   :db.type/double
               :db/cardinality :db.cardinality/one}]
             (sut/spec->schema [:map
                                [:id uuid?]
                                [:name string?]
                                [:cars [:vector
                                        [:map
                                         [:brand string?]
                                         [:model string?]
                                         [:engines [:vector
                                                    [:map
                                                     [:engine-code string?]
                                                     [:displacement double?]]]]]]]]))))))
