(ns calculadora-calorias.services.servicos)

(defn calcular-saldo [transacoes]
  (reduce (fn [saldo-acumulado transacao]
            (if (= (:tipo transacao) "refeicao")
              (+ saldo-acumulado (:calorias transacao))
              (- saldo-acumulado (:calorias transacao))))
          0
          transacoes))

(defn calcular-consumidas [transacoes]
  (reduce + (map :calorias (filter #(= (:tipo %) "refeicao") transacoes))))

(defn calcular-gastas [transacoes]
  (reduce + (map :calorias (filter #(= (:tipo %) "exercicio") transacoes))))

(defn gerar-resumo-diario [usuario transacoes]
  (let [saldo (calcular-saldo transacoes)
        meta-calorica (:meta-calorica usuario)]
    {:usuario (:nome usuario)
     :meta-diaria meta-calorica
     :saldo-atual saldo
     :consumidas (calcular-consumidas transacoes)
     :gastas (calcular-gastas transacoes)
     :total-transacoes (count transacoes)}))