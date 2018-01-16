(ns owlet.views.not-found
  (:require [re-frame.core :as rf]
            [owlet.components.back :refer [back]]))

(defn not-found-view []
  [:div.not-found
   [:h2 [:mark.white.box-shadow [back] "404 - Not Found"]]])
