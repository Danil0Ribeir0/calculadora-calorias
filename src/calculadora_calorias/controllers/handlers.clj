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

(defn obter-parametro [req nome]
  (let [params (or (:query-params req) (:params req) {})
        valor-string (get params nome)
        valor-keyword (get params (keyword nome))]
    (or valor-string
        valor-keyword
        (let [query-string (str (:query-string req))]
          (second (re-find (re-pattern (str nome "=([^&]+)")) query-string))))))

(defn listar-transacoes [req]
  (let [inicio (obter-parametro req "inicio")
        fim (obter-parametro req "fim")
        todas-transacoes (db/obter-historico)
        transacoes-filtradas (if (and inicio fim)
                               (servicos/filtrar-por-periodo todas-transacoes inicio fim)
                               todas-transacoes)]
    {:status 200 :body transacoes-filtradas}))

(defn mostrar-resumo [req]
  (let [inicio (obter-parametro req "inicio")
        fim (obter-parametro req "fim")
        usuario (db/obter-usuario)
        todas-transacoes (db/obter-historico)
        transacoes-filtradas (if (and inicio fim)
                               (servicos/filtrar-por-periodo todas-transacoes inicio fim)
                               todas-transacoes)
        resumo (servicos/gerar-resumo-diario usuario transacoes-filtradas)]
    {:status 200 :body resumo}))