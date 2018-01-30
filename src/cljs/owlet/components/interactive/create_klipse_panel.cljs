(ns owlet.components.interactive.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [owlet.components.interactive.create-klipse-code-validation :refer [create-klipse-code-validation-component]]
            cljsjs.simplemde))

(defn remount-klipse [remount?]
  (js/setTimeout #(swap! remount? not) 100))

(defn create-klipse-panel-component [panel-number]
  (let [text-id-base (str "panel-" panel-number "-text-")
        language (reagent/atom "python")
        remount? (reagent/atom true)]
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
         [:div.panel-klipse
          [:span {:style {:font-weight "500"
                          :margin "0 0.3em 0 0.05em"}}
                 [:mark "Code Evaluator"]]
          [:select {:id (str "panel-" panel-number "-language")
                    :value @language
                    :on-change (fn [e]
                                 (when (not= @language (-> e .-target .-value))
                                   (swap! remount? not)
                                   (remount-klipse remount?))
                                 (reset! language (-> e .-target .-value)))}
           [:option {:value ""} "None"]
           [:option {:value "python"} "Python"]
           [:option {:value "javascript"} "JavaScript"]
           [:option {:value "clojure"} "Clojure"]]
          (when @remount?
            (case @language
              "python" [klipse-component @language "# type here"]
              "javascript" [klipse-component @language "// type here"]
              "clojure" [klipse-component @language ";; type here"]
              "" [:div {:style {:margin-bottom "2em"}} ""]))]
         [:div.panel-validation
          [:span {:style {:font-weight "500"
                          :margin "0 0.3em 0 0.05em"}}
           "Below, place the output of the code once this panel is completed"]
          [create-klipse-code-validation-component false]]
         [:div.panel-text
          [:textarea {:id (str text-id-base "2")
                      :placeholder "Optional text (markdown)"}]]])})))
