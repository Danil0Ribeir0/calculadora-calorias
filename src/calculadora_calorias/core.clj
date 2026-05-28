(ns calculadora-calorias.core
  (:require [calculadora-calorias.controllers.interface :as controller])
  (:gen-class))

(defn -main
  "Ponto de entrada principal da aplicação via terminal."
  [& args]
  (controller/iniciar-loop-aplicacao!))