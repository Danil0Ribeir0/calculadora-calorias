(ns calculadora-calorias.services.servicos)

(defn calcular-saldo [transacoes]
  (reduce (fn [saldo-acumulado transacao]
            (if (= (:tipo transacao) "refeicao")
              (+ saldo-acumulado (:calorias transacao))
              (- saldo-acumulado (:calorias transacao))))
          0
          transacoes))

(defn analisar-meta [saldo meta-diaria]
  (if (<= saldo meta-diaria)
    "Meta atingida ou abaixo do limite. Excelente!"
    "Atenção: Limite calórico diário ultrapassado."))

(defn gerar-resumo-diario [usuario transacoes]
  (let [saldo (calcular-saldo transacoes)
        meta-calorica (:meta-calorica usuario)]
    {:usuario (:nome usuario)
     :meta-diaria meta-calorica
     :saldo-atual saldo
     :status-meta (analisar-meta saldo meta-calorica)
     :total-transacoes (count transacoes)}))