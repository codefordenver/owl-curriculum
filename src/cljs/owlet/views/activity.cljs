(ns owlet.views.activity
    (:require [owlet.views.general-activity :refer [general-activity-view]]
              [owlet.views.klipse-activity :refer [klipse-activity-view]]
              [owlet.views.klipse-panel-activity :refer [klipse-panel-activity-view]]
              [owlet.views.experimental-activity :refer [experimental-activity-view]]
              [owlet.components.activity.title :refer [activity-title]]
              [owlet.components.back :refer [back]]
              [re-frame.core :as rf]))

(defn activity-view
  "Renders activity according to type" []
  (let [activity @(rf/subscribe [:activity-in-view])
        activity-type (get-in activity [:sys :contentType :sys :id])
        title (get-in activity [:fields :title])
        author (get-in activity [:fields :author])]
    (rf/dispatch [:set-active-document-title! title])
    (if-not activity
      [:div.branch-activities-wrap
        [:h2 [:mark.box [:b "Loading..."]]]]
      (if (= activity "error")
        [:div.branch-activities-wrap
          [:h2 [:mark.box [back] [:b "This activity does not exist"]]]]
        [:div.activity
         [:div.activity-wrap
          [:div.activity-header.col-xs-12
           [activity-title title author]]
          (case activity-type
            "experimentalActivity" [experimental-activity-view activity]
            "klipseActivity" [klipse-activity-view activity]
            "klipsePanelActivity" [klipse-panel-activity-view activity]
            "activity" [general-activity-view activity])]]))))
