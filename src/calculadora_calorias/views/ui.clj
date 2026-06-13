(ns calculadora-calorias.views.ui
  (:require [clj-http.client :as http]
            [cheshire.core :refer [generate-string]])
  (:gen-class))

(defn exibir-menu []
  (println "\n=== CALCULADORA DE CALORIAS ===")
  (println "1. Cadastrar Usuario")
  (println "2. Registrar Refeicao")
  (println "3. Registrar Exercicio")
  (println "4. Ver Resumo Diario")
  (println "5. Sair")
  (print "Escolha uma opcao: ")
  (flush)
  (read-line))

(defn cadastrar-usuario []
  (println "\n--- CADASTRO DE UTILIZADOR ---")
  (print "Nome: ") (flush)
  (let [nome (read-line)]
    (print "Altura (em cm, ex: 175): ") (flush)
    (let [altura (read-line)]
      (print "Peso (em kg, ex: 70.5): ") (flush)
      (let [peso (read-line)]
        (print "Idade: ") (flush)
        (let [idade (read-line)]
          (print "Sexo (M/F): ") (flush)
          (let [sexo (read-line)
                dados-usuario {:nome nome
                               :altura (try (Float/parseFloat altura) (catch Exception _ 0.0))
                               :peso (try (Float/parseFloat peso) (catch Exception _ 0.0))
                               :idade (try (Integer/parseInt idade) (catch Exception _ 0))
                               :sexo sexo}
                url "http://localhost:3000/api/usuario"
                resposta (try
                           (http/post url {:body         (generate-string dados-usuario)
                                           :content-type :json
                                           :accept       :json
                                           :as           :json})
                           (catch Exception _ nil))]
            (if resposta
              (let [dados (:body resposta)]
                (println "-> Sucesso:" (:mensagem dados)))
              (println "-> Erro: O servidor não está a correr ou a rota está incorreta."))))))))

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
        (println "Usuario:" (:usuario dados))
        (println "Consumidas:" (:consumidas dados) "kcal")
        (println "Gastas:" (:gastas dados) "kcal")
        (println "Saldo Atual:" (:saldo-atual dados) "kcal")
        (println "Meta Diaria:" (:meta-diaria dados) "kcal")
        (println "Status da Meta:" (:status-meta dados))
        (println "Total de Atividades Registradas:" (:total-transacoes dados)))
      (println "-> Erro ao buscar resumo. O servidor está a correr?"))))

(defn iniciar-cli []
  (let [opcao (exibir-menu)]
    (cond
      (= opcao "1") (do (cadastrar-usuario) (recur))
      (= opcao "2") (do (registrar-atividade "refeicao") (recur))
      (= opcao "3") (do (registrar-atividade "exercicio") (recur))
      (= opcao "4") (do (mostrar-resumo) (recur))
      (= opcao "5") (println "A sair do sistema...")
      :else         (do (println "-> Opcao invalida!") (recur)))))

(defn -main [& _]
  (println "Iniciando o Front-end interativo...")
  (iniciar-cli))