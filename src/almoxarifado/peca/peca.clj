(ns almoxarifado.peca.peca
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [almoxarifado.db :refer :all]))

(defentity peca)

(defn find-all []  (select peca))

(defn find-by-id [id] (select peca
                              (where {:id id})
                              (limit 1)))

(defn find-by-nome [nome]
  (select peca
          (where {:nome [like nome]})))

(defn get-available [id quantity]
  (select peca
          (where {:id id})
          (limit quantity)))

(defn create [nome qtdDisponivel qtdMinima]
  (insert peca
          (values {:nome nome
                   :qtdDisponivel qtdDisponivel
                   :qtdMinima :qtdMinima})))

(defn update-with-data [id nome qtdDisponivel qtdMinima]
  (update peca
          (set-fields {:nome nome
                       :qtdDisponivel qtdDisponivel
                       :qtdMinima :qtdMinima})
          (where {:id id})))