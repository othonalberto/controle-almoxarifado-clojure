(ns almoxarifado.db
  (:use korma.db))

(defdb db (mysql
           {:classname "com.mysql.jdbc.Driver"
            :host ""
            :port ""
            :db ""
            :user ""
            :password ""}))