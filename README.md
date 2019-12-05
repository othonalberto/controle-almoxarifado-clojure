# API Controle de Almoxarifado

API do projeto de controle de almoxarifado.

## Pre requisitos

Primeiramente, é necessário ter o [Leiningen][] 2.0.0, ou versão superior, instalado.
[leiningen]: https://github.com/technomancy/leiningen


## Para executar

Para iniciar o servidor da aplicação, rode:

    lein ring server

## Estrutura do projeto

A pasta ```src/almoxarifado``` contém todo o código do projeto.

Para cada entidade do BD, há uma pasta na qual está o arquivo que se conecta ao banco e realiza as queries.

No arquivo ```db.clj``` há a conexão com o banco. Para testar localmente, deve-se criar o banco seguindo o script ```createdb.sql``` e então preencher o arquivo ```db.clj``` com as credenciais corretas.

O arquivo ```handler.clj``` é responsável por receber as requests e então direcionar para o local correto.

O arquivo ```service.clj``` contém as lógicas de negócio, as quais recebem a request do ```handler.clj``` e então realia o resto do trabalho.

O arquivo ```pdfGenerator.clj``` é o serviço responsável por montar os PDFs e salvar na raiz do projeto.