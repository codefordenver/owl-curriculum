(ns owlet.components.creation.create-klipse-panel
  (:require [reagent.core :as reagent]
            [owlet.components.creation.custom-klipse-component :refer [custom-klipse-component]]
            [owlet.components.creation.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [re-com.core :refer [h-box button]]
            [owlet.components.creation.markdown-editor :refer [simplemde]]))

(defn create-klipse-panel-component [id]
  (reagent/create-class
    {:component-did-mount
     (fn []
       (let [smde-text1 (simplemde "text1")
             smde-text2 (simplemde "text2")]))
     :reagent-render
     (fn [id]
      [:div.activity-creation-wrap
       [:div.panel-heading.flexcontainer-wrap
        [h-box
         :size "1"
         :align :start
         :children [[:div.panel-number (str id)]
                    [:div {:style {:width "100%"}}
                     [:h2 [:textarea {:id (str "panel-" id "-heading")
                                      :rows "2"
                                      :placeholder "Heading"}]]]]]]
       [:div.panel-text
        [:h5 [:mark "Optional Text"]]
        [:textarea {:id "text1"
                    :placeholder "Accepts markdown"}]]
       [:div.panel-klipse
        [custom-klipse-component id]]
       [:div.panel-validation
        [:h6 "Below, enter the expected output for this panel:"]
        [create-klipse-code-validation-component false]]
       [:div.panel-text
        [:h5 [:mark "Optional Text"]]
        [:textarea {:id "text2"
                    :placeholder "Accepts markdown"}]]])}))
