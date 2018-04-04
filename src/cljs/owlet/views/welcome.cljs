(ns owlet.views.welcome
  (:require [owlet.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet.components.login :refer [signup-button]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn welcome-view []
  (reagent/create-class
     {:component-did-mount
      (fn []
        (js/Masonry. (js/document.querySelector ".grid")
                     #js {:itemSelector ".grid-item"}))
      :component-did-update
      (fn []
        (js/Masonry. (js/document.querySelector ".grid")
                     #js {:itemSelector ".grid-item"}))
      :reagent-render
      (fn []
        (let [activities (take 3 (shuffle @(rf/subscribe [:activities])))]
          [:div.welcome-container
           [:div.welcome-wrap
            [:div.welcome-image
             [:img {:src "img/owlet-owl.png"}]]
            [:div.welcome-text
             [:h1 [:mark "Welcome to Owlet!"]]
             [:p "Explore free, self-guided projects for creative learning in "
              [:b "STEM: Science, Technology, Engineering, Art, and Math!"]]
             [:p "Explora proyectos creativos para aprender ciencia, tecnología, arte y mate. ¡Gratis!"]
             [signup-button]
             [:button.btn-explore {:href "/#/branches"}
              "Explore"]]]
           [:div.featured-projects-wrap
            [:h2 "Featured Projects"]
            [:div.grid
             (for [activity activities
                   :let [fields (:fields activity)
                         entry-id (get-in activity [:sys :id])]]
               ^{:key [entry-id (gensym "key-")]}
               [activity-thumbnail fields entry-id "featured"])]]]))}))
