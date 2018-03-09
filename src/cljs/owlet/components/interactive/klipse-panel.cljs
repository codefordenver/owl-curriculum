(ns owlet.components.interactive.klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [re-com.core :refer [h-box button]]
            [owlet.helpers :refer [showdown]]))

(defn klipse-panel-component [panel]
  (let [{:keys [code
                text1
                text2
                heading
                language]} panel]
    [:div.activity-info-wrap
     [:div.panel-heading.flexcontainer-wrap
      [h-box
       :size "1"
       :align :start
       :children [[:div.panel-number "1"]
                  [:div {:style {:width "100%"}}
                   [:h2 {:style {:margin-bottom "1em"}}
                        heading]
                   [:h5 {"dangerouslySetInnerHTML"
                         #js{:__html (.makeHtml showdown text1)}}]]]]]

     [klipse-component language code]
     [:h5 {"dangerouslySetInnerHTML"
           #js{:__html (.makeHtml showdown text2)}}]]))
