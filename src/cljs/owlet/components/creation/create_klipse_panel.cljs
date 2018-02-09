(ns owlet.components.creation.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [owlet.components.creation.custom-klipse-component :refer [custom-klipse-component]]
            [owlet.components.creation.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [re-com.core :refer [h-box]]
            cljsjs.simplemde))

(defn create-klipse-panel-component [panel-number]
  (let [text-id-base (str "panel-" panel-number "-text-")]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (let [smde-1-id (str text-id-base "1")
               smde-2-id (str text-id-base "2")
               smde-1 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" smde-1-id))
                                          :lineWrapping true
                                          :autosave #js {:enabled true
                                                         :uniqueId smde-1-id}})
               smde-2 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" smde-2-id))
                                          :lineWrapping true
                                          :autosave #js {:enabled true
                                                         :uniqueId smde-2-id}})]))
       :reagent-render
       (fn [panel-number]
        [:div.activity-creation-wrap
         [:div.panel-heading.flexcontainer-wrap
          [h-box
           :width "100%"
           :align :start
           :children [[:div.panel-number (str panel-number)]
                      [:div {:style {:width "100%"}}
                       [:h2 [:textarea {:id (str "panel-" panel-number "-heading")
                                        :rows "2"
                                        :placeholder "Heading"}]]]]]]
         [:div.panel-text
          [:h5 [:mark "Optional Text"]]
          [:textarea {:id (str text-id-base "1")
                      :placeholder "Accepts markdown"}]]
         [:div.panel-klipse
          [:span {:style {:font-weight "500"
                          :margin "0 0.3em 0 0.05em"}}]
          [custom-klipse-component panel-number]]
         [:div.panel-validation
          [:h6 "Below, enter the output expected once this panel is completed:"]
          [create-klipse-code-validation-component false]]
         [:div.panel-text
          [:h5 [:mark "Optional Text"]]
          [:textarea {:id (str text-id-base "2")
                      :placeholder "Accepts markdown"}]]])})))
