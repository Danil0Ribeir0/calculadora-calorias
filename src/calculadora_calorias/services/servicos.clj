(ns calculadora-calorias.services.servicos
  (:require [calculadora-calorias.api.conexao :as api]
            [calculadora-calorias.api.tratamento :as tratamento]))

(defn somar-consumidas [transacoes]
  (reduce + 0 (map :calorias (filter #(= (:tipo %) "refeicao") transacoes))))

(defn somar-gastas [transacoes]
  (reduce + 0 (map :calorias (filter #(= (:tipo %) "exercicio") transacoes))))

(defn calcular-saldo [transacoes]
  (reduce (fn [saldo-acumulado transacao]
            (if (= (:tipo transacao) "refeicao")
              (+ saldo-acumulado (:calorias transacao))
              (- saldo-acumulado (:calorias transacao))))
          0
          transacoes))

(defn calcular-calorias-refeicao [nome quantidade]
  (let [busca-json (api/buscar-alimento nome)
        calorias-por-100g (tratamento/extrair-calorias-alimento busca-json)]
    (if quantidade
      (float (* (/ calorias-por-100g 100.0) quantidade))
      calorias-por-100g)))

(defn calcular-calorias-exercicio [nome duracao]
  (let [busca-json (api/buscar-exercicio nome)
        calorias-por-hora (tratamento/extrair-calorias-exercicio busca-json)]
    (if duracao
      (float (* (/ calorias-por-hora 60.0) duracao))
      calorias-por-hora)))

(defn gerar-resumo-diario [usuario transacoes]
  (let [consumidas (somar-consumidas transacoes)
        gastas (somar-gastas transacoes)
        saldo (calcular-saldo transacoes)]

    {:usuario (:nome usuario)
     :consumidas (float consumidas)
     :gastas (float gastas)
     :saldo-atual (float saldo)
     :total-transacoes (count transacoes)}))