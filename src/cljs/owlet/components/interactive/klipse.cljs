(ns owlet.components.interactive.klipse
  (:require-macros [purnam.core :refer [? !> !]])
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [owlet.components.loading :refer [loading-component]]))

(def klipse-plugin-path "js/vendor/klipse_plugin.min.js")

(def klipse-container-class ".klipse-container")

(def ok-color "#65D2C0") ;; $aqua

(def err-color "#FF0076") ;; $magenta

(defn- reset-background-color! [element color]
  (! element.style.background color))

(defn- klipse-event-handler [e]
  (let [output-wrapper-div (? e.detail.resultElement.display.wrapper)
        hasError (? e.detail.hasError)]
    (if hasError
      (reset-background-color! output-wrapper-div err-color)
      (reset-background-color! output-wrapper-div ok-color))))

(defn klipse-component [language code & [inline?]]
  (let [evaled? (reagent/atom false)]
    (reagent/create-class
      {:component-will-unmount
       (fn []
         (when-let [klipse-container (js/document.querySelector klipse-container-class)]
           (!> klipse-container.removeEventListener
               "klipse-snippet-evaled" klipse-event-handler))
         (-> (js/document.querySelector "#klipse-script")
             (.remove)))
       :component-did-mount
       (fn []
         (let [tag (js/document.createElement "script")]
           (-> tag (.setAttribute "src" klipse-plugin-path))
           (-> tag (.setAttribute "id" "klipse-script"))
           (js/document.body.appendChild tag)
           ;; TODO: ADD SUPPORT FOR ERROR ONLOAD
           (js/setTimeout #(reset! evaled? true) 5000)))
       :reagent-render
       (fn [language code]
         [:div.klipse-component
          (when @evaled?
            (let [klipse-container (js/document.querySelector klipse-container-class)]
              (!> klipse-container.addEventListener "klipse-snippet-evaled" klipse-event-handler)))
          [:pre
           [:code {:class (case (string/lower-case language)
                            "python" "language-klipse-python"
                            "javascript" "language-klipse-eval-js"
                            "clojure" "language-klipse")
                   :style {:display "none"}}
            (let [pattern #"\\n"]
              (if (and (nil? inline?) (re-find pattern code))
                (string/replace code pattern "\n")
                code))]
           (when-not @evaled? [loading-component])]])})))
