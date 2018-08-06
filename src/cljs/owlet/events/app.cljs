(ns owlet.events.app
  (:require [clojure.string :as clj-str]
            [re-frame.core :as rf]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet.config :as config]
            [re-futil.getter-setter-pipeline :refer [reg-setter]]
            [owlet.db :as db]
            [owlet.helpers :refer [keywordize-name]]))


(rf/reg-event-db
  :initialize-db
  (constantly db/default-db))


(reg-setter :set-loading-state! [:app :loading?])


(rf/reg-event-db
  :set-active-view
  (fn [db [_ active-view opts]]
    (let [sub-info (:sub-info opts)
          search (aget (js/document.getElementsByClassName "form-control") 0)]
      (when-not (nil? search)
        (set! (.-value search) "")
        (.blur search))
      (-> db
          (assoc :active-view active-view)
          (assoc-in [:app :route-opts] sub-info)))))


(rf/reg-event-db
  :set-active-document-title!
  (fn [db [_ val skip-caps]]
    (let [document-title (if skip-caps val (clj-str/capitalize (or val "")))
          title (str document-title " | " config/project-name)]
      (assoc-in db [:app :title] title))))


(reg-setter :show-bg-img-upload [:showing-bg-img-upload])


(rf/reg-event-fx
  :update-user-background!
  (fn [{{{:keys [private private-ref]} :my-identity} :db}
       [_ {:keys [url filename]}]]
    ; Called after the upload of a background image file was successful, we
    ; update the user's data in the Firebase database, which will automatically
    ; cause app-db to be similarly updated, thanks to the effect
    ; :start-authorized-listening and the handler :private.
    (let [old-url        (:background-image-url  private)
          old-name       (:background-image-name private)
          old-image-info {:background-image-url  old-url
                          :background-image-name old-name}
          new-image-info {:background-image-url  url
                          :background-image-name filename}]

      {:firebase-reset-into-ref
       [private-ref new-image-info            ; Where and what to save.
        :user-background-saved new-image-info old-image-info]
                                              ; Event to dispatch when complete.
       :dispatch
       [:show-bg-img-upload false]})))

(rf/reg-event-fx
  :user-background-saved
  (fn [_ [_ {err :error-reason} new-image-info old-image-info]]
    ; Called after an attempt to update the Firebase db with the URL and
    ; filename of a new background image for the user, we delete the old image
    ; file if the update was successful, or we delete the new image file if
    ; there was an error.

    (letfn [(same-image-name-in [& infos]
              ; Return true iff all the infos maps refer to the same filename.
              (apply = (map :background-image-name infos)))]

      ; Delete the old uploaded file only if Firebase still has it. The old
      ; file will already have been replaced if it had the same filename as the
      ; new file we just uploaded. In this case, proceeding would just result
      ; in deleting the new file we just uploaded! Also, if we should indeed
      ; delete, and if there was an error attempting to write the new URL to
      ; the Firebase DATABASE (at users/.../background-image-url), then we do
      ; delete the file we just uploaded, because it would be inaccessible by
      ; the app.
      (if-let [url-to-delete (:background-image-url (if err
                                                       new-image-info
                                                       old-image-info))]
        (when (not (same-image-name-in new-image-info old-image-info))
          {:delete-file-at-url url-to-delete})))))

      ; Do no effects if the filename didn't change: return nil.


(rf/reg-event-db
  :set-activity-in-view
  (fn [db [_ activity-id]]
    (when-let [activity-match (some #(when (= (get-in % [:sys :id]) activity-id) %)
                                (:activities db))]
      (assoc db :activity-in-view activity-match))))
