(ns calculadora-calorias.api.tratamento
  (:require [calculadora-calorias.api.conexao :as conexao]))

(defn processar-busca-alimento [nome-alimento]
  (let [resultado (conexao/buscar-informacao-nutricional nome-alimento)]
    (if (:sucesso? resultado)
      (let [lista-alimentos (:dados resultado)]
        {:sucesso? true
         :dados    {:nome     nome-alimento
                    :calorias (get-in lista-alimentos [0 :calories] 0)}})
      resultado)))