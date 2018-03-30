(ns owlet.views.general-activity
  (:require [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.activity.embed :refer [activity-embed]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.components.activity.image-gallery :refer [activity-image-gallery]]
            [owlet.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn general-activity-view
  "General activity" []
  (let [activity @(rf/subscribe [:activity-in-view])]
    (if-not activity
      [:div.branch-activities-wrap
        [:h2 [:mark [:b "Loading..."]]]]
      (if (= activity "error")
        [:div.branch-activities-wrap
          [:h2 [:mark [back] [:b "This activity does not exist"]]]]
        (let [{:keys [fields]} activity]
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
                        image-gallery-items]} fields]
            (rf/dispatch [:set-active-document-title! title])
            [:div.activity
             [:div.activity-wrap
              [:div.activity-header.col-xs-12
               [activity-title title author]]
              [:div.activity-content.col-xs-12.col-lg-8
               [activity-embed embed tags preview]
               (when (seq image-gallery-items)
                [activity-image-gallery image-gallery-items])]
              [:div.activity-content.col-xs-12.col-lg-4
               [activity-info platform summary why preRequisites materials]
               (when challenge
                [activity-challenge challenge])
               (when inspiration
                [activity-inspiration inspiration])]]]))))))
