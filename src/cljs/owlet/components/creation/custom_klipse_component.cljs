(ns owlet.components.creation.custom-klipse-component
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]))

(defn remount-klipse [remount?]
  (js/setTimeout #(swap! remount? not) 100))

(def remount? (reagent/atom false))
(def language (reagent/atom "python"))
(def code (reagent/atom ""))
(defn custom-klipse-component [panel-number]
  [:div.code-eval
   [:h5 [:mark "Code Evaluator"]]
   [:select.language {:id (str "panel-" panel-number "-language")
                      :value @language
                      :on-change #(reset! language (-> % .-target .-value))}
    [:option {:value "python"} "Python"]
    [:option {:value "javascript"} "JavaScript"]
    [:option {:value "clojure"} "Clojure"]]
   [:h6 "Initial Code:"]
   [:input#code {:type "text"
                 :placeholder "Enter code to provide at the start. Use \\n for line breaks."
                 :value @code
                 :on-change #(reset! code (-> % .-target .-value))}]
   [:div {:style {:text-align "center"}}
    [:button.btn.code-preview {:on-click (fn []
                                           (reset! remount? true)
                                           (swap! remount? not)
                                           (remount-klipse remount?))}
     "Generate Preview"]]
   (when @remount?
     [:div
      [:h6 "Preview"]
      [klipse-component @language @code]])])
