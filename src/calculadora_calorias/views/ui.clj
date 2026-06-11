(ns calculadora-calorias.views.ui
  (:require [clj-http.client :as http]
            [cheshire.core :refer [generate-string]]) ;; 1. Importamos o Cheshire
  (:gen-class))

(defn exibir-menu []
  (println "\n=== CALCULADORA DE CALORIAS ===")
  (println "1. Registrar Refeicao")
  (println "2. Registrar Exercicio")
  (println "3. Ver Resumo Diario")
  (println "4. Sair")
  (print "Escolha uma opcao: ")
  (flush)
  (read-line))

(defn registrar-atividade [tipo]
  (print "Digite o nome (em ingles, ex: apple ou running): ")
  (flush)
  (let [nome (read-line)
        url "http://localhost:3000/api/transacoes"
        resposta (try
                   (http/post url {:body         (generate-string {:tipo tipo :nome nome})
                                   :content-type :json
                                   :accept       :json
                                   :as           :json})
                   (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println "-> Sucesso:" (:mensagem dados) "- Calorias:" (:calorias dados)))
      (println "-> Erro: O servidor nso esta rodando"))))

(defn mostrar-resumo []
  (let [resposta (try
                   (http/get "http://localhost:3000/api/resumo" {:as :json})
                   (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println "\n--- RESUMO DIARIO ---")
        (println "Consumidas:" (:consumidas dados) "kcal")
        (println "Gastas:" (:gastas dados) "kcal")
        (println "Saldo Atual:" (:saldo dados) "kcal")
        (println "Meta Diaria:" (:meta dados) "kcal"))
      (println "-> Erro ao buscar resumo. O servidor (Terminal 1) esta rodando?"))))

(defn iniciar-cli []
  (let [opcao (exibir-menu)]
    (cond
      (= opcao "1") (do (registrar-atividade "refeicao") (recur))
      (= opcao "2") (do (registrar-atividade "exercicio") (recur))
      (= opcao "3") (do (mostrar-resumo) (recur))
      (= opcao "4") (println "Saindo do sistema...")
      :else         (do (println "-> Opcao invalida!") (recur)))))

(defn -main [& _]
  (println "Iniciando o Front-end interativo...")
  (iniciar-cli))