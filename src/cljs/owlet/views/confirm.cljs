(ns owlet.views.confirm
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [ajax.core :refer [GET]]))

(defn confirm-view []
  (defn data_callback []
    (let [id @(rf/subscribe [:subscriber-info])]
      (set! (.-location js/document) (str "http://owlet.codefordenver.org/api/contentful/webhook/content/confirm?id=" id))))
  (defn captcha [handler]
    (let [callback-hooks (let [s (.createElement js/document "script")]
                           (.setAttribute s "id" "captcha-callbacks")
                           (set! (.-text s)
                                 (str "var captcha_data_callback = function(x) { owlet.views.confirm.data_callback(x)};"))
                           s)
          grecaptcha-script (doto (.createElement js/document "script")
                              (.setAttribute "id" "grecaptcha-script")
                              (.setAttribute "src" "https://www.google.com/recaptcha/api.js"))
          captcha-div [:div#confirm
                       [:span#confirm-message "Please check the box below to confirm your subscription"]
                       [:div.g-recaptcha
                         {:data-sitekey "6LdKwjAUAAAAAOXp-DRhuRXN77yKgZ9vDTR5gcxl"
                          :data-callback "captcha_data_callback"}]]]

      (reagent/create-class
       {:component-did-mount (fn [this]
                               (doto (.-body js/document)
                                 (.appendChild callback-hooks)
                                 (.appendChild grecaptcha-script)))
        :component-will-unmount (fn [this]
                                  (doto (.-body js/document)
                                    (.removeChild (.getElementById js/document "captcha-callbacks"))
                                    (.removeChild (.getElementById js/document "grecaptcha-script"))))
        :reagent-render (fn [this] captcha-div)}))))
