(ns calculadora-calorias.controllers.handlers
  (:require [calculadora-calorias.models.database :as db]
            [calculadora-calorias.services.servicos :as servicos]
            [ring.util.response :as response]))

(defn salvar-perfil-usuario [req]
  (let [corpo (:body req)
        perfil-atualizado (db/atualizar-usuario! corpo)]
    {:status 200
     :body {:mensagem "Perfil atualizado com sucesso!"
            :usuario perfil-atualizado}}))

(defn registrar-transacao [req]
  (let [dados (:body req)
        tipo (:tipo dados)
        nome (:nome dados)
        data (:data dados)
        quantidade (:quantidade dados)
        duracao (:duracao dados)]

    (cond
      (= tipo "refeicao")
      (let [calorias (servicos/calcular-calorias-refeicao nome quantidade)]
        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias :data data})
        (response/created "/api/transacoes"
                          {:mensagem (str "Alimento '" nome "' registrado.")
                           :calorias calorias}))

      (= tipo "exercicio")
      (let [calorias (servicos/calcular-calorias-exercicio nome duracao)]
        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias :data data})
        (response/created "/api/transacoes"
                          {:mensagem (str "Exercicio '" nome "' registrado.")
                           :calorias calorias}))
      :else
      (response/bad-request {:erro "O campo 'tipo' deve ser 'refeicao' ou 'exercicio'."}))))

(defn listar-transacoes [req]
  (let [params (:query-params req)
        inicio (get params "inicio")
        fim (get params "fim")
        todas-transacoes (db/obter-historico)
        transacoes-filtradas (if (and inicio fim)
                               (servicos/filtrar-por-periodo todas-transacoes inicio fim)
                               todas-transacoes)]
    {:status 200 :body transacoes-filtradas}))

(defn mostrar-resumo [req]
  (let [params (:query-params req)
        inicio (get params "inicio")
        fim (get params "fim")
        usuario (db/obter-usuario)
        todas-transacoes (db/obter-historico)
        transacoes-filtradas (if (and inicio fim)
                               (servicos/filtrar-por-periodo todas-transacoes inicio fim)
                               todas-transacoes)
        resumo (servicos/gerar-resumo-diario usuario transacoes-filtradas)]
    {:status 200 :body resumo}))