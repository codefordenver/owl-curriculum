(ns owlet.views.klipse-activity
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [owlet.components.activity.embed :refer [activity-embed]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [re-frame.core :as rf]))

(defn klipse-activity-view [activity]
  (let [{:keys [why
                title
                embed
                author
                tags
                summary
                preview
                materials
                preRequisites
                platform
                language
                code]} (:fields activity)]
    [:div.activity-content.col-xs-12.col-lg-6
     [activity-embed embed tags preview]]
    [:div.activity-content.col-xs-12.col-lg-6
     [:h2 {:style {:margin-top "40px"}}
      [:mark "Edit this code:"]]
     [klipse-component language code true]]
    [:div.activity-content.col-xs-12
     [activity-info platform  summary why preRequisites materials]]))
