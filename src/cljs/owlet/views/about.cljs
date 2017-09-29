(ns owlet.views.about
  (:require [owlet.components.email-notification :refer [email-notification]]))

(defn about-view []
  (fn []
    [:pre [:code.language-klipse-python
           "```
					 x = 1 + 1
					 print(x)
					 ```"]]))