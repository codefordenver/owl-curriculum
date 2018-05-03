(ns owlet.events.create-activity
  (:require [re-frame.core :as rf]))


(defn text
  [db [_ textarea-value field-id :as event]]
  ; For now, we just show the event. We probably want to issue effect
  ; :firebase-reset-ref or similar at this point.
  (js/console.log "text handler event:\n\t" (str event))
  db)
; Used here:
(rf/reg-event-db
  ::text
  text)
