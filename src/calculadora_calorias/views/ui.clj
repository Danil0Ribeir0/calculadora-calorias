(ns calculadora-calorias.views.ui
  (:require [clj-http.client :as http]
            [cheshire.core :refer [generate-string]])
  (:gen-class))

(defn exibir-menu []
  (println "\n=== CALCULADORA DE CALORIAS ===")
  (println "1. Cadastrar Usuario")
  (println "2. Registrar Refeicao")
  (println "3. Registrar Exercicio")
  (println "4. Ver Resumo Geral")
  (println "5. Consultar Extrato de Transacoes por Periodo")
  (println "6. Consultar Saldo por Periodo")
  (println "7. Sair")
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
        _ (print "Data (DD-MM-AAAA, ex: 25-10-2023): ")
        _ (flush)
        data (read-line)

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

        dados-base {:tipo tipo :nome nome :data data}
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

(defn mostrar-resumo-geral []
  (let [resposta (try (http/get "http://localhost:3000/api/resumo" {:as :json}) (catch Exception _ nil))]
    (if resposta
      (let [dados (:body resposta)]
        (println "\n--- RESUMO GERAL ---")
        (println "Usuario:" (:usuario dados))
        (println "Consumidas:" (:consumidas dados) "kcal")
        (println "Gastas:" (:gastas dados) "kcal")
        (println "Saldo Atual:" (:saldo-atual dados) "kcal")
        (println "Total de Atividades Registradas:" (:total-transacoes dados)))
      (println "-> Erro ao buscar resumo."))))

(defn consultar-extrato-periodo []
  (println "\n--- EXTRATO POR PERIODO ---")
  (print "Data Inicial (DD-MM-AAAA): ") (flush)
  (let [inicio (read-line)]
    (print "Data Final (DD-MM-AAAA): ") (flush)
    (let [fim (read-line)
          resposta (try (http/get "http://localhost:3000/api/transacoes"
                                  {:query-params {"inicio" inicio "fim" fim} :as :json})
                        (catch Exception _ nil))]
      (if resposta
        (let [transacoes (:body resposta)]
          (if (empty? transacoes)
            (println "-> Nenhuma transacao encontrada neste periodo.")
            (doseq [t transacoes]
              (println (str "Data: " (:data t) " | Tipo: " (:tipo t)
                            " | Nome: " (:nome t) " | Calorias: " (:calorias t))))))
        (println "-> Erro ao buscar extrato.")))))

(defn consultar-saldo-periodo []
  (println "\n--- SALDO POR PERIODO ---")
  (print "Data Inicial (DD-MM-AAAA): ") (flush)
  (let [inicio (read-line)]
    (print "Data Final (DD-MM-AAAA): ") (flush)
    (let [fim (read-line)
          resposta (try (http/get "http://localhost:3000/api/resumo"
                                  {:query-params {"inicio" inicio "fim" fim} :as :json})
                        (catch Exception _ nil))]
      (if resposta
        (let [dados (:body resposta)]
          (println "\nUsuario:" (:usuario dados))
          (println "Consumidas no Periodo:" (:consumidas dados) "kcal")
          (println "Gastas no Periodo:" (:gastas dados) "kcal")
          (println "Saldo Final do Periodo:" (:saldo-atual dados) "kcal")
          (println "Total de Atividades no Periodo:" (:total-transacoes dados)))
        (println "-> Erro ao buscar saldo.")))))

(defn iniciar-cli []
  (let [opcao (exibir-menu)]
    (cond
      (= opcao "1") (do (cadastrar-usuario) (recur))
      (= opcao "2") (do (registrar-atividade "refeicao") (recur))
      (= opcao "3") (do (registrar-atividade "exercicio") (recur))
      (= opcao "4") (do (mostrar-resumo-geral) (recur))
      (= opcao "5") (do (consultar-extrato-periodo) (recur))
      (= opcao "6") (do (consultar-saldo-periodo) (recur))
      (= opcao "7") (println "A sair do sistema...")
      :else         (do (println "-> Opcao invalida!") (recur)))))

(defn -main [& _]
  (println "Iniciando o Front-end interativo...")
  (iniciar-cli))