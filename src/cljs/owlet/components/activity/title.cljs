(ns owlet.components.activity.title
  (:require [re-frame.core :as rf]
            [owlet.components.back :refer [back]]))

(defn activity-title [title author]
  [:div.activity-title-wrap
   [:h1 [:mark [back] title]]
   [:h5.author [:mark.white "Created by: " author]]])
