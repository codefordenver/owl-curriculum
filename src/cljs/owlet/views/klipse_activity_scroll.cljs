(ns owlet.views.klipse-activity-scroll
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]))

(defn klipse-activity-scroll-view []
  [:div.activity
   [:div.activity-wrap
    [:div.activity-header.col-xs-12
     [activity-title "Klipse Activity Scroll" "mslim"]]
    [:div.activity-content.col-xs-12.col-lg-6
     [klipse-component "Python" "# click here to start typing"]]]])
