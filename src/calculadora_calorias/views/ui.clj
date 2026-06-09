(ns calculadora-calorias.views.ui
  (:require [clj-http.client :as http])
  (:gen-class))

(defn exibir-menu []
  (println (str "\n=== CALCULADORA DE CALORIAS ==="))
  (println (str "1. Registrar Refeicao"))
  (println (str "2. Registrar Exercicio"))
  (println (str "3. Ver Resumo Diario"))
  (println (str "4. Sair"))
  (print (str "Escolha uma opcao: "))
  (flush)
  (read-line))

(defn registrar-atividade [tipo]
  (print (str "Digite o nome (em ingles, ex: apple ou running): "))
  (flush)
  (let [nome (read-line)
        url "http://localhost:3000/api/transacoes"
        resposta (try
                   (http/post url {:form-params {:tipo tipo :nome nome}
                                   :content-type :json
                                   :as :json})
                   (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println (str "-> Sucesso: " (:mensagem dados) " - Calorias: " (:calorias dados))))
      (println (str "-> Erro: O servidor (Terminal 1) esta rodando?")))))

(defn mostrar-resumo []
  (let [resposta (try
                   (http/get "http://localhost:3000/api/resumo" {:as :json})
                   (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println (str "\n--- RESUMO DIARIO ---"))
        (println (str "Consumidas: " (:consumidas dados) " kcal"))
        (println (str "Gastas: " (:gastas dados) " kcal"))
        (println (str "Saldo Atual: " (:saldo dados) " kcal"))
        (println (str "Meta Diaria: " (:meta dados) " kcal")))
      (println (str "-> Erro ao buscar resumo. O servidor (Terminal 1) esta rodando?")))))

(defn iniciar-cli []
  (let [opcao (exibir-menu)]
    (cond
      (= opcao "1") (do (registrar-atividade "refeicao") (recur))
      (= opcao "2") (do (registrar-atividade "exercicio") (recur))
      (= opcao "3") (do (mostrar-resumo) (recur))
      (= opcao "4") (println (str "Saindo do sistema... Ate logo!"))
      :else (do (println (str "-> Opcao invalida!")) (recur)))))

(defn -main [& args]
  (println (str "Iniciando o Front-end interativo..."))
  (iniciar-cli))