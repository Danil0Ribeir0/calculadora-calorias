(ns calculadora-calorias.api.tratamento)

(defn extrair-calorias-alimento [dados-usda]
  (try
    (let [alimentos (:foods dados-usda)
          primeiro-alimento (first alimentos)
          nutrientes (:foodNutrients primeiro-alimento)
          calorias (some #(when (= (:nutrientName %) "Energy") (:value %)) nutrientes)]
      (if calorias
        (int calorias)
        0))
    (catch Exception _ 0)))

(defn extrair-calorias-exercicio [dados-ninjas]
  (try
    (let [primeira-atividade (first dados-ninjas)]
      (if primeira-atividade
        (int (:calories_per_hour primeira-atividade))
        0))
    (catch Exception _ 0)))