(ns owlet.components.creation.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [owlet.components.creation.custom-klipse-component :refer [custom-klipse-component]]
            [owlet.components.creation.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
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
          [:div.panel-number (str panel-number)]
          [:div {:style {:width "82%"}}
           [:h2 [:textarea {:id (str "panel-" panel-number "-heading")
                            :rows "2"
                            :placeholder "Heading"}]]]]
         [:div.panel-text
          [:textarea {:id (str text-id-base "1")
                      :placeholder "Optional text (markdown)"}]]
         [:div.panel-klipse
          [:span {:style {:font-weight "500"
                          :margin "0 0.3em 0 0.05em"}}]
          [custom-klipse-component panel-number]]
         [:div.panel-validation
          [:span {:style {:font-weight "500"
                          :margin "0 0.3em 0 0.05em"}}
           "Below, place the output of the code once this panel is completed"]
          [create-klipse-code-validation-component false]]
         [:div.panel-text
          [:textarea {:id (str text-id-base "2")
                      :placeholder "Optional text (markdown)"}]]])})))
