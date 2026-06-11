(ns calculadora-calorias.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [calculadora-calorias.routes :refer [app]])
  (:gen-class))

(defn -main [& _]
  (println (str "Iniciando o servidor da Calculadora de Calorias..."))
  (println (str "Servidor rodando na porta 3000!"))
  (run-jetty app {:port 3000 :join? false}))