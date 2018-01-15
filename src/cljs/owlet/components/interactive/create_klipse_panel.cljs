(ns owlet.components.interactive.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [cljsjs.simplemde]))

(defn create-klipse-panel-component [panel-number]
  (let [text-id-base (str "panel-" panel-number "-text-")]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (let [smde-1 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" text-id-base "1"))
                                          :lineWrapping true})
               smde-2 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" text-id-base "2"))
                                          :lineWrapping true})]))
       :reagent-render
       (fn [panel-number]
        [:div.activity-info-wrap.box-shadow
         [:div.panel-heading.flexcontainer-wrap
          [:div.panel-number (str panel-number)]
          [:div {:style {:width "82%"}}
           [:h2 [:textarea {:id (str "panel-" panel-number "-heading")
                            :rows "2"
                            :placeholder "Heading"}]]]]
         [:div.panel-text
            [:textarea {:id (str text-id-base "1")
                        :placeholder "Optional text (markdown)"}]]
         [klipse-component "Python" "# Code editor"]
         [:div.panel-text
            [:h5 [:textarea {:id (str text-id-base "2")
                             :placeholder "Optional text (markdown)"}]]]])})))
