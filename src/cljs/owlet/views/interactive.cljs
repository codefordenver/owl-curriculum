(ns owlet.views.interactive
  (:require [reagent.core :as reagent]))

(defn interactive-view []
  (reagent/create-class
     {:component-did-mount
      (fn []
        (let [tag (js/document.createElement "script")]
          (-> tag (.setAttribute "src" "https://storage.googleapis.com/app.klipse.tech/plugin_prod/js/klipse_plugin.min.js"))
          (js/document.body.appendChild tag)))
      :reagent-render
      (fn []
        [:pre
         [:code {:class "language-klipse-python"} "x = 1 + 1\nprint(x)"]])}))
