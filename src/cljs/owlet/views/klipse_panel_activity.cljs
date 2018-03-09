(ns owlet.views.klipse-panel-activity
  (:require [owlet.components.interactive.klipse-panel :refer [klipse-panel-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.components.back :refer [back]]
            [re-frame.core :as rf]
            [owlet.components.activity.comments :refer [activity-comments]]))

(defn klipse-panel-activity-view []
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
                        platform
                        panels]} fields]
            (rf/dispatch [:set-active-document-title! title])
            [:div.activity
             [:div.activity-wrap
              [:div.activity-header.col-xs-12
               [activity-title title author]]
              ; [:div.activity-content.col-xs-12.col-lg-6
              ;  [activity-embed embed tags preview]]
              ; [:div.activity-content.col-xs-12.col-lg-6
              ;  [:h2 {:style {:margin-top "40px"}}
              ;   [:mark "Edit this code:"]]
              ;  [klipse-component language code]]
              [:div.activity-content.col-xs-12.col-lg-8
                (for [panel panels :let [order (str (key panel))]]
                  ^{:key order}
                  [klipse-panel-component (val panel)])]
              [:div.activity-content.col-xs-12.col-lg-4
                [activity-info platform summary nil preRequisites nil]]
              [:div.activity-content.col-xs-12.col-lg-8
                [activity-comments]]]]))))))
