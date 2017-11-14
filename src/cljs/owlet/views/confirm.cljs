(ns owlet.views.confirm
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.spec :as s]))

(defn data-callback []
    (let [id @(rf/subscribe [:subscriber-info])]
      (set! (.-location js/document) (str "http://owlet.codefordenver.org/api/contentful/webhook/content/confirm?id=" id))))

(s/fdef re-captcha
        :args (s/cat :dom-id string?
                     :callback ifn?))

(defn re-captcha [dom-id callback]
  (when (.getElementById js/document dom-id)
    (.render js/grecaptcha dom-id
             #js {:sitekey "6LdKwjAUAAAAAOXp-DRhuRXN77yKgZ9vDTR5gcxl"
                  :callback callback})))

(defn confirm-view []
  (r/create-class
       {:component-did-mount
        #(re-captcha "g-recaptcha" data-callback)
        :reagent-render
         (fn []
          [:div#confirm
           [:span#confirm-message "Please check the box below to confirm your subscription"]
           [:div#g-recaptcha]])}))
