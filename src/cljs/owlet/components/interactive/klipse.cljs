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

(defn klipse-component [language code & [slides?]]
  (let [evaled? (reagent/atom false)]
    (reagent/create-class
      {:component-will-unmount
       (fn []
         (js/document.removeEventListener "klipse-snippet-evaled" klipse-event-handler)
         (-> (js/document.querySelector "#klipse-script")
             (.remove)))
       :component-did-mount
       (fn []
         (let [tag (js/document.createElement "script")]
           (-> tag (.setAttribute "src" klipse-plugin-path))
           (-> tag (.setAttribute "id" "klipse-script"))
           (js/document.body.appendChild tag)
           (js/document.addEventListener "klipse-snippet-evaled" klipse-event-handler)))
       :reagent-render
       (fn [language code]
         [:div
          [:div.klipse-component
           [:pre
            [:code {:class (case (string/lower-case language)
                             "python" "language-klipse-python"
                             "javascript" "language-klipse-eval-js"
                             "clojure" "language-klipse")
                    :style {:display "none"}}
             (let [pattern #"\\n"]
               (if (re-find pattern code)
                 (string/replace code pattern "\n")
                 code))]
            (when-not @evaled? [loading-component])]]
          (when slides?
            [:div#nav-buttons
             [:button#prev-slide "Previous Slide"]
             [:button#next-slide.inactive "Next Slide"]])])})))
