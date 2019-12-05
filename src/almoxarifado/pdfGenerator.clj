(ns almoxarifado.pdfGenerator
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [almoxarifado.peca.peca :as peca]
            [almoxarifado.pessoa.pessoa :as pessoa]
            [almoxarifado.retirada.retirada :as retirada]
            [almoxarifado.aviso.aviso :as aviso]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :as ring-resp]
            [clj-pdf.core :as pdf]))

(def page-size :a4)
(def page-width 612)
(def page-height 792)
(def margin-left 50)
(def margin-right 50)
(def headerMessage "Este é um report inteiro, proibida divulgação.")

(def templateRetirada
  (pdf/template
   [:paragraph
    [:heading {:style {:align :center}} "Report de Retirada de Peça. #" $id]
    [:chunk {:style :bold} "Peça: "] $pecaTxt "\n"
    [:chunk {:style :bold} "Funcionário #"] $IdFuncionario ": " $funcionarioTxt "\n"
    [:chunk {:style :bold} "Almoxarife #"] $IdAlmoxarife ": " $almoxarifeTxt "\n"
    [:chunk {:style :bold} "Quantidade retiradada: "] $quantidade "\n"]))

(defn generateAndSavePdfRetirada [idRetirada]
  (let [retirada (nth (retirada/find-by-id idRetirada) 0)
        pdfName (str "retirada_" (get retirada :id) ".pdf")
        peca (nth (peca/find-by-id (get retirada :idPeca)) 0)
        almoxarife (nth (pessoa/find-by-id (get retirada :idAlmoxarife)) 0)
        funcionario (nth (pessoa/find-by-id (get retirada :idFuncionario)) 0)
        enrichedRetirada [(assoc retirada :funcionarioTxt (get funcionario :cpf)
                                 :almoxarifeTxt (get almoxarife :cpf)
                                 :pecaTxt (get peca :nome))]]

    (pdf/pdf [{:size page-size
               :title "Retirada"
               :left-margin margin-left
               :right-magin margin-right
               :font  {:size 11}
               :footer {:start-page 2
                        :align :center}
               :pages true
               :header headerMessage}
              (templateRetirada enrichedRetirada)]
             pdfName)))

(def templateAviso
  (pdf/template
   [:paragraph
    [:heading {:style {:align :center}} "Report de Aviso de necessidade de reposição #" (str $id)]
    [:chunk {:style :bold} "Data do acontecimento: "] (str $datahoraAcontecimento "\n")
    [:chunk {:style :bold} "Data do envio do aviso: "] (str $dataHoraEnvio "\n")
    [:chunk {:style :bold} "Retirada que causou a necessidade: # "] $idRetiradaVinculada "\n"]))

(defn generateAndSavePdfAviso [idAviso]
  (let [aviso (nth (aviso/find-by-id idAviso) 0)
        pdfName (str "aviso_" (get aviso :id) ".pdf")
        avisoToPdf [aviso]]

    (pdf/pdf [{:size page-size
               :title "Aviso"
               :left-margin margin-left
               :right-magin margin-right
               :font  {:size 11}
               :footer {:start-page 2
                        :align :center}
               :pages true
               :header headerMessage}
              (templateAviso avisoToPdf)]
             pdfName)))

(def templateRepo
  (pdf/template
   [:paragraph
    [:heading {:style {:align :center}} "Report de Aviso de chegada de reposição #" (str $id)]
    [:chunk {:style :bold} "Data da chegada: "] (str $datahoraChegada "\n")
    [:chunk {:style :bold} "Quantidade reposta: "] (str $quantidadeReposta "\n")
    [:chunk {:style :bold} "Peça reposta: # "] $idPeca "\n"
    [:chunk {:style :bold} "Aviso vinculado: # "] $idAviso "\n"]))

(defn generateAndSavePdfReposicao [reposicao]
  (let [pdfName (str "reposicao_" (get reposicao :id) ".pdf")
        repoToPdf [reposicao]]

    (pdf/pdf [{:size page-size
               :title "Reposicao"
               :left-margin margin-left
               :right-magin margin-right
               :font  {:size 11}
               :footer {:start-page 2
                        :align :center}
               :pages true
               :header headerMessage}
              (templateRepo repoToPdf)]
             pdfName)))