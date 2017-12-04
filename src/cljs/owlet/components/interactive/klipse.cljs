(ns owlet.components.interactive.klipse
  (:require-macros
    [purnam.core :refer [?]])
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [owlet.components.loading :refer [loading-component]]))

(def klipse-plugin "js/vendor/klipse_plugin.min.js")

(def klipse-container-class ".klipse-container")

(defn klipse-component [language code]
  (let [evaled? (reagent/atom false)]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (let [tag (js/document.createElement "script")]
           (-> tag (.setAttribute "src" klipse-plugin))
           (js/document.body.appendChild tag)
           (js/setTimeout #(reset! evaled? true) 5000)))
       :reagent-render
       (fn [language code]
         [:div
          (when @evaled?
            (let [klipse-container (js/document.querySelector klipse-container-class)]
              (. klipse-container
                 (addEventListener "klipse-snippet-evaled"
                                   (fn [e]
                                     (let [text (? e.detail.state.result-element.display.maxLine.text)
                                           has-error? (not (nil? (re-find #"error" text)))]
                                       (js/console.log has-error?)
                                       (js/console.log text)))))))

          [:pre
           [:code {:class (case language
                            "Python" "language-klipse-python"
                            "Javascript" "language-klipse-eval-js"
                            "Clojure" "language-klipse")
                   :style {:display "none"}}
            (let [pattern #"\\n"]
              (if (re-find pattern code)
                (string/replace code pattern "\n")
                code))]
           [loading-component]]])})))
