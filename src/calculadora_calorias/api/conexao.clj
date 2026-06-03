(ns calculadora-calorias.api.conexao
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]))

(defn buscar-alimento [nome-alimento]
  (let [api-key (env :usda-api-key "DEMO_KEY")
        url "https://api.nal.usda.gov/fdc/v1/foods/search"]
    (try
      (let [resposta (http/get url {:query-params {"query" nome-alimento
                                                   "api_key" api-key}
                                    :as :json})]
        (:body resposta))
      (catch Exception e
        (println "Erro ao conectar com USDA:" (.getMessage e))
        nil))))

(defn buscar-exercicio [nome-exercicio]
  (let [api-key (env :api-ninjas-key)
        url "https://api.api-ninjas.com/v1/caloriesburned"]
    (try
      (let [resposta (http/get url {:query-params {"activity" nome-exercicio}
                                    :headers {"X-Api-Key" api-key}
                                    :as :json})]
        (:body resposta))
      (catch Exception e
        (println "Erro ao conectar com API Ninjas:" (.getMessage e))
        nil))))