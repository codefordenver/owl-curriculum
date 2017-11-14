(ns owlet.components.interactive.klipse
  (:require [reagent.core :as reagent]))

(defonce klipse-plugin "https://storage.googleapis.com/app.klipse.tech/plugin_prod/js/klipse_plugin.min.js")

(defn klipse-component [language code]
  (reagent/create-class
     {:component-did-mount
      (fn []
        (let [tag (js/document.createElement "script")]
          (-> tag (.setAttribute "src" klipse-plugin))
          (js/document.body.appendChild tag)))
      :reagent-render
      (fn []
        [:pre
         [:code {:class (case language
                          "Python" "language-klipse-python"
                          "Javascript" "language-klipse-eval-js"
                          "Clojure" "language-klipse")}
          code]])}))
