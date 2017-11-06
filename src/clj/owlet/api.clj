(ns owlet.api
  (:require [compojure.api.sweet :refer [context]]
            [compojure.core :refer [defroutes]]
            [owlet.routes.contentful :as contentful]
            [owlet.routes.github :as github]))

(defroutes api-routes
           (context "/api" []
             (context "/contentful" [] contentful/routes)
             (context "/github" [] github/routes)))