(ns owlet.views.create-klipse-slides-activity
  (:require [owlet.components.creation.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [owlet.components.creation.create-activity-response :refer [create-activity-response-component]]
            [owlet.components.creation.create-activity-title :refer [create-activity-title]]
            [owlet.components.creation.select-branches :refer [select-branches]]
            [owlet.components.creation.select-tags :refer [select-tags]]
            [owlet.components.creation.select-platform :refer [select-platform]]
            [owlet.components.creation.general-activity-text-fields :refer [general-activity-text-fields]]
            [owlet.components.creation.custom-klipse-component :refer [custom-klipse-component]]
            [owlet.views.login-only :refer [login-only-view]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            cljsjs.simplemde))

(def input-url (reagent/atom ""))
(def embed-url (reagent/atom ""))
(def show-slides? (reagent/atom false))
(def panel-number (reagent/atom 1))

(defn create-klipse-slides-activity []
   [:div.activity
    [:div.activity-wrap
     [:div.col-sm-12
      [:h1 "creating: a slides-based coding activity"]
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
        [:div [:h5 [:mark "Tags"]]
         [select-tags true]]]
       [general-activity-text-fields]]]
     [:div.activity-content.col-sm-12.col-lg-7
      [:h1 "activity content"]
      [:div.activity-creation-wrap
       [custom-klipse-component 0]
       [:h5 [:mark "Slideshow"]]
       [:h6 "Create your slideshow at "
        [:a {:href "https://slides.com"
             :target "_blank"}
          "slides.com"]]
       [:input#iframe-url {:type "text"
                           :placeholder "Paste the direct link to your slides.com presentation here."
                           :value @input-url
                           :on-change (fn [v]
                                        (reset! input-url (.. v -target -value))
                                        (if (clojure.string/starts-with? @input-url "http://slides.com")
                                          (do
                                            (reset! embed-url (str @input-url "/embed?style=light"))
                                            (reset! show-slides? true))
                                          (reset! show-slides? false)))}]
       (when @show-slides?
         [:div.embed-container
          [:iframe#iframe-preview {:src @embed-url
                                   :scrolling "no"
                                   :frameBorder "0"
                                   :allowFullScreen true}]])
       [:h5 [:mark "Code Validations"]]
       (for [n (range @panel-number)]
         ^{:key (inc n)}
         [create-klipse-code-validation-component true])
       [:button.btn.add-validation {:on-click #(swap! panel-number inc)}
        "+ Add another code validation"]
       [:button.btn.rem-validation {:on-click #(when (> @panel-number 1)
                                                 (swap! panel-number dec))}
        "- Remove last code validation"]]
      [:div.create-activity-buttons
       [:button.save-activity
        "Save Activity"]]]
     [create-activity-response-component :ok]]])

(defn create-klipse-slides-activity-view []
  (if @(rf/subscribe [:my-id])
    [create-klipse-slides-activity]
    [login-only-view]))
