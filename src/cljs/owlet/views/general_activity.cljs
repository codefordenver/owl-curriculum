(ns owlet.views.general-activity
  (:require [owlet.components.activity.embed :refer [activity-embed]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.components.activity.image-gallery :refer [activity-image-gallery]]
            [re-frame.core :as rf]))

(defn general-activity-view [activity]
  (let [{:keys [why
                title
                embed
                author
                tags
                summary
                preview
                challenge
                materials
                inspiration
                preRequisites
                platform
                image-gallery-items]} (:fields activity)]
    [:div.activity-content.col-xs-12.col-lg-8
     [activity-embed embed tags preview]
     (when (seq image-gallery-items)
      [activity-image-gallery image-gallery-items])]
    [:div.activity-content.col-xs-12.col-lg-4
     [activity-info platform summary why preRequisites materials]
     (when challenge
      [activity-challenge challenge])
     (when inspiration
      [activity-inspiration inspiration])]))
