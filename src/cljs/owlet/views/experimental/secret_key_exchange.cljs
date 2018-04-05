(ns owlet.views.experimental.secret-key-exchange
  (:require [reagent.core :as reagent]))

(def prime (reagent/atom 0))

(def base (reagent/atom 0))

(defn input-number [title value]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:input.form-control
     {:type "number"
      :placeholder 0
      :min 0
      :max 99
      :value @value
      :on-change #(reset! value (js/parseInt (-> % .-target .-value)))}]
    [:span.input-group-text title]]])


(defn prime-base-wrapper []
  [:div.prime-base-wrapper.flex-container
   [input-number "PRIME #" prime]
   [input-number "BASE #" base]])

(defn activity []
  [:div.secret-key-exchange-experimental-view
   [prime-base-wrapper]])