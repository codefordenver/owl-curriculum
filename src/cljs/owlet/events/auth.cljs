(ns owlet.events.auth
  (:require [re-frame.core :as rf]
            [owlet.rf-util :refer [reg-setter fx-pipeline]]
            [owlet.firebase :as fb]))


(reg-setter :my-identity [:my-identity])

(reg-setter :firebase-users-change [:users])

(reg-setter
  :private
  [:my-identity :private]
  :interceptors fx-pipeline)


(rf/reg-event-fx
  :auth0-authenticated
  (fn [{db :db} [_ {:keys [delegation-token]}]]
    {:db (assoc-in db [:my-identity :auth-status] :pending-fb-sign-in)
     :firebase-sign-in [fb/firebase-auth-object
                        delegation-token
                        :firebase-sign-in-failed]}))


(rf/reg-event-fx
  :auth0-error
  (fn [_ [_ error]]
    (js/console.log "*** Error from Auth0: " error)))


(rf/reg-event-fx
  :firebase-sign-in-failed
  (fn [_ [_ fb-error]]
    (js/console.log "*** Error signing into Firebase: ", fb-error)
    {}))


(defn init-my-identity
  "Returns an :effects map (as returned by rf/reg-event-fx) based on the given
  db (@app-db map), a possibly nil Firebase user id (fb-id), and a keyword used
  to record the status of the current user's login. A nil fb-id indicates that
  no user is logged in, so the returned :my-identity map will consist of just
  the :auth-status value :not-logged-in. Listening to the user's private data
  on Firebase is started if fb-id is not nil, or this listening is stopped
  otherwise.
  "
  ([db]
   (init-my-identity db nil :unused))

  ([db fb-id status]
   (if fb-id
     (let [private-ref  (fb/path-str->db-ref (str "users/" (name fb-id)))
           presence-ref (fb/path-str->db-ref (str "presence/" (name fb-id)))
           new-db       (assoc db :my-identity {:firebase-id  fb-id
                                                :private-ref  private-ref
                                                :presence-ref presence-ref
                                                :auth-status  status})]
       {:db new-db
        :start-authorized-listening new-db})

     {:db (assoc db :my-identity {:auth-status :not-logged-in})
      :stop-authorized-listening db})))


(defn fb-user->id
  "Extracts the user id string from the given Firebase User object and returns
  it as a keyword. This is how we represent the current user in this app. See
  https://firebase.google.com/docs/reference/js/firebase.User#uid
  "
  [fb-user]
  (some-> fb-user .-uid keyword))

(rf/reg-fx
  :start-authorized-listening
  (fn [{{:keys [private-ref presence-ref]} :my-identity}]
    (fb/on-db-change "value" private-ref :private)
    (fb/note-presence-changes presence-ref)))


(rf/reg-fx
  :stop-authorized-listening
  (fn [{{:keys [private-ref presence-ref]} :my-identity}]
    (when private-ref (.off private-ref))
    (when presence-ref (.off presence-ref))))
    ; TODO: Does .off really work? Try logging out, :online is false -- OK.
    ;       Then disconnect from network, then reconnect. :online becomes true.
    ;       How? We're still logged out, so shouldn't know which user's :online
    ;       to set.


(defn log-out
  [{{{:keys [presence-ref]} :my-identity :as db} :db} _]
  (assoc (init-my-identity db)
         :firebase-reset-into-ref
         [presence-ref
          {:online             false
           :online-change-time fb/timestamp-placeholder}
          ;
          ; When finished with above update of location
          ; presence/<firebase-id>/, then dispatch an
          ; event to sign me out from Firebase:
          :then-sign-out]))
; Used here:
(rf/reg-event-fx :log-out log-out)


(rf/reg-event-fx
  :then-sign-out
  (fn [_ _]
    {:firebase-sign-out fb/firebase-auth-object}))


(defn firebase-auth-change
  "Effect handler ultimately triggered by Firebase when the user logs in or
  out, or when the app starts up, like when the page is refreshed. We depend
  upon the value in app-db at [:my-identity :auth-status] and the existence of
  the user id in the given Firebase User object to tell whether this is a
  log-in, log-out, or the continuation of a logged-in state.
  "
  [{{{old-fb-id :firebase-id
      status    :auth-status} :my-identity
     :as db} :db :as cofx}                  ; <-- coeffects
   [_ fb-user]]                             ; <-- event w/FB User object from
                                            ;     effect :firebase-sign-in, via
                                            ;     event :auth0-authenticated.
  (let [new-fb-id (fb-user->id fb-user)]
    (cond
      (nil? status)
      ; App just started up. Henceforth, :auth-status' value will not be nil.
      (init-my-identity db new-fb-id :already-logged-in)

      (nil? old-fb-id)
      (init-my-identity db new-fb-id :just-logged-in)

      (nil? new-fb-id)
      ; Just logged out.
      (init-my-identity db)

      (= old-fb-id new-fb-id)
      ;; The Auth0 lock object probably has been assigned more than one
      ;; .on("authenticated"...) listener, because owlet.main/view ran more
      ;; than once, thus calling owlet.auth0/on-authenticated more than once.
      ;; This can happen in development when the app hot-reloads code.
      ;; Do nothing.
      {}

      :else
      ; This shouldn't happen! the Firebase user id should only change from
      ; nil to not nil or vice-versa.
      (do
        (js/console.log
          "Error: Somehow trying to log in user" (name new-fb-id)
          "while user" (name old-fb-id)
          "is still logged in! Logging out now.")
        (log-out cofx [:log-out])))))
; Used here:
(rf/reg-event-fx :firebase-auth-change firebase-auth-change)

