(ns calculadora-calorias.controllers.handlers
  (:require [calculadora-calorias.models.database :as db]
            [calculadora-calorias.services.servicos :as servicos]
            [ring.util.response :as response]))

(defn obter-resumo [_]
  (let [usuario-atual (db/obter-usuario)
        historico-atual (db/obter-historico)
        resumo-calculado (servicos/gerar-resumo-diario usuario-atual historico-atual)]

    {:status 200
     :body resumo-calculado}))

(defn obter-historico [_]
  {:status 200
   :body (db/obter-historico)})

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
        quantidade (:quantidade dados)
        duracao (:duracao dados)]

    (cond
      (= tipo "refeicao")
      (let [calorias (servicos/calcular-calorias-refeicao nome quantidade)]
        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias})
        (response/created "/api/transacoes"
                          {:mensagem (str "Alimento '" nome "' registrado com " (or quantidade 100) "g.")
                           :calorias calorias}))

      (= tipo "exercicio")
      (let [calorias (servicos/calcular-calorias-exercicio nome duracao)]
        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias})
        (response/created "/api/transacoes"
                          {:mensagem (str "Exercicio '" nome "' registrado com " (or duracao 60) " min.")
                           :calorias calorias}))

      :else
      (response/bad-request {:erro "O campo 'tipo' deve ser 'refeicao' ou 'exercicio'."}))))