(ns owlet.views.create-klipse-panel-activity
  (:require [owlet.components.interactive.create-klipse-panel :refer [create-klipse-panel-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]))

(defn create-klipse-panel-activity-view []
  [:div.activity
   [:div.activity-wrap
    [:div.activity-header.col-xs-12
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
     [create-klipse-panel-component 1]
     [create-klipse-panel-component 2]
     ;TODO: When clicked, call [create-klipse-panel-component] with the next number in sequence
     [:button.add-panel {:style {:margin-bottom "3em"}} "+ Add another panel"]]]])
