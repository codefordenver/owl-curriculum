(ns owlet.views.unsubscribe
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet.config :as config]
            [owlet.constants :as constants]))

(defonce email-endpoint
         (str config/server-url
              "/webhook/content/unsubscribe"))

(def res (reagent/atom nil))

(def msg (reagent/atom false))

(add-watch res :watcher (fn [key atom old-state new-state]
                          (reset! msg false)
                          (js/setTimeout #(reset! msg true) 100)))

(defn unsubscribe-response [response]
  (cond
    (or
      (= constants/confirmation-sent response)
      (= constants/confirmation-resent response)) (reset! res 2)
    (= constants/not-subscribed response) (reset! res 1)
    :else (reset! res 0)))

(defn unsubscribe [email]
  (PUT email-endpoint {:params        {:email email}
                       :format        :json
                       :handler       unsubscribe-response
                       :error-handler #(reset! res 0)}))

(defn unsubscribe-view []
  (let [email (reagent/atom nil)]
    (fn []
      [:div.information-wrap
       [:div.information-inner-wrap.col-xs-12.col-xl-7
        [:div.information-inner
         [:h2 [:mark "Sorry to see you go!"]]
         [:h3 "Enter your email address to unsubscribe:"]
         [:div.email-unsubscribe
           [:input#email-input {:type        "text"
                                :placeholder "Email address"
                                :on-change   #(reset! email (-> % .-target .-value))
                                :value       @email}]
           [:button#unsubscribe-button {:on-click #(unsubscribe @email)}
            "Unsubscribe"]
           (when @msg
             (cond
               (= @res 2) [:p.refresh {:style {:color "green"}} "Almost there! Check your email to confirm."]
               (= @res 1) [:p.refresh {:style {:color "yellow"}} "You are not subscribed."]
               (= @res 0) [:p.refresh {:style {:color "red"}} "Unsuccessful. Please try again."]))]]]])))
