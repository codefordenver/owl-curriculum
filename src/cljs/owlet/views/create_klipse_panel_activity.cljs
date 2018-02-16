(ns owlet.views.create-klipse-panel-activity
  (:require [owlet.components.creation.create-klipse-panel :refer [create-klipse-panel-component]]
            [owlet.components.creation.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.creation.create-activity-title :refer [create-activity-title]]
            [owlet.components.creation.general-activity-text-fields :refer [general-activity-text-fields]]
            [owlet.components.creation.select-tags :refer [select-tags]]
            [owlet.components.creation.select-branches :refer [select-branches]]
            [owlet.components.creation.select-tags :refer [select-tags]]
            [owlet.components.creation.select-platform :refer [select-platform]]
            [owlet.views.login-only :refer [login-only-view]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn create-klipse-panel-activity []
 (let [panel-number (reagent/atom 1)]
   (fn []
     [:div.activity
      [:div.activity-wrap
       [:div.col-sm-12
        [:h1 "creating: a multi-panel coding activity"]
        [:div.activity-header.col-sm-12.col-lg-7
         [create-activity-title]]]
       [:div.activity-content.col-sm-12.col-lg-5
        [:h1 "metadata"]
        [:div.activity-creation-wrap
         [:div#select-categories
          [:div [:h5 [:mark "Branch"]]
           [select-branches true]]
          [:div [:h5 [:mark "Platform"]]
           [select-platform true]]
          [:div [:h5 [:mark "Tag(s)"]]
           [select-tags true]]]
         [general-activity-text-fields]]]
       [:div.activity-content.col-sm-12.col-lg-7
        [:h1 "activity panels"]
        (for [n (range @panel-number)]
          ^{:key (inc n)}
          [create-klipse-panel-component (inc n)])
        [:div.create-activity-buttons
         [:button.add-panel
          {:on-click #(swap! panel-number inc)}
          "+ Add another panel"]
         [:button.add-panel
          {:on-click #(swap! panel-number dec)}
          "- Remove last panel"][:br]
         [:button.save-activity
          "Save Activity"]]]]
      [create-activity-response-component :ok]])))

(defn create-klipse-panel-activity-view []
  (if @(rf/subscribe [:my-id])
    [create-klipse-panel-activity]
    [login-only-view]))
