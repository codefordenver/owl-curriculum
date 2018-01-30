(ns owlet.views.create-klipse-panel-activity
  (:require [owlet.components.interactive.create-klipse-panel :refer [create-klipse-panel-component]]
            [owlet.components.interactive.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]
            [owlet.components.creation.select-branch :refer [select-branch]]
            [owlet.views.login-only :refer [login-only-view]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn create-klipse-panel-activity []
 (let [panel-number (reagent/atom 1)]
   (fn []
     [:div.activity
      [:div.activity-wrap
       [:div.activity-header.col-sm-12.col-lg-7
        [:div.activity-title-wrap
         [:h1 [:mark [back]
               [:input {:type "text"
                        :name "title"
                        :placeholder "Activity Title"}]]]
         [:h5.author [:mark.white "Created by: "
                      [:input {:type "text"
                               :name "author"
                               :placeholder "Author"}]]]]]
       [:div.activity-content.col-sm-12.col-lg-7
        [select-branch]
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

(defn create-klipse-panel-activity-view []
  (if @(rf/subscribe [:my-id])
    [create-klipse-panel-activity]
    [login-only-view]))
