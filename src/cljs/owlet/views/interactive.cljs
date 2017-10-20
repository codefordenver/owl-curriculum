(ns owlet.views.interactive
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]))

(defn interactive-view []
  [klipse-component "language-klipse-python" "x = 1 + 1\nprint(x)"])
