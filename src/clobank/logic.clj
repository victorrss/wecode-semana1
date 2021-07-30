(ns clobank.logic
  (:require [clobank.db :as cb.db]))

(require '[java-time])

(defn get-total [v]
  (->> v
       (map #(get % :amount))
       (reduce +)))

(defn transform-group-with-sum
  [[k, v]]
  {
   k (get-total v)})

(defn group-total-by-category
  [ccs]
  (->> ccs
       (map :transactions)
       (reduce into [])
       (group-by :category)
       (map transform-group-with-sum)))

(defn format-transaction
  [transaction]
  (-> transaction
      (dissoc :guid)
      (update :date #(str (java-time/format "dd/MM/yyyy hh:mm" %)))
      (update :amount #(str "R$ " %))))

(defn show-formatted-transactions
  [transaction]
  (println "\nData:" (:date transaction)
           "\nValor:" (:amount transaction)
           "\nEstabelecimento:" (:merchant transaction)
           "\nCategoria:" (:category transaction)))

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

(defn get-customers-credit-cards
  [customers]
  (->> customers
       :credit-cards
       first))

(defn transactions-by-category []
  (->> (cb.db/clobank-database)
       (map get-customers-credit-cards)
       (group-total-by-category)
       println
       ))

(defn transactions-by-customer-category []
  (->> (cb.db/clobank-database)
       (group-by :guid)
       (map group-transactions-by-customer)
       clojure.pprint/pprint))

(defn list-transactions []
  (->> (cb.db/clobank-database)
       (map get-customers-credit-cards)
       (map :transactions)
       (reduce into [])
       (map format-transaction)
       (mapv show-formatted-transactions)))

(defn get-customer-credit-cards
  [customer]
  (->> customer
       first
       :credit-cards
       first))

(defn get-transactions-of-credit-card
  [credit-card]
  {
   :credit-card  (:number credit-card)
   :transactions (map format-transaction (:transactions credit-card))
   }
  )

(defn group-credit-cards-by-customer
  [[guid customer]]
  {
   :customer     guid,
   :credit-cards (get-customer-credit-cards customer)
   })

(defn group-transactions-by-credit-cards [[guid-customer, credit-cards]]
  {
   :customer     guid-customer,
   :credit-cards (get-transactions-of-credit-card credit-cards)
   })

;(defn filter-transaction-month [month, transaction]
;  (->> transaction
;       (filter (fn [tr] (= (java-time/month :date tr) month))))
;       )
;
;
;
;(defn get-month-bill [month]
;  (->> (cb.db/clobank-database)
;       (group-by :guid)
;       (map group-credit-cards-by-customer)
;       (map group-transactions-by-credit-cards)
;       println
;       (map (filter-transaction-month month))
;       println
;       ))