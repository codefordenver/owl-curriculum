(ns owlet.views.create-klipse-panel-activity
  (:require [owlet.components.interactive.create-klipse-panel :refer [create-klipse-panel-component]]
            [owlet.components.interactive.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]
            [reagent.core :as reagent]))

(defn create-klipse-panel-activity-view []
 (let [panel-number (reagent/atom 1)]
   (fn []
     [:div.activity
      [:div.activity-wrap
       [:div.activity-header.col-sm-12.col-lg-7
        [:div.activity-title-wrap
         [:h1 [:mark.white.box-shadow [back]
               [:input {:type "text"
                        :name "title"
                        :placeholder "Activity Title"}]]]
         [:h5.author "Created by: "
          [:input {:type "text"
                   :name "author"
                   :placeholder "Author"}]]]]
       [:div.activity-content.col-sm-12.col-lg-7
        (for [n (range @panel-number)]
          ^{:key (inc n)}
          [create-klipse-panel-component (inc n)])
        [:div.create-activity-buttons
         [:button.add-panel
          {:on-click #(swap! panel-number inc)}
          "+ Add another panel"]
         [:button.save-activity
          "Save Activity"]]]]
      [create-activity-response-component :ok]])))
