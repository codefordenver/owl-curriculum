(ns owlet.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [owlet.helpers :refer [keywordize-name]]))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
            (rf/dispatch [:set-active-view :welcome-view])
            (rf/dispatch [:get-content-from-contentful]))

  (defroute "/404" []
            (rf/dispatch [:get-content-from-contentful :show-not-found]))

  (defroute "/about" []
            (rf/dispatch [:get-content-from-contentful :show-about]))

  (defroute "/settings" []
            (rf/dispatch [:get-content-from-contentful :show-settings]))

  (defroute "/confirm/:sub-info" {:as params} []
            (rf/dispatch [:get-content-from-contentful :show-confirm params]))

  (defroute "/subscribed/:sub-info" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-subscribed params]))

  (defroute "/unsubscribe" []
            (rf/dispatch [:get-content-from-contentful :show-unsubscribe]))

  (defroute "/branches" []
            (rf/dispatch [:get-content-from-contentful :show-branches]))

  (defroute "/tag/:tag" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-tag (:tag params)]))

  (defroute "/platform/:platform" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-platform (:platform params)]))

  (defroute "/branch/:branch" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-branch (:branch params)]))

  (defroute "/activity/#!:activity" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-activity (:activity params)]))

  (defroute "/klipse/#!:klipse" {:as params}
            (rf/dispatch [:get-content-from-contentful :show-klipse (:klipse params)]))

  (defroute "/create/:type" {:as params}
            (rf/dispatch [:get-content-from-contentful (keywordize-name (str "show-create-" (:type params)))]))

  (defroute "/activity/hello-world" []
            (rf/dispatch [:get-content-from-contentful :show-temp-hello-world]))

  (defroute "/activity/print-errors" []
            (rf/dispatch [:get-content-from-contentful :show-temp-print-errors]))

  (defroute "*" []
            (let [uri (-> js/window .-location .-href)]
              (if (re-find #"%23" uri)
                (let [new-uri (js/decodeURIComponent uri)]
                  (set! (-> js/window .-location) new-uri))
                (set! (.-location js/window) "/#/404"))))

  ; Ensure browser history uses Secretary to dispatch.
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
