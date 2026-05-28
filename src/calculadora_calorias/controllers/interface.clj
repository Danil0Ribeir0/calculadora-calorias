(ns calculadora-calorias.controllers.interface
  (:require [calculadora-calorias.views.terminal :as view]
            [calculadora-calorias.api.tratamento :as api]
            [calculadora-calorias.models.database :as model]))

(defn- ler-entrada []
  (let [entrada (read-line)]
    (if (clojure.string/blank? entrada) "" entrada)))

(defn- executar-opcao-busca-alimento []
  (println "\nDigite o nome do alimento para buscar na API:")
  (print "> ") (flush)
  (let [alimento (ler-entrada)
        _ (view/exibir-mensagem "Conectando à API externa e calculando... (Recomendação do Livro)")
        resultado-api (api/processar-busca-alimento alimento)]

    (if (:sucesso? resultado-api)
      (let [dados-alimento (:dados resultado-api)
            transacao {:tipo "refeicao"
                       :nome (:nome dados-alimento)
                       :calorias (:calorias dados-alimento)}]
        (model/adicionar-transacao! transacao)
        (view/exibir-mensagem (str (:nome dados-alimento) " (" (:calorias dados-alimento) " kcal) adicionado com sucesso!")))
      (view/exibir-erro (:mensagem resultado-api)))))

(defn- executar-opcao-exercicio []
  (println "\nDigite o nome do exercicio:")
  (print "> ") (flush)
  (let [nome-exe (ler-entrada)]
    (println "Digite a quantidade de calorias gastas:")
    (print "> ") (flush)
    (let [calorias-str (ler-entrada)
          calorias (try (Integer/parseInt calorias-str) (catch Exception _ nil))]
      (if (and (not-empty nome-exe) calorias)
        (do
          (model/adicionar-transacao! {:tipo "exercicio" :nome nome-exe :calorias calorias})
          (view/exibir-mensagem "Exercicio registrado com sucesso!"))
        (view/exibir-erro "Dados invalidos. O exercicio nao foi registrado.")))))

(defn iniciar-loop-aplicacao!
  "Inicia o loop interativo do terminal usando recursão pura (loop/recur)."
  []
  (view/exibir-cabecalho)
  (loop []
    (view/exibir-menu)
    (let [opcao (ler-entrada)]
      (cond
        (= opcao "1") (do (view/exibir-resumo (model/obter-resumo)) (recur))
        (= opcao "2") (do (executar-opcao-busca-alimento) (recur))
        (= opcao "3") (do (executar-opcao-exercicio) (recur))
        (= opcao "4") (do (view/exibir-historico (model/obter-historico)) (recur))
        (= opcao "5") (view/exibir-mensagem "Obrigado por usar a Calculadora de Calorias. Ate logo!")
        :else          (do (view/exibir-erro "Opcao invalida! Tente novamente.") (recur))))))