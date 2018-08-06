(ns owlet.events.priv
  (:require [re-frame.core :as rf]
            [owlet.firebase :as fb]
            [re-futil.getter-setter-pipeline :as pipeline]))


(defn path-str->my-db-ref
  "Handy way to obtain a Firebase Database ref to store or retrieve data for
  the current logged-in user. The first argument is the re-frame @app-db map,
  and the (optional) second argument is the path string (or nil) indicating the
  location within the user's particular node in the Firebase Database. If the
  second arg. is not provided or empty, returns a ref to the root of the user's
  private data."

  ([db]
   (path-str->my-db-ref db nil))

  ([{{private-ref :private-ref} :my-identity :as db} path]
   (if private-ref
     (.child private-ref (if (empty? path) "/" path))
     (assoc db :on-app-failure {:show? true
                                :msg "You must be logged in to do that."}))))


(defn inc-login-count
  "A transform function for the pipeline of the fx-transform interceptor on the
  :private event handler. It thus takes two arguments, the effects map being
  transformed through the pipeline, and the coeffects available to the
  interceptor. Returns an effects map like the given one, except that that it
  increments the :login-count value in app-db and in Firebase, but only when
  the current user just logged in.
  "
  [{db :db :as effects}                           ; Effects in pipeline.
   {[_ {remote-count :login-count}]      :event   ; Coeffects for interceptor.
    {{status :auth-status} :my-identity} :db}]

  (if (= status :just-logged-in)
    ; The :firebase-auth-change handler just ran, responding to my login, which
    ; initiated listening to the current user's private data (:private-ref).
    ; That's why we're now handling the :private event and its fx pipeline.
    ; So the :private data in app-db is just now being populated with Firebase
    ; data by the :private handler with the value from effects, initially
    ; (get-in db [:my-identity :private]). So, we now increment the login count
    ; from Firebase and store it both there AND in app-db. Note that we don't
    ; really need to store it in app-db now, since there will be a second
    ; [:private {:login-count ...}] event arriving from the change on Firebase
    ; caused by the :firebase-reset-ref effect below. But we go ahead
    ; and update app-db now in order to avoid the brief moment when the
    ; :login-count value in app-db is stale prior to the arrival of that second
    ; event.
    (let [new-count       (inc (or remote-count 0))
          login-count-ref (path-str->my-db-ref db "login-count")]
      (-> effects
        ; Update remote "login-count" via an effect defined in owlet.firebase.
        ; (Just log any error. What else can we do about it?)
        (assoc :firebase-reset-ref [login-count-ref new-count])
        ; Update :login-count in (local) app-db, correct immediately.
        (assoc-in [:db :my-identity :private :login-count] new-count)
        ; Update status to indicate we got the data, preventing infinite loop!
        (assoc-in [:db :my-identity :auth-status] :updated-login-count)))

    ; Else didn't just log in, but indicate that we've obtained :private data.
    (assoc-in effects [:db :my-identity :auth-status] :obtained-private-data)))

; Used here:
(pipeline/pipe-fx-transform!
  :private                             ; Handler registered in owlet.auth.
  ::inc-login-count                    ; Label for this transform function.
  inc-login-count)                     ; Maybe do app-db & Firebase updates.


(pipeline/pipe-setter-transform!
  :private                             ; Handler registered in owlet.auth.
  ::roles->set                         ; Label for this transform func.
  #(update % :roles (comp set keys)))  ; Transform fn converts roles to a set.


(defn toggle-user-role
  [{{{{roles :roles} :private} :my-identity :as db} :db} [_ role-kw]]
  (let [assign? (not (role-kw roles))]
    {:firebase-reset-ref
     [(path-str->my-db-ref db (str "roles/" (name role-kw)))
      (or assign? nil)
      :check-error "Unable to save role assignment."]}))
; Used here:
(rf/reg-event-fx :toggle-user-role toggle-user-role)


(defn check-error
  [db [_ {err :error-reason} msg]]
  (if err
    (assoc db :on-app-failure {:show? true :msg msg})
    db))
; Used here:
(rf/reg-event-db :check-error check-error)

