(ns owlet.components.creation.create-klipse-code-validation
  (:require [reagent.core :as reagent]
            [re-com.core :refer [h-box v-box]]))

(defn create-klipse-code-validation-component [specify-number?]
  (if specify-number?
    [h-box
     :children [[v-box
                 :style {:margin-right "1.5em"}
                 :children [[:h6 "Slide:"]
                            [:textarea.pane-number {:rows "1"
                                                    :placeholder "#"}]]]
                [v-box
                 :size "1"
                 :children [[:h6 "Expected output for this slide:"]
                            [:textarea.code-validation {:rows "3"
                                                        :placeholder "Hello World"}]]]]]
    [:div
     [:textarea.code-validation {:rows "3"
                                 :placeholder "Hello World"}]]))
