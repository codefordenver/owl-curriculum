(ns owlet.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [cheshire.core :as json]
            [schema.core :as s]))

(defapi service-routes

  {:swagger {:ui   "/api/docs"
             :spec "/swagger.json"
             :data {:info {:version     "0.0.1"
                           :title       "Owlet API"
                           :description "Services & Webhooks"}}}}

  (context "/api" []

    :tags ["API"]

    (context "/content" []

      (GET "/space" []
        :query-params [space-id, library-view :- Boolean]
        :summary
        "Asynchronously GETs all entries for given,
				optionally pass library-view=true
				param to get all entries for given space"
        (ok {:metadata   {},
             :activities [],
             :platforms  []})))))