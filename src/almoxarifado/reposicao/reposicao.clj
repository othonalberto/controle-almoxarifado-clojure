(ns almoxarifado.reposicao.reposicao
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [almoxarifado.db :refer :all]))

(defentity reposicao)

(defn find-by-id [id]
  (select reposicao
          (where {:id id})
          (limit 1)))

(defn create [datahora qtd idPeca idAviso]
  (insert reposicao
          (values {:datahoraChegada datahora
                   :quantidadeReposta qtd
                   :idPeca idPeca
                   :idAviso idAviso})))
