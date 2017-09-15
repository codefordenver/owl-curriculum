(ns owlet.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(defapi service-routes

  {:swagger {:ui   "/docs/api"
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
             :platforms  []})))

    (context "/webhook" []

      :tags ["Webhooks"]

      ;; TODO: (david) document returns

      (context "/content" []

        (GET "/confirm" []
         :query-params [id]
         :summary "Confirmation route, gets hit by the front end")

        (POST "/email" []
          :query-params [payload]
          :summary "Sends email to list of subscribers")

        (PUT "/subscribe" []
          :query-params [email]
          :summary
          "handles new subscription request -checks list of subs b4 adding to list; ie no duplicates")

        (PUT "/unsubscribe" []
          :query-params [email]
          :summary
          "handles new subscription request -checks list of subs b4 adding to list; ie no duplicates")))))
