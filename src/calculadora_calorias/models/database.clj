(ns calculadora-calorias.models.database)

(def usuario-db (atom {:nome "Usuario Padrao"
                       :altura 0.0
                       :peso 0.0
                       :idade 0
                       :sexo ""}))
(def transacoes-db (atom []))

(defn atualizar-usuario! [dados]
  (reset! usuario-db dados))

(defn obter-usuario []
  @usuario-db)

(defn adicionar-transacao! [transacao]
  (swap! transacoes-db conj transacao))

(defn obter-historico []
  @transacoes-db)