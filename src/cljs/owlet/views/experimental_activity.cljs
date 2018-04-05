(ns owlet.views.experimental-activity
  (:require [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn experimental-activity-view []
  (let [activity @(rf/subscribe [:activity-in-view])]
    (if-not activity
      [:div.branch-activities-wrap
        [:h2 [:mark.box [:b "Loading..."]]]]
      (if (= activity "error")
        [:div.branch-activities-wrap
          [:h2 [:mark.box [back] [:b "This activity does not exist"]]]]
        (let [{:keys [fields]} activity]
          (let [{:keys [title
                        branch
                        platform
                        author
                        tags
                        summary
                        preview
                        preRequisites
                        platform]} fields]
            (rf/dispatch [:set-active-document-title! title])
            [:div.activity
             [:div.activity-wrap
              [:div.activity-header.col-xs-12
               [activity-title title author]]
              [:div.activity-content.col-xs-12.col-lg-8]]]))))))
