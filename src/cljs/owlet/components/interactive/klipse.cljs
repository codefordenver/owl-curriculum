(ns owlet.components.interactive.klipse
  (:require-macros [purnam.core :refer [? !> !]])
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [owlet.components.loading :refer [loading-component]]))

(def klipse-plugin-path "js/vendor/klipse_plugin.min.js")

(def klipse-container-class ".klipse-container")

(def ok-color
  ;; $aqua
  "#65D2C0")

(def err-color
  ;; $magenta
  "#FF0076")

(defn- reset-background-color! [element color]
  (! element.style.background color))

(defn klipse-component [language code]
  (let [evaled? (reagent/atom false)
        klipse-handler (fn [e]
                         (let [output-wrapper-div (? e.detail.result-element.display.wrapper)
                               detail (? e.detail.result)
                               [result _] (js->clj detail)
                               result->key (keyword result)
                               output (string/replace-first (? e.detail.result-element.display.lineDiv.textContent) "Output" "")]
                           (if (= result->key :ok)
                             (reset-background-color! output-wrapper-div ok-color)
                             (reset-background-color! output-wrapper-div err-color))))]
    (reagent/create-class
      {:component-will-unmount
       (fn []
         (let [klipse-container (js/document.querySelector klipse-container-class)]
           (when klipse-container
            (!> klipse-container.removeEventListener "klipse-snippet-evaled" klipse-handler))))
       :component-did-mount
       (fn []
         (let [tag (js/document.createElement "script")]
           (-> tag (.setAttribute "src" klipse-plugin-path))
           (js/document.body.appendChild tag)
           (js/setTimeout #(reset! evaled? true) 7000)))
       :reagent-render
       (fn [language code]
         [:div
          (when @evaled?
            (let [klipse-container (js/document.querySelector klipse-container-class)]
              (. klipse-container
                 (addEventListener "klipse-snippet-evaled" klipse-handler))))
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
