(ns owlet.views.activity
    (:require [owlet.views.ro-activity :refer [ro-activity-view]]
              [owlet.views.klipse-activity :refer [klipse-activity-view]]
              [re-frame.core :as rf]))
  
  (defn activity-view 
    "Renders activity according to type" []
    (let [activity @(rf/subscribe [:activity-in-view])
          activity-type (get-in activity [:sys :contentType :sys :id])]
        (case activity-type
          "klipseActivity" [klipse-activity-view]
          "activity" [ro-activity-view])))
  