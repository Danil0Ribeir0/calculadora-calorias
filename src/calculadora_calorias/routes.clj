(ns calculadora-calorias.routes
  (:require [compojure.core :refer [defroutes POST GET]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [calculadora-calorias.controllers.handlers :as handlers]))

(defroutes app-routes
           (POST "/api/usuario" req (handlers/salvar-perfil-usuario req))
           (POST "/api/transacoes" req (handlers/registrar-transacao req))
           (GET "/api/transacoes" req (handlers/listar-transacoes req))
           (GET "/api/resumo" req (handlers/mostrar-resumo req)))

           (route/not-found {:status 404 :body {:erro "Rota não encontrada"}})

(def app
  (-> app-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})))