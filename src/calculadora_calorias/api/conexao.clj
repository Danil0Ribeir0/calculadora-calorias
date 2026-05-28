(ns calculadora-calorias.api.conexao
  (:require [clj-http.client :as http-client]
            [cheshire.core :refer [parse-string]]
            [environ.core :refer [env]]))

(def chave-api (env :api-ninjas-key))
(def url-alimentos "https://api.api-ninjas.com/v1/nutrition")

(defn buscar-informacao-nutricional
  [nome-alimento]
  (try
    (let [resposta (http-client/get url-alimentos
                                    {:query-params {"query" nome-alimento}
                                     :headers      {"X-Api-Key" chave-api}})]
      {:sucesso? true
       :dados    (-> (:body resposta)
                     (parse-string true))})
    (catch Exception _
      {:sucesso? false
       :mensagem "Falha ao consultar a API de alimentos."})))