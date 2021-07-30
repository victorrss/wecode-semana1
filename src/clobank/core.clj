(ns clobank.core
  (:require [clobank.logic :as cb.logic]))

(println "\nTransações por cliente e categoria: ")
(cb.logic/transactions-by-customer-category)

(println "\nTransações por categoria: ")
(cb.logic/transactions-by-category)

(println "\nTransações: ")
(cb.logic/list-transactions)

;(println "\nFatura do mês 7: ")
;(cb.logic/get-month-bill 7)
