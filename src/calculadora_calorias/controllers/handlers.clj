(ns calculadora-calorias.controllers.handlers
  (:require [calculadora-calorias.models.database :as db]
            [calculadora-calorias.api.conexao :as api]
            [calculadora-calorias.api.tratamento :as tratamento]
            [ring.util.response :as response]))

(defn obter-resumo [req]
  {:status 200
   :body (db/obter-resumo)})

(defn obter-historico [req]
  {:status 200
   :body (db/obter-historico)})

(defn salvar-perfil-usuario [req]
  (let [corpo (:body req)
        perfil-atualizado (db/atualizar-usuario! corpo)]
    {:status 200
     :body {:mensagem "Perfil e meta calórica atualizados com sucesso!"
            :usuario perfil-atualizado}}))

(defn registrar-transacao [req]
  (let [dados (:body req)
        tipo (:tipo dados)
        nome (:nome dados)]

    (cond
      (= tipo "refeicao")
      (let [busca-json (api/buscar-alimento nome)
            calorias-calculadas (tratamento/extrair-calorias-alimento busca-json)]
        (db/adicionar-transacao! [tipo nome calorias-calculadas])
        (response/created "/api/transacoes"
                          {:mensagem (str "Alimento '" nome "' registrado via USDA!")
                           :calorias calorias-calculadas}))

      (= tipo "exercicio")
      (let [busca-json (api/buscar-exercicio nome)
            calorias-calculadas (tratamento/extrair-calorias-exercicio busca-json)]
        (db/adicionar-transacao! [tipo nome calorias-calculadas])
        (response/created "/api/transacoes"
                          {:mensagem (str "Exercício '" nome "' registrado via Ninjas!")
                           :calorias calorias-calculadas}))

      :else
      (response/bad-request {:erro "O campo 'tipo' deve ser 'refeicao' ou 'exercicio'."}))))