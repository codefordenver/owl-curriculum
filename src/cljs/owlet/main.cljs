(ns owlet.main
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [owlet.components.upload-image-modal :refer [upload-image-component]]
            [owlet.components.sidebar :refer [sidebar-component]]
            [owlet.components.lpsidebar :refer [lpsidebar-component]]
            [owlet.components.loading :refer [loading-component]]
            [owlet.components.error :refer [error]]
            [owlet.views.welcome :refer [welcome-view]]
            [owlet.views.about :refer [about-view]]
            [owlet.views.klipse-activity :refer [klipse-activity-view]]
            [owlet.views.temp-activities.print-errors :refer [temp-print-errors-view]]
            [owlet.views.create-klipse-panel-activity :refer [create-klipse-panel-activity-view]]
            [owlet.views.create-klipse-slides-activity :refer [create-klipse-slides-activity-view]]
            [owlet.views.create-general-activity :refer [create-general-activity-view]]
            [owlet.components.top-bar :refer [top-bar]]
            [owlet.views.not-found :refer [not-found-view]]
            [owlet.views.activity :refer [activity-view]]
            [owlet.views.branches :refer [branches-view]]
            [owlet.views.confirm :refer [confirm-view]]
            [owlet.views.subscribed :refer [subscribed-view]]
            [owlet.views.unsubscribe :refer [unsubscribe-view]]
            [owlet.views.settings :refer [settings-view]]
            [owlet.views.filtered-activities :refer [filtered-activities-view]]
            [owlet.async :as async]
            [owlet.auth0 :as auth0]
            [owlet.config :as config]
            [owlet.firebase :as fb]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :about-view [] [about-view])
(defmethod views :klipse-activity-view [] [klipse-activity-view])
(defmethod views :temp-print-errors-view [] [temp-print-errors-view])
(defmethod views :filtered-activities-view [] [filtered-activities-view])
(defmethod views :create-klipse-panel-activity-view [] [create-klipse-panel-activity-view])
(defmethod views :create-klipse-slides-activity-view [] [create-klipse-slides-activity-view])
(defmethod views :create-general-activity-view [] [create-general-activity-view])
(defmethod views :not-found-view [] [not-found-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :branches-view [] [branches-view])
(defmethod views :confirm-view [] [confirm-view])
(defmethod views :subscribed-view [] [subscribed-view])
(defmethod views :unsubscribe-view [] [unsubscribe-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view [view-name]
  [views view-name])

(defn view []

  (auth0/on-authenticated auth0/lock
                          config/auth0-del-opts-for-firebase
                          :auth0-authenticated
                          :auth0-error)
  (fb/on-auth-change fb/firebase-auth-object :firebase-auth-change)

  (let [active-view (rf/subscribe [:active-view])
        loading? (rf/subscribe [:loading-state])
        src (rf/subscribe [:my-background-image-url])
        is-user-logged-in? (rf/subscribe [:my-id])]
    (fn []
      (set! (-> js/document .-title) @(rf/subscribe [:app-title]))

      (if (= @active-view :welcome-view)

        [:div
         [error]
         [show-view @active-view]]

        [:div#main
         [error]
         [:div.outer-height-wrap
          [top-bar]
          [:div.inner-height-wrap
             [:div.content
              ;TODO: repurpose custom bg functionality for custom header
                           ; (when @src
                           ;   {:style {:background-image (when-not (or (= @active-view :about-view)
                           ;                                            (= @active-view :confirm-view))
                           ;                                (str "url('" @src "')"))
                           ;            :background-size "cover"}})
                ; (when-not (or (= @active-view :about-view)
                ;               (= @active-view :confirm-view))
                ;   [:button#change-bg-btn
                ;    {:type     "button"
                ;     :class    "btn btn-secondary"
                ;     :style    {:display (if @is-user-logged-in?
                ;                             "block"
                ;                             "none")}
                ;     :on-click #(rf/dispatch [:show-bg-img-upload true])}
                ;    [:i.fa.fa-pencil]])
                ; [upload-image-component]
                (when @loading?
                  [loading-component])
                [show-view @active-view]]]]]))))
