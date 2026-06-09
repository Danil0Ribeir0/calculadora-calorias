(ns calculadora-calorias.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [calculadora-calorias.routes :refer [app]]))

(deftest testar-rotas-api
  (testing "Testa se a rota de resumo retorna status 200 OK"
    (let [resposta (app (mock/request :get "/api/resumo"))]
      (is (= (:status resposta) 200)))))