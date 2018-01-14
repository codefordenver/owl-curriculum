(ns owlet.components.interactive.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [cljsjs.simplemde]))

(defn create-klipse-panel-component [order]
  [:div.activity-info-wrap.box-shadow
     [:h2 (str order ". ")
      [:input {:type "text"
               :name "panel-heading"
               :placeholder "Heading"}]]
     [:div.panel-comments
        ;TODO: Replace with markdown editor (simplemde)
        [:h5 "optional markdown"]]
     [:div.code-block
        [:textarea {:rows "2"
                    :style {:width "100%"}
                    :placeholder "optional code block"}]]
     [klipse-component "Python" "# replace with starting code"]
     [:div.panel-comments
        ;TODO: Replace with markdown editor (simplemde)
        [:h5 [:b "Fun fact: "]
             "Printing \"Hello world!\" to the screen as your first program in a new language is considered good luck ~(˘▾˘)~"]
        [:h5 [:b "Values in \"quotes\" are called "
              [:i "strings. "]]
             "\"Hello world!\" is a string."]]])
