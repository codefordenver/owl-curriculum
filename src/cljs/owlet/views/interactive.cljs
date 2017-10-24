(ns owlet.views.interactive
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]))

(defn interactive-view []
  [:div
   [klipse-component "language-klipse-python" "x = 1 + 1\nprint(x)"]
   [klipse-component "language-klipse-eval-js" "var x = 1 + 1\nconsole.log(x)"]])
