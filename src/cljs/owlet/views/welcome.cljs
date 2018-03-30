(ns owlet.views.welcome
  (:require [owlet.components.activity-thumbnail :refer [activity-thumbnail]]
            [re-frame.core :as rf]))

(defn welcome-view []
  (let [activities @(rf/subscribe [:activities])]
    [:div.welcome-container
     [:div.welcome-wrap
      [:div.welcome-image ""]
      [:div.welcome-text
       [:h1 [:mark "Welcome to Owlet!"]]
       [:p "Explore free, self-guided projects for creative learning in "
        [:b "STEM:"]
        " Science, Technology, Engineering, Art, and Math!"]
       [:p "Explora proyectos creativos para aprender ciencia, tecnología, arte y mate. ¡Gratis!"]]]
     [:div.featured-projects-wrap
      [:h2 "Featured Projects"]
      [:div.grid
       (for [activity activities
             :let [fields (:fields activity)
                   entry-id (get-in activity [:sys :id])]]
         ^{:key [entry-id (gensym "key-")]}
         [activity-thumbnail fields entry-id "featured"])]]]))
