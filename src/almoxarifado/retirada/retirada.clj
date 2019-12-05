(ns almoxarifado.retirada.retirada
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [almoxarifado.db :refer :all]))

(defentity retirada)

(defn find-all-open-timeline []
  (exec-raw
   ["SELECT p.nome, r.idPeca, r.datahoraRetirada, r.quantidade, r.id
      FROM Peca as P
        JOIN Retirada as R on P.id = r.IdPeca
      WHERE R.status != 'fechada' AND r.idAlmoxarife IS NULL"]
   :results))

(defn find-all-open [idFuncionario]
  (def query (str "SELECT p.nome, r.id, r.status
    FROM Peca as P
      JOIN Retirada as R on P.id = r.IdPeca
    WHERE R.status != 'fechada' AND r.idFuncionario = " idFuncionario))
  (exec-raw query :results))

(defn find-all-closed []
  (exec-raw
   ["SELECT p.nome, r.datahoraRetirada FROM  Peca as P JOIN Retirada as R on P.id = r.IdPeca WHERE R.status = 'fechada'"]
   :results))

(defn find-by-id [id]
  (select retirada
          (where {:id id})
          (limit 1)))

(defn find-by-id-peca [idPeca]
  (select retirada
          (where {:IdPeca idPeca})))

(defn find-by-id-almoxarife [idAlmoxarife]
  (def query (str "SELECT p.nome, r.datahoraRetirada FROM  Peca as P JOIN Retirada as R on P.id = r.IdPeca WHERE R.idAlmoxarife = " idAlmoxarife))
  (exec-raw query :results))

(defn find-by-id-funcionario [idFuncionario]
  (def query (str "SELECT p.nome, r.datahoraRetirada FROM  Peca as P JOIN Retirada as R on P.id = r.IdPeca WHERE R.idFuncionario = " idFuncionario))
  (exec-raw query :results))

(defn get-available [id quantity]
  (select retirada
          (where {:id id})
          (limit quantity)))

(defn create [datahoraRetirada IdFuncionario idPeca quantidade]
  (insert retirada
          (values {:datahoraRetirada datahoraRetirada
                   :IdFuncionario IdFuncionario
                   :idPeca idPeca
                   :quantidade quantidade
                   :status "aberta"})))

(defn update-with-data [id datahoraRetirada idAlmoxarife idFuncionario idPeca quantidade]
  (update retirada
          (set-fields {:datahoraRetirada datahoraRetirada
                       :idAlmoxarife idAlmoxarife
                       :idFuncionario idFuncionario
                       :idPeca idPeca
                       :quantidade quantidade
                       :status "fechada"})
          (where {:id id})))

(defn update-with-data-ok [id datahoraRetirada idAlmoxarife idFuncionario idPeca quantidade]
  (update retirada
          (set-fields {:datahoraRetirada datahoraRetirada
                       :idAlmoxarife idAlmoxarife
                       :idFuncionario idFuncionario
                       :idPeca idPeca
                       :quantidade quantidade
                       :status "ok"})
          (where {:id id})))