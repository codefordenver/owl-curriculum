(ns owlet.routes.github
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [context]]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [reagent.core :as reagent]))

(def OWLET_GITHUB_TOKEN (System/getenv "OWLET_GITHUB_TOKEN"))

(defn proxy-github-request
  "proxy front end request"
  [_]
  (let [headers {:Authorization OWLET_GITHUB_TOKEN}
        {:keys [status body]} @(http/get "https://api.github.com/repos/codefordenver/owlet/stats/commit_activity" headers)]
    (when (= status 200)
      (reset! stored-labels [])
      (let [stats (json/parse-string body true)
            labels (mapv #(f/unparse (f/formatter "MMM YYYY") (c/from-long (* (get % :week) 1000))) (map #(select-keys % [:week]) stats))
            totals (map #(get % :total) stats)]
        (ok {:status status
             :body {:labels labels, :totals totals}})))))


(defroutes routes
           (GET "/stats" {params :params} proxy-github-request))
