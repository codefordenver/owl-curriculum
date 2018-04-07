(ns owlet.views.experimental.secret-key-exchange
  (:require [reagent.core :as reagent]))

(def prime (reagent/atom 1))

(def base (reagent/atom 1))

(def alice-secret (reagent/atom 1))

(def bob-secret (reagent/atom 1))

(defn exp-mod [base exponent modulus]
  (-> (js/bigInt base)
      (.pow exponent)
      (.mod modulus)))

(defn input-number [title value]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:input.form-control
     {:type "number"
      :placeholder 0
      :min 0
      :max 10000
      :value @value
      :on-change #(reset! value (js/parseInt (-> % .-target .-value)))}]
    [:span.input-group-text title]]])

(defn known-number [title value]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:div.form-control
     (str value)]
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
     [:span.prime "PRIME number"]
     ", and a "
     [:span.base "BASE number"]
     ". It doesn't matter if these are intercepted, because they're useless on their own."]]
   [:div#diffie-hellman-grid
    [:div.divider-1]
    [:div.divider-2]
    [:div.hotspot-2]
    [:div.lines
     [:img {:src "img/experimental/lines.png"}]]
    [:div.flex.pa.prime
     [input-number "PRIME" prime]]
    [:div.flex.pa.base
     [input-number "BASE" base]]
    [:div.flex.pa.alice-name
     [:h1 "ALICE"]]
    [:div.flex.pa.alice-secret-1
     [input-number "ALICE SECRET" alice-secret]]
    [:div.flex.pa.alice-secret-msg
     [:div [:span "<"]
      " Enter a secret number for Alice. For subtle reasons, it should not have any common factors with the "
      [:span "BASE"]
      " number"]]
    [:div.flex.pa.alice-base
     [known-number "BASE" @base]]
    [:div.flex.alice-secret-2
     [:div.exp
      [known-number "" @alice-secret]]
     [:div.mod "%"]]
    [:div.flex.pa.alice-prime-1
     [known-number "PRIME" @prime]]
    [:div.flex.alice-equal-1.equal-sign
     [:div "="]]
    [:div.flex.pa.alice-number.mixed-1
     [known-number "ALICE PUBLIC" (exp-mod @base @alice-secret @prime)]]
    [:div.flex.pa.alice-bob-number.mixed-1
     [known-number "BOB PUBLIC" (exp-mod @base @bob-secret @prime)]]
    [:div.flex.alice-secret-3
     [:div.exp
      [known-number "" @alice-secret]]
     [:div.mod "%"]]
    [:div.flex.pa.alice-prime-2
     [known-number "PRIME" @prime]]
    [:div.flex.alice-equal-2.equal-sign
     [:div "="]]
    [:div.flex.pa.alice-shared-secret.mixed-2
     [known-number "SHARED SECRET" (exp-mod (exp-mod @base @bob-secret @prime)
                                            @alice-secret
                                            @prime)]]
    [:div.flex.pa.bob-name
     [:h1 "BOB"]]
    [:div.flex.pa.bob-secret-1
     [input-number "BOB SECRET" bob-secret]]
    [:div.flex.pa.bob-secret-msg
     [:div [:span "<"]
      " Enter a secret number for Bob. For subtle reasons, it should not have any common factors with the "
      [:span "BASE"]
      " number"]]
    [:div.flex.pa.bob-base
     [known-number "BASE" @base]]
    [:div.flex.bob-secret-2
     [:div.exp
      [known-number "" @bob-secret]]
     [:div.mod "%"]]
    [:div.flex.pa.bob-prime-1
     [known-number "PRIME" @prime]]
    [:div.flex.bob-equal-1.equal-sign
     [:div "="]]
    [:div.flex.pa.bob-number.mixed-1
     [known-number "BOB PUBLIC" (exp-mod @base @bob-secret @prime)]]
    [:div.flex.pa.bob-alice-number.mixed-1
     [known-number "ALICE PUBLIC" (exp-mod @base @alice-secret @prime)]]
    [:div.flex.bob-secret-3
     [:div.exp
      [known-number "" @bob-secret]]
     [:div.mod "%"]]
    [:div.flex.pa.bob-prime-2
     [known-number "PRIME" @prime]]
    [:div.flex.bob-equal-2.equal-sign
     [:div "="]]
    [:div.flex.pa.bob-shared-secret.mixed-2
     [known-number "SHARED SECRET" (exp-mod (exp-mod @base @alice-secret @prime)
                                            @bob-secret
                                            @prime)]]]
   [:div
    [:p "Each computer takes the publicly shared "
     [:span.base "BASE number"]
     ", applies its own secret exponent (which is never shared), and then mods the result by the "
     [:span.prime "PRIME number"]
     " to produce a brand new number. Then, those numbers are publicly exchanged."]
    [:p "This is where the magic happens. Computer A takes Computer B's number, applies its own secret exponent from before, and then mods it by the "
     [:span.prime "PRIME number"]
     ". Computer B does the same, using Computer A's number and its own secret exponent."]
    [:p "The final number will be the same for both; this is their encryption key!"]]])
