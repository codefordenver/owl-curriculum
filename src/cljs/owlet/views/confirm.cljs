(ns owlet.views.confirm
  (:require [re-frame.core :as rf]))

(defn confirm-view []
  (let [id @(rf/subscribe [:subscriber-info])]
    [:div#confirm
     [:span#confirm-message "Please check the box below to confirm your subscription"]
     [:div.g-recaptcha
       {:data-sitekey "6LdKwjAUAAAAAOXp-DRhuRXN77yKgZ9vDTR5gcxl"
        :data-callback #(js/recaptchaCallback id)}]]))
