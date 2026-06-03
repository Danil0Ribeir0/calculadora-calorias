(ns calculadora-calorias.models.database)

(def app-state
  (atom {:usuario    {:nome "Usuário" :peso 70 :altura 170 :idade 30 :sexo "M" :meta-diaria 2000}
         :transacoes '()}))

(defn- somar-por-tipo
  [transacoes tipo-alvo]
  (->> transacoes
       (filter #(= (:tipo %) tipo-alvo))
       (map :calorias)
       (apply +)))

(defn- calcular-meta-basal
  [peso altura idade sexo]
  (if (= (clojure.string/upper-case sexo) "M")
    (+ 66.5 (* 13.75 peso) (* 5.003 altura) (* -6.75 idade))
    (+ 655.1 (* 9.563 peso) (* 1.850 altura) (* -4.676 idade))))

(defn atualizar-usuario!
  [dados-usuario]
  (let [{:keys [nome peso altura idade sexo]} dados-usuario
        meta (calcular-meta-basal peso altura idade sexo)
        novo-perfil {:nome nome
                     :peso peso
                     :altura altura
                     :idade idade
                     :sexo sexo
                     :meta-diaria (int meta)}]
    (swap! app-state assoc :usuario novo-perfil)
    novo-perfil))

(defn calcular-resumo-estado
  [estado-atual]
  (let [transacoes (:transacoes estado-atual)
        consumidas (somar-por-tipo transacoes "refeicao")
        gastas     (somar-por-tipo transacoes "exercicio")]
    {:consumidas consumidas
     :gastas     gastas
     :saldo      (- consumidas gastas)
     :meta       (get-in estado-atual [:usuario :meta-diaria])}))

(defn adicionar-transacao!
  [transacao-limpa]
  (let [nova-transacao (assoc transacao-limpa :id (System/currentTimeMillis))]
    (swap! app-state update :transacoes conj nova-transacao)
    nova-transacao))

(defn obter-resumo [] (calcular-resumo-estado @app-state))
(defn obter-historico [] (:transacoes @app-state))