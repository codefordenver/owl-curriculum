(ns owlet.views.confirm
  (:require [re-frame.core :as rf]))

(defn ^:export data_callback []
    (let [id @(rf/subscribe [:subscriber-info])]
      (set! (.-location js/document) (str "http://owlet.codefordenver.org/api/contentful/webhook/content/confirm?id=" id))))

(defn confirm-view []
  [:div#confirm
   [:span#confirm-message "Please check the box below to confirm your subscription"]
   [:div.g-recaptcha
     {:data-sitekey "6LdKwjAUAAAAAOXp-DRhuRXN77yKgZ9vDTR5gcxl"
      :data-callback "recaptchaCallback"}]])
