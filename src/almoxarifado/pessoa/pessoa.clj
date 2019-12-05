(ns almoxarifado.pessoa.pessoa

  (:require [korma.db :refer :all]

            [korma.core :refer :all]

            [almoxarifado.db :refer :all]))

(defentity pessoa)

(defn create [cpf email cargo nome senha]

  (insert pessoa

          (values {:cpf cpf

                   :email email

                   :cargo cargo

                   :nome nome

                   :senha senha})))

(defn find-by-id [id]

  (select pessoa

          (where {:id id})

          (limit 1)))

(defn find-by-email [email]

  (select pessoa

          (where {:email email})

          (limit 1)))