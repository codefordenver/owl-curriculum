(ns owlet.components.creation.create-klipse-panel
  (:require [reagent.core :as reagent]
            [owlet.components.creation.custom-klipse-component :refer [custom-klipse-component]]
            [owlet.components.creation.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            [re-com.core :refer [h-box button]]
            [owlet.components.creation.markdown-editor :refer [simplemde]]
            [owlet.events.create-activity :as create]))


(defn create-klipse-panel-component [id]
  (reagent/create-class
    {:component-did-mount
     (fn []
       (simplemde :text1 :blur ::create/text)
       (simplemde :text2 :blur ::create/text))

     :reagent-render
     (fn [id]
      [:div.activity-creation-wrap
       [:div.panel-heading
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
