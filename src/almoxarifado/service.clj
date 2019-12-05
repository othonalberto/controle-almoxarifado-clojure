(ns almoxarifado.service
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [almoxarifado.peca.peca :as peca]
            [almoxarifado.retirada.retirada :as retirada]
            [almoxarifado.pessoa.pessoa :as pessoa]
            [almoxarifado.aviso.aviso :as aviso]
            [almoxarifado.reposicao.reposicao :as reposicao]
            [almoxarifado.pdfGenerator :as pdfGen]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :as ring-resp]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn getRequestThenCreatePeca [req]
  (let [nome (get-in req [:body "nome"])
        qtdDisponivel (get-in req [:body "qtdDisponivel"])
        qtdMinima (get-in req [:body "qtdMinima"])
        peca (peca/create nome qtdDisponivel qtdMinima)]

    (if peca
      (ring-resp/status (ring-resp/response {}) 201)
      (ring-resp/status (ring-resp/response {}) 404))))

(defn getRequestThenCreatePessoa [req]
  (let [cpf (get-in req [:body "cpf"])
        email (get-in req [:body "email"])
        cargo (get-in req [:body "cargo"])
        nome (get-in req [:body "nome"])
        senha (get-in req [:body "senha"])
        pessoaID (get (pessoa/create cpf email cargo nome senha) :generated_key)]

    (if pessoaID
      (ring-resp/status (ring-resp/response {}) 201)
      (ring-resp/status (ring-resp/response {}) 404))))

(defn getPessoaByEmail [email]
  (let [pessoa (pessoa/find-by-email email)]

    (if (not-empty pessoa)
      (ring-resp/status (ring-resp/response pessoa) 200)
      (ring-resp/status (ring-resp/response {}) 404))))

(defn getInfosPeca [nome]
  (let [nomeWithLike (str "%" nome "%%")
        pecas (peca/find-by-nome nomeWithLike)]

    (if (not-empty pecas)
      (ring-resp/response pecas)
      (ring-resp/status (ring-resp/response {}) 404))))

(defn generateAndSavePdfRetirada [idRetirada]
  (pdfGen/generateAndSavePdfRetirada idRetirada)
  )

(defn generateAndSavePdfAlert [idRetirada]
  (pdfGen/generateAndSavePdfAviso idRetirada)
  )

(defn generateAndSavePdfReposicao [reposicao]
  (pdfGen/generateAndSavePdfReposicao reposicao)
  )

(defn alertMinQuantity [retiradaId]
  (let [datetime (c/to-sql-date (t/now))
        idAlertaCriado (get (aviso/create datetime
                                          retiradaId
                                          datetime) :generated_key)]

    idAlertaCriado))

(defn decreaseAvailability [peca updatedQtdDisponivel]
  (peca/update-with-data (get peca :id)
                         (get peca :nome)
                         updatedQtdDisponivel
                         (get peca :qtdMinima)))

(defn openOrder [req]
  (let [id (get-in req [:body "idPeca"])
        needed (get-in req [:body "qtdRequerida"])
        funcionario (get-in req [:body "idFuncionario"])
        peca (nth (peca/find-by-id id) 0)
        available (get peca :qtdDisponivel)
        left (- needed available)]

    (if (< available needed)
      (ring-resp/status
       (ring-resp/response {:qtdDisponivel available}) 404)

      (let [retiradaId (get (retirada/create (c/to-sql-date (t/now))
                                             funcionario
                                             id
                                             needed) :generated_key)]

        (decreaseAvailability peca (- available needed))

        (when-not (> left (get peca :qtdMinima))
          (generateAndSavePdfAlert (alertMinQuantity retiradaId)))

        (ring-resp/response {})))))

(defn closeOrder [req]
  (let [idRetirada (get-in req [:body "idRetirada"])
        idAlmoxarife (get-in req [:body "idAlmoxarife"])
        retirada (nth (retirada/find-by-id idRetirada) 0)
        updatedRetirada (retirada/update-with-data-ok
                         idRetirada
                         (c/to-sql-date (t/now))
                         idAlmoxarife
                         (get retirada :idFuncionario)
                         (get retirada :idPeca)
                         (get retirada :quantidade))]

    (generateAndSavePdfRetirada idRetirada))
  (ring-resp/response {}))

(defn getPecasParaRepor []
  (let [aRepor (vec (aviso/find-all-open))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:id :idAviso,
                     :nome :nomePeca,
                     :dataHoraAcontecimento :dataAviso}) aRepor))
    (ring-resp/status (ring-resp/response saida) 200)))

(defn historicoRetiradas []
  (let [retiradas (vec (retirada/find-all-closed))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:nome :nomePeca,
                     :datahoraRetirada :dataPedido}) retiradas))

    (ring-resp/status (ring-resp/response saida) 200)))

(defn getRetiradasOpen []
  (let [retiradas (vec (retirada/find-all-open-timeline))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:id :idRetirada,
                     :nome :nomePeca,
                     :datahoraRetirada :dataPedido
                     :quantidade :qtdRequerida}) retiradas))

    (ring-resp/status (ring-resp/response saida) 200)))

(defn getRetiradasOpenByFuncionario [idFuncionario]
  (let [retiradas (vec (retirada/find-all-open idFuncionario))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:id :idRetirada,
                     :nome :nomePeca}) retiradas))
    (ring-resp/status (ring-resp/response saida) 200)))

(defn getRetiradasByAlmoxarife [idAlmoxarife]
  (let [retiradas (vec (retirada/find-by-id-almoxarife idAlmoxarife))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:nome :nomePeca,
                     :datahoraRetirada :dataPedido}) retiradas))

    (ring-resp/status (ring-resp/response saida) 200)))

(defn getRetiradasByFuncionario [idFuncionario]
  (let [retiradas (vec (retirada/find-by-id-funcionario idFuncionario))]
    (def saida (map
                #(clojure.set/rename-keys
                  % {:nome :nomePeca,
                     :datahoraRetirada :dataPedido}) retiradas))

    (ring-resp/status (ring-resp/response saida) 200)))

(defn reporPeca [req]
  (let [idPeca (get-in req [:body "idPeca"])
        qtd (get-in req [:body "qtdRepor"])
        idAviso (get-in req [:body "idAviso"])
        peca (nth (peca/find-by-id idPeca) 0)
        aviso (nth (aviso/find-by-id idAviso) 0)
        idReposicao (get (reposicao/create (c/to-sql-date (t/now))
                                           qtd
                                           idPeca
                                           idAviso) :generated_key)]

    (peca/update-with-data idPeca
                           (get peca :nome)
                           (+ (get peca :qtdDisponivel) qtd)
                           (get peca :qtdMinima))

    (aviso/closeAviso idAviso)
    (def reposicao (nth (reposicao/find-by-id idReposicao) 0))
    (generateAndSavePdfReposicao reposicao)
    (ring-resp/response {})))

(defn setOrderToOk [req]
  (let [idRetirada (get-in req [:body "idRetirada"])
        retirada (nth (retirada/find-by-id idRetirada) 0)]
    (retirada/update-with-data idRetirada
                               (c/to-sql-date (t/now))
                               (get retirada :idAlmoxarife)
                               (get retirada :idFuncionario)
                               (get retirada :idPeca)
                               (get retirada :quantidade)))

  (ring-resp/response {}))