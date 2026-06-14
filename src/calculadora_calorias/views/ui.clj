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
  (print "Digite o nome (em ingles): ")
  (flush)
  (let [nome (read-line)
        quantidade (if (= tipo "refeicao")
                     (do
                       (print "Quantidade consumida (em gramas, ex: 150): ")
                       (flush)
                       (try (Float/parseFloat (read-line)) (catch Exception _ 100.0)))
                     nil)

        duracao (if (= tipo "exercicio")
                  (do
                    (print "Duracao do exercicio (em minutos, ex: 30): ")
                    (flush)
                    (try (Float/parseFloat (read-line)) (catch Exception _ 60.0)))
                  nil)

        dados-base {:tipo tipo :nome nome}
        dados-com-qtd (if quantidade (assoc dados-base :quantidade quantidade) dados-base)
        dados-envio (if duracao (assoc dados-com-qtd :duracao duracao) dados-com-qtd)

        url "http://localhost:3000/api/transacoes"
        resposta (try
                   (http/post url {:body         (generate-string dados-envio)
                                   :content-type :json
                                   :accept       :json
                                   :as           :json})
                   (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println "-> Sucesso:" (:mensagem dados) "- Calorias:" (:calorias dados)))
      (println "-> Erro: O servidor não está a correr."))))

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