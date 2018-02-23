(ns owlet.components.creation.create-klipse-code-validation
  (:require [reagent.core :as reagent]
            [re-com.core :refer [h-box v-box button]]
            [owlet.helpers :refer [remove-dom-element]]))

(defn create-klipse-code-validation-component [specify-number? & [id]]
  (if specify-number?
     [h-box
      :attr {:id id}
      :children [[v-box
                  :style {:margin-right "1.5em"}
                  :children [[:h6 "Slide:"]
                             [:input.pane-number {:placeholder "#"}]]]
                 [v-box
                  :size "1"
                  :children [[:h6 "Expected OUTPUT for this slide:"]
                             [:textarea.code.code-validation {:rows "3"
                                                              :placeholder "Hello World"}]]]
                 [button
                  :style {:margin "1.55em 0 1em 0.5em"
                          :padding "0 0.45em 0.1em 0.45em"}
                  :class "btn-danger"
                  :label "x"
                  :on-click #(remove-dom-element id)]]]
    [:div
     [:textarea.code-validation {:rows "3"
                                 :placeholder "Hello World"}]]))
