(ns owlet.views.subscribed
  (:require [owlet.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn subscribed-view []
  [:div.not-found
   [:h2 [:mark [back] "Thanks! You are now subscribed."]]
   [:h3 [:mark "We will send an email notification to "
                     [:span {:style {:color "#0275d8"}}
                        @(rf/subscribe [:subscriber-info])]
                     " whenever a new activity is posted."]]])
