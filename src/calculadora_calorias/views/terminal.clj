(ns calculadora-calorias.views.terminal)

(defn exibir-cabecalho []
  (println "\n=============================================")
  (println "      CALCULADORA DE CALORIAS - CLOJURE      ")
  (println "============================================="))

(defn exibir-menu []
  (println "\nEscolha uma opcao:")
  (println "[1] Ver resumo do dia (Saldo Calorico)")
  (println "[2] Buscar alimento (Adicionar Refeicao)")
  (println "[3] Adicionar exercicio manualmente")
  (println "[4] Ver historico de transacoes")
  (println "[5] Sair")
  (print "Digite a opcao desejada: ")
  (flush))

(defn exibir-resumo [resumo]
  (println "\n--------- RESUMO CALORICO DO DIA ---------")
  (println (str " Meta Diária:  " (:meta resumo) " kcal"))
  (println (str " Consumidas:   " (:consumidas resumo) " kcal"))
  (println (str " Gastas (-):   " (:gastas resumo) " kcal"))
  (println " -----------------------------------------")
  (println (str " SALDO ATUAL:  " (:saldo resumo) " kcal"))
  (if (> (:saldo resumo) (:meta resumo))
    (println " Alerta: Você ultrapassou a sua meta diária!")
    (println " Tudo sob controle! Mantenha o foco."))
  (println "------------------------------------------"))

(defn exibir-historico [transacoes]
  (println "\n--------------- HISTORICO ---------------")
  (if (empty? transacoes)
    (println " Nenhuma transacao registrada ainda.")
    (doseq [t transacoes]
      (println (str "• [" (:tipo t) "] " (:nome t) ": " (:calorias t) " kcal"))))
  (println "------------------------------------------"))

(defn exibir-mensagem [msg]
  (println (str "\n[INFO] " msg)))

(defn exibir-erro [msg]
  (println (str "\n[ERRO] " msg)))