(ns owlet.views.interactive
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]))

(defn interactive-view []
  [:div
   [klipse-component "language-klipse-python" "print('hello world')"]])
