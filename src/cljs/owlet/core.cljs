(ns owlet.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as rf]
              [dirac.runtime :as dirac]
              [owlet.events.contentful]
              [owlet.events.auth]
              [owlet.events.app]
              [owlet.subs]
              [owlet.routes :as routes]
              [owlet.main :as main]
              [owlet.config :as config]
              [re-frisk.core :refer [enable-re-frisk!]]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (dirac/install!)))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [main/view]
                  (.getElementById js/document "app")))

(defn ^:export init! []
  (routes/app-routes)
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
