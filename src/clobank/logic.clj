(ns clobank.logic
  (:require [clobank.db :as cb.db]))

(defn get-total [v]
  (->> v
       (map #(get % :amount))
       (reduce +)
       ))

(defn transform-group-with-sum
  [[k, v]]
  {
   k (get-total v)
   })

(defn group-total-by-category
  [ccs]
  (->> ccs
       (map :transactions)
       (reduce into [])
       (group-by :category)
       (map transform-group-with-sum)
       ))

(defn get-amount-of-transactions-customer-by-category
  [customer]
  (->> customer
       (map :credit-cards)
       (map group-total-by-category)
       ))

(defn group-transactions-by-customer
  [[guid customer]]
  {
   :customer    guid,
   :by-category (get-amount-of-transactions-customer-by-category customer)
   })

(defn get-all-credit-cards
  [customer]
  (->> customer
       :credit-cards
       first))

(defn transactions-by-category []
  (->> (cb.db/clobank-database)
       (map get-all-credit-cards)
       (group-total-by-category)
       println
       ))

(defn transactions-by-customer-category []
  (->> (cb.db/clobank-database)
       (group-by :guid)
       (map group-transactions-by-customer)
       println))