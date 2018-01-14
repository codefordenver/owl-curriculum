(ns owlet.components.interactive.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [cljsjs.simplemde]))

(defn create-klipse-panel-component [order]
  [:div.activity-info-wrap.box-shadow
     [:h2 (str order ". ")
      [:textarea {:rows "1"
                  :placeholder "Heading"}]]
     [:div.panel-comments
        ;TODO: Replace with markdown editor (simplemde)
        [:h5 "optional markdown"]]
     [:div.code-block
        [:textarea {:rows "2"
                    :placeholder "optional code block"}]]
     [klipse-component "Python" "# replace with starting code"]
     [:div.panel-comments
        ;TODO: Replace with markdown editor (simplemde)
        [:h5 "optional markdown"]]])
