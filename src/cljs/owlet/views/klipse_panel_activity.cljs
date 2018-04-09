(ns owlet.views.klipse-panel-activity
  (:require [owlet.components.interactive.klipse-panel :refer [klipse-panel-component]]
            [owlet.components.activity.info :refer [activity-info]]
            [owlet.components.activity.inspiration :refer [activity-inspiration]]
            [owlet.components.activity.challenge :refer [activity-challenge]]
            [owlet.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn klipse-panel-activity-view [activity]
  (let [{:keys [title
                branch
                platform
                author
                tags
                summary
                preview
                preRequisites
                platform
                panels]} (:fields activity)]
    [:div.activity-content.col-xs-12.col-lg-8
      (for [panel panels :let [id (:id panel)]]
        ^{:key id}
        [klipse-panel-component panel])]
    [:div.activity-content.col-xs-12.col-lg-4
      [activity-info platform summary nil preRequisites nil]]))
