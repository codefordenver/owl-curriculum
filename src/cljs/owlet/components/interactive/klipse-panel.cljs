(ns owlet.components.interactive.klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [re-com.core :refer [h-box button]]
            [owlet.helpers :refer [showdown]]))

(defn klipse-panel-component [panel]
  (let [{:keys [id
                code
                text1
                text2
                heading
                language]} panel]
    [:div.activity-info-wrap
     [:div.panel-heading.flexcontainer-wrap
      [h-box
       :size "1"
       :align :start
       :children [[:div.panel-number id]
                  [:div {:style {:width "100%"
                                 :margin-left "0.15em"}}
                   [:h2 {:style {:margin-top "0.5em"
                                 :margin-bottom "0.7em"}}
                        heading]
                   [:h5 {:class "panel-text1"
                         "dangerouslySetInnerHTML"
                         #js{:__html (.makeHtml showdown text1)}}]]]]]

     [klipse-component language code]
     [:h5 {:class "panel-text2"
           "dangerouslySetInnerHTML"
           #js{:__html (.makeHtml showdown text2)}}]]))
