(ns owlet.views.experimental-activity
  (:require [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.views.experimental.secret-key-exchange :as secret-key-exchange]
            [re-frame.core :as rf]))

(defn experimental-activity-view [activity]
  (let [{:keys [title
                branch
                platform
                author
                tags
                summary
                preview
                preRequisites
                json
                platform]} (:fields activity)]
    [:div.activity-content.col-xs-12
     [secret-key-exchange/activity]]))
