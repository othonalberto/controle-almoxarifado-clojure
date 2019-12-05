(ns almoxarifado.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [almoxarifado.peca.peca :as peca]
            [almoxarifado.service :as service]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))

(defroutes all-routes
  (GET  "/" [] "The service is working")

  (POST "/pessoa" req (service/getRequestThenCreatePessoa req))
  (GET "/pessoa/:email" [email] (service/getPessoaByEmail email))
  (GET "/peca" [] (peca/find-all))
  (POST "/peca" req (service/getRequestThenCreatePeca req))
  (GET "/peca/:nome" [nome] (service/getInfosPeca nome))
  (POST "/peca/retirar" req (service/openOrder req))
  (POST "/peca/repor" req (service/reporPeca req))
  (GET "/pecasParaRepor" [] (service/getPecasParaRepor))
  (POST "/retirada/aprovar" req (service/closeOrder req))
  (POST "/retirada/ok" req (service/setOrderToOk req))
  (GET "/retiradas/fechadas" [] (service/historicoRetiradas))
  (GET "/retiradas/abertas" [] (service/getRetiradasOpen))
  (GET "/retiradas/abertas/:idFuncionario" [idFuncionario] (service/getRetiradasOpenByFuncionario idFuncionario))
  (GET "/retiradas/almoxarife/:idAlmoxarife" [idAlmoxarife] (service/getRetiradasByAlmoxarife idAlmoxarife))
  (GET "/retiradas/funcionario/:idFuncionario" [idFuncionario] (service/getRetiradasByFuncionario idFuncionario))
  (route/not-found "Error! 404"))

(def app
  (-> all-routes
      wrap-json-response
      wrap-json-body))

(defn -main
  [& args]
  (println "hello"))