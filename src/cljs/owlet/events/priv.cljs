(ns owlet.events.priv
  (:require [re-frame.core :as rf]
            [owlet.firebase :as fb]
            [owlet.rf-util :refer [pipe-setter-transform!]]))


(pipe-setter-transform!
  :private                             ; Handler registered in owlet.auth.
  ::roles->set                         ; Label for this transform func.
  #(update % :roles (comp set keys)))  ; Transform func. makes roles a set.


(defn path-str->my-db-ref
  "Handy way to obtain a Firebase Database ref to store or retrieve data
  for the current user. The first argument is the re-frame app-db, and the
  (optional) second argument is the path string (or nil) indicating the
  location within the user's particular node in the Firebase Database."
  ([db]
   (path-str->my-db-ref db nil))

  ([{{fb-id :firebase-id} :my-identity :as db} path]
   (if fb-id
     (let [divider (and (or (not= "/" (first path)) nil) "/")]
       (fb/path-str->db-ref (str "users/" (name fb-id) divider path)))

     (assoc db :on-app-failure {:show? true
                                :msg "You must be logged in to do that."}))))


(defn toggle-user-role
  [{{{{roles :roles} :private} :my-identity :as db} :db} [_ role-kw]]
  (let [assign? (not (role-kw roles))]
    {:firebase-reset-ref
     [(path-str->my-db-ref db (str "roles/" (name role-kw)))
      (or assign? nil)
      :check-error "Unable to save role assignment."]}))
(rf/reg-event-fx :toggle-user-role toggle-user-role)


(defn check-error
  [db [_ {err :error-reason} msg]]
  (if err
    (assoc db :on-app-failure {:show? true :msg msg})
    db))
(rf/reg-event-db :check-error check-error)

