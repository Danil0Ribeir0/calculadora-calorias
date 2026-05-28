(ns calculadora-calorias.models.database)

(def app-state
  (atom {:usuario    {:nome "Usuário" :meta-diaria 2000}
         :transacoes '()}))

(defn- somar-por-tipo
  "Filtra as transações por tipo (ex: 'refeicao' ou 'exercicio') e soma as calorias.
   Uso a macro ->> (thread-last) para criar um pipeline funcional legível."
  [transacoes tipo-alvo]
  (->> transacoes
       (filter #(= (:tipo %) tipo-alvo))
       (map :calorias)
       (apply +)))

(defn- calcular-resumo-estado
  "Recebe um mapa de estado e retorna o balanço calórico atual."
  [estado-atual]
  (let [transacoes (:transacoes estado-atual)
        consumidas (somar-por-tipo transacoes "refeicao")
        gastas     (somar-por-tipo transacoes "exercicio")]
    {:consumidas consumidas
     :gastas     gastas
     :saldo      (- consumidas gastas)
     :meta       (get-in estado-atual [:usuario :meta-diaria])}))

(defn adicionar-transacao!
  "Recebe uma transação limpa (da camada API), adiciona um ID único
   e a insere no topo da lista de transações usando 'conj'."
  [transacao-limpa]
  (let [nova-transacao (assoc transacao-limpa :id (System/currentTimeMillis))]
    (swap! app-state update :transacoes conj nova-transacao)
    nova-transacao))

(defn obter-resumo
  "Lê o estado atual (desreferenciando o atom com @) e calcula o resumo."
  []
  (calcular-resumo-estado @app-state))

(defn obter-historico
  "Retorna a lista completa de transações cadastradas."
  []
  (:transacoes @app-state))