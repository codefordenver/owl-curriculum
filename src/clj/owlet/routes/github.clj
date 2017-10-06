(ns owlet.routes.github
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [context]]
            [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def OWLET_GITHUB_TOKEN (System/getenv "OWLET_GITHUB_TOKEN"))

(defn proxy-github-request
  "proxy front end request"
  [_]
  (let [headers {:Authorization OWLET_GITHUB_TOKEN}
        {:keys [status body]} @(http/get "https://api.github.com/repos/codefordenver/owlet/stats/commit_activity" headers)]
    (when (= status 200)
      (let [stats (json/parse-string body true)
            weeks-and-total (map #(select-keys % [:total :week]) stats)]
        (ok {:status status
             :body weeks-and-total})))))


(defroutes routes
           (GET "/stats" {params :params} proxy-github-request))
