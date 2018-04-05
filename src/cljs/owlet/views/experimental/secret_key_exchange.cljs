(ns owlet.views.experimental.secret-key-exchange
  (:require [reagent.core :as reagent]))

(def prime (reagent/atom 0))

(def base (reagent/atom 0))

(def secret1 (reagent/atom 0))

(defn input-number [title value]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:input.form-control
     {:type "number"
      :placeholder 0
      :min 0
      :value @value
      :on-change #(reset! value (js/parseInt (-> % .-target .-value)))}]
    [:span.input-group-text title]]])

(defn known-number [title value]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:div.form-control
     value]
    [:span.input-group-text title]]])

(defn activity []
  [:div.secret-key-exchange-experimental-view
   [:div.col-xs-12.col-lg-6
    [:p "When you submit a password or credit card number online, that information gets sent from your computer to another computer (server) over the internet. However, any information traveling over the internet can be intercepted by someone else, in what is called a "
     [:a {:href "https://us.norton.com/internetsecurity-wifi-what-is-a-man-in-the-middle-attack.html"
          :target "_blank"}
      "man-in-the-middle (MITM) attack"]
     "."]
    [:p "So when dealing with sensitive information, computers are instructed to scramble (encrypt) the data before sending it out. Once it's received by an authorized server, that server unscrambles (decrypts) the data and uses it however its supposed to."]
    [:p "To scramble the data, Computer A combines it with a random string of bits, called an \"encryption key.\" Computer B needs that encryption key in order to unscramble the data."]]
   [:div.col-xs-12.col-lg-6
    [:p [:i "But how do you share the key without exposing it??"]]
    [:p "Instead of sending the key, Computer A sends over two numbers: a "
     [:span.public "PRIME number"]
     ", and a "
     [:span.public "BASE number"]
     ". It doesn't matter if these are intercepted, because they're useless on their own."]]
   [:div#diffie-hellman-grid
    [:div.prime-wrapper
     [input-number "PRIME #" prime]]
    [:div.base-wrapper
     [input-number "BASE #" base]]
    [:h1.alice-name "Alice"]
    [:div.alice-secret-wrapper-1
     [input-number "SECRET #" secret1]]
    [:div.alice-base-wrapper
     [known-number "BASE #" @base]]
    [:div.alice-secret-wrapper-2.flex-container
     [known-number "" @secret1]
     [:div "%"]]
    [:div.alice-prime-1
     [known-number "PRIME #" @prime]]
    [:div.alice-equal-1
     "="]
    [:div.alice-number
     [known-number "ALICE'S #" (mod (js/Math.pow @base @secret1) @prime)]]]
   [:div
    [:p "Each computer takes the publicly shared "
     [:span.public "BASE number"]
     ", applies its own secret exponent (which is never shared), and then mods the result by the "
     [:span.public "PRIME number"]
     " to produce a brand new number. Then, those numbers are publicly exchanged."]
    [:p "This is where the magic happens. Computer A takes Computer B's number, applies its own secret exponent from before, and then mods it by the "
     [:span.public "PRIME number"]
     ". Computer B does the same, using Computer A's number and its own secret exponent."]
    [:p "The final number will be the same for both; this is their encryption key!"]]])
