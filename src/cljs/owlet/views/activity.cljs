(ns owlet.views.activity
    (:require [owlet.views.general-activity :refer [general-activity-view]]
              [owlet.views.klipse-activity :refer [klipse-activity-view]]
              [owlet.views.klipse-panel-activity :refer [klipse-panel-activity-view]]
              [owlet.views.experimental-activity :refer [experimental-activity-view]]
              [re-frame.core :as rf]))

(defn activity-view
  "Renders activity according to type" []
  (let [activity @(rf/subscribe [:activity-in-view])
        activity-type (get-in activity [:sys :contentType :sys :id])]
      (case activity-type
        "experimentalActivity" [experimental-activity-view]
        "klipseActivity" [klipse-activity-view]
        "klipsePanelActivity" [klipse-panel-activity-view]
        "activity" [general-activity-view])))
