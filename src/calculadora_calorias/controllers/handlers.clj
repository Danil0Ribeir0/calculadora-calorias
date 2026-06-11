(ns calculadora-calorias.controllers.handlers
  (:require [calculadora-calorias.models.database :as db]
            [calculadora-calorias.api.conexao :as api]
            [calculadora-calorias.services.servicos :as servicos]
            [calculadora-calorias.api.tratamento :as tratamento]
            [ring.util.response :as response]))

(defn obter-resumo [_]
  (let [usuario-atual (db/obter-usuario)
        historico-atual (db/obter-historico)
        resumo-calculado (servicos/gerar-resumo-diario usuario-atual historico-atual)]

    ;; 4. Devolve o JSON formatado
    {:status 200
     :body resumo-calculado}))

(defn obter-historico [_]
  {:status 200
   :body (db/obter-historico)})

(defn salvar-perfil-usuario [req]
  (let [corpo (:body req)
        perfil-atualizado (db/atualizar-usuario! corpo)]
    {:status 200
     :body {:mensagem "Perfil e meta calorica atualizados com sucesso!"
            :usuario perfil-atualizado}}))

(defn registrar-transacao [req]
  (let [dados (:body req)
        tipo (:tipo dados)
        nome (:nome dados)]

    (cond
      (= tipo "refeicao")
      (let [busca-json (api/buscar-alimento nome)
            calorias-calculadas (tratamento/extrair-calorias-alimento busca-json)]

        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias-calculadas})

        (response/created "/api/transacoes"
                          {:mensagem (str "Alimento '" nome "' registrado.")
                           :calorias calorias-calculadas}))

      (= tipo "exercicio")
      (let [busca-json (api/buscar-exercicio nome)
            calorias-calculadas (tratamento/extrair-calorias-exercicio busca-json)]

        (db/adicionar-transacao! {:tipo tipo :nome nome :calorias calorias-calculadas})

        (response/created "/api/transacoes"
                          {:mensagem (str "Exercicio '" nome "' registrado.")
                           :calorias calorias-calculadas}))

      :else
      (response/bad-request {:erro "O campo 'tipo' deve ser 'refeicao' ou 'exercicio'."}))))