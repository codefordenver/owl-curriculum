(ns owlet.views.create-klipse-slides-activity
  (:require [owlet.components.interactive.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [owlet.components.interactive.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.creation.select-platform :refer [select-platform]]
            [owlet.components.creation.select-tags :refer [select-tags]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]
            [owlet.views.login-only :refer [login-only-view]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn create-klipse-slides-activity []
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
        [select-platform false]
        [select-tags true]
        [:div.activity-info-wrap.box-shadow
         (for [n (range @panel-number)]
           ^{:key (inc n)}
           [create-klipse-code-validation-component true])]
        [:div.create-activity-buttons
         [:button.add-panel
          {:on-click #(swap! panel-number inc)}
          "+ Add another code validation"]
         [:button.save-activity
          "Save Activity"]]]]
      [create-activity-response-component :ok]])))

(defn create-klipse-slides-activity-view []
  (if @(rf/subscribe [:my-id])
    [create-klipse-slides-activity]
    [login-only-view]))
