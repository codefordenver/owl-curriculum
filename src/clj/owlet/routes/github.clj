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

(defn replace-dups [coll]
  (let [frequencies (frequencies coll)
        distinct-coll (distinct coll)]
    (flatten (map (fn [label]
                    (let [freq (get frequencies label)]
                        (concat [label] (repeat (- freq 1) ""))))
                  distinct-coll))))

(defn get-month-labels [weeks]
  (map #(f/unparse (f/formatter "MMM YYYY") (c/from-long (* % 1000))) weeks))

(defn proxy-github-request
  "proxy front end request"
  [_]
  (let [headers {:Authorization OWLET_GITHUB_TOKEN}
        {:keys [status body]} @(http/get "https://api.github.com/repos/codefordenver/owlet/stats/commit_activity" headers)]
    (when (= status 200)
      (let [stats (json/parse-string body true)
            weeks (map :week stats)
            labels (replace-dups (get-month-labels weeks))
            totals (map :total stats)
            every-other-month (take-nth 2 (distinct labels))]

        (ok {:status status
             :body   {:labels         labels
                      :reduced-labels (map #(if-not (.contains every-other-month %)
                                               ""
                                               %) labels)
                      :totals         totals}})))))

(defroutes routes
           (GET "/stats" {params :params} proxy-github-request))
