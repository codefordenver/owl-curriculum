(ns owlet.views.create-klipse-slides-activity
  (:require [owlet.components.interactive.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [owlet.components.interactive.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.creation.select-branches :refer [select-branches]]
            [owlet.components.creation.select-tags :refer [select-tags]]
            [owlet.components.creation.select-platform :refer [select-platform]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]
            [owlet.views.login-only :refer [login-only-view]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(def input-url (reagent/atom ""))
(def embed-url (reagent/atom "http://localhost:3000/#/"))

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
        [:div#select-categories.box-shadow
         [:div [:h5 [:mark "Branch"]]
          [select-branches true]]
         [:div [:h5 [:mark "Platform"]]
          [select-platform true]]
         [:div [:h5 [:mark "Tags"]]
          [select-tags true]]]
        [:textarea#iframe-url {:value @input-url
                               :on-change (fn [v]
                                            (reset! input-url (.. v -target -value))
                                            (when (clojure.string/starts-with? @input-url "http://slides.com")
                                              (reset! embed-url (str @input-url "/embed?style=light"))))}]
        [:iframe#preview {:src @embed-url
                          :scrolling "no"
                          :frameBorder "0"
                          :allowFullScreen true}]
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
