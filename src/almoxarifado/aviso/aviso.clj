(ns almoxarifado.aviso.aviso
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [almoxarifado.db :refer :all]))

(defentity AvisoQuantidadeMinima)

(defn find-all []
  (select AvisoQuantidadeMinima))

(defn find-all-open []
  (exec-raw
   ["SELECT a.dataHoraAcontecimento, a.id, r.idPeca, p.nome
    FROM
        (AvisoQuantidadeMinima AS a JOIN Retirada as R ON a.idRetiradaVinculada = r.Id)
        LEFT JOIN PECA as p ON r.idPeca = p.Id
        WHERE a.status != 'fechada'"] :results))

(defn find-by-id [id]
  (select AvisoQuantidadeMinima
          (where {:id id})
          (limit 1)))

(defn create [datahoraAcontecimento idRetiradaVinculada dataHoraEnvio]
  (insert AvisoQuantidadeMinima
          (values {:datahoraAcontecimento datahoraAcontecimento
                   :idRetiradaVinculada idRetiradaVinculada
                   :dataHoraEnvio dataHoraEnvio
                   :status "aberta"})))

(defn closeAviso [idAviso]
  (update AvisoQuantidadeMinima
          (set-fields {:status "fechada"})
          (where {:id idAviso})))
