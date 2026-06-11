(ns calculadora-calorias.api.tratamento)

(defn extrair-calorias-alimento [dados-usda]
  (let [nutrientes (-> dados-usda
                       :foods
                       first
                       :foodNutrients)
        calorias   (some #(when (= (:nutrientName %) "Energy") (:value %)) nutrientes)]
    (if calorias
      (int calorias)
      0)))

(defn extrair-calorias-exercicio [dados-ninjas]
  (let [primeira-atividade (first dados-ninjas)
        calorias           (:calories_per_hour primeira-atividade)]
    (if calorias
      (int calorias)
      0)))