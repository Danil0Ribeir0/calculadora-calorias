(ns calculadora-calorias.api.conexao
  (:require [clj-http.client :as http]
            [cheshire.core :refer [parse-string]]))

(defn buscar-alimento [nome-alimento]
  (let [api-key  (System/getenv "USDA_API_KEY")
        url      "https://api.nal.usda.gov/fdc/v1/foods/search"
        resposta (http/get url {:query-params {"query" nome-alimento
                                               "api_key" api-key}})]
    (parse-string (:body resposta) true)))

(defn buscar-exercicio [nome-exercicio]
  (let [api-key  (System/getenv "API_NINJAS_KEY")
        url      "https://api.api-ninjas.com/v1/caloriesburned"
        resposta (http/get url {:query-params {"activity" nome-exercicio}
                                :headers {"X-Api-Key" api-key}})]
    (parse-string (:body resposta) true)))