(ns owlet.views.experimental.secret-key-exchange
  (:require [reagent.core :as reagent]
            [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]))

(def prime (reagent/atom nil))

(def base (reagent/atom nil))

(def alice-secret (reagent/atom nil))

(def bob-secret (reagent/atom nil))

(defn exp-mod [base exponent modulus]
  (-> (js/bigInt base)
      (.pow exponent)
      (.mod modulus)))

(defn input-number [title value placeholder]
  (let [prime-showing? (reagent/atom false)]
    [:div.input-group.mb-3
     [:div.input-group-prepend
      [:input.form-control
       {:type "number"
        :placeholder placeholder
        :min 1
        :max 10000
        :value @value
        :on-change (fn [e]
                     (if (= "" (-> e .-target .-value))
                       (reset! value nil)
                       (reset! value (js/parseInt (-> e .-target .-value)))))}]
      (if (= value prime)
        [re-com/popover-anchor-wrapper
             :showing? prime-showing?
             :position :below-center
             :anchor [:span.input-group-text.pulse
                      {:on-mouse-over (handler-fn (reset! prime-showing? true))
                       :on-mouse-out  (handler-fn (reset! prime-showing? false))}
                      title]
             :popover [re-com/popover-content-wrapper
                       :close-button? false
                       :body [:div {:style {:text-align "center"
                                            :font-size "1.2em"}}
                              "a \"prime\" number is only divisible by itself and 1"]]]
        [:span.input-group-text title])]]))

(defn known-number [title & [value]]
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:div.form-control
     (when value (str value))]
    [:span.input-group-text title]]])

(defn activity []
  (let [mod-showing-1? (reagent/atom false)
        mod-showing-2? (reagent/atom false)
        mod-showing-3? (reagent/atom false)
        mod-showing-4? (reagent/atom false)]
    [:div.secret-key-exchange-experimental-view
     [:div.col-xs-12.too-small
      [:h1.secret "To view the interactive demo, rotate your phone or come back on a desktop."]]
     [:div.col-xs-12.col-lg-6
      [:p "When you submit a password or credit card number online, your computer has to send that data to another computer (server) over the internet. Any data sent over the internet can be intercepted, so when dealing with sensitive data, computers are instructed to scramble (encrypt) the data before sending it out. Otherwise, it's vulnerable to a "
       [:a {:href "https://us.norton.com/internetsecurity-wifi-what-is-a-man-in-the-middle-attack.html"
            :target "_blank"}
        "man-in-the-middle (MITM) attack"]
       "."]
      [:p "To scramble the data, "
       [:span "Computer A"]
       " combines it with a random string of bits, called an \"encryption key.\" Once the data is received, "
       [:span "Computer B"]
       " unscrambles (decrypts) the data using the same encryption key."]
      [:p [:span "How do they share that key??"]]
      [:p "Remember, "
       [:span "Computer A"]
       " can't just send the key over, or anyone would be able to use it to decrypt your credit card number."]]
     [:div.col-xs-12.col-lg-6
      [:p "Instead, "
       [:span "Computer A"]
       " sends "
       [:span "Computer B"]
       " two numbers: a "
       [:span.prime "PRIME"]
       " number, and a "
       [:span.base "BASE"]
       " number. It doesn't matter if these are intercepted, because they're useless on their own."]
      [:p "Then each computer chooses its own "
       [:span.secret "SECRET"]
       " number (which is never shared) and uses it to perform a series of calculations with the "
       [:span.prime "PRIME"]
       " and "
       [:span.base "BASE"]
       " numbers to generate new "
       [:span.mixed-1 "PUBLIC"]
       " numbers, which they exchange out in the open."]
      [:p "After a few more calculations, they will end up with the "
       [:span.mixed-2 "SAME number (the encryption key)"]
       " without ever actually sharing it!"]
      [:p "Today, there are various key exchange methods that rely on different kinds of math. "
       [:span "This interactive demo is based on the Diffie-Hellman key exchange (DH), one of the earliest practical examples first introduced in 1976.*"]]]
     [:div.col-xs-12.directions
      [:h2.mixed-2 "Fill in the white boxes to create a shared secret key"]
      [:h4 "Use the suggested numbers or try your own."
       [:br] "Hover your mouse over "
       [:span.pulse-shrink "\"pulsing\""]
       " areas to learn more!"]]
     [:div#diffie-hellman-grid
      [:div.divider-1]
      [:div.divider-2]
      [:div.hotspot-2]
      [:div.lines
       [:img {:src "img/experimental/lines.png"}]]
      [:div.flex.pa.prime
       [input-number "PRIME #" prime 29]]
      [:div.flex.pa.base
       [input-number "BASE #" base 8]]
      [:div.flex.pa.alice-name
       [:h1 "ALICE"]]
      [:div.flex.pa.alice-secret-1
       [input-number "ALICE SECRET" alice-secret 47]]
      [:div.flex.pa.alice-secret-msg
       [:div [:span "<"]
        " Enter a secret number for Alice. For subtle reasons, it should not have any common factors with the "
        [:span.base "BASE #"]]]
      [:div.flex.pa.alice-base
       [known-number "BASE #" @base]]
      [:div.flex.alice-secret-2
       [:div.exp
        [known-number "" @alice-secret]]
       [:div.mod
        [re-com/popover-anchor-wrapper
           :showing? mod-showing-1?
           :position :below-center
           :anchor [:span.pulse
                    {:on-mouse-over (handler-fn (reset! mod-showing-1? true))
                     :on-mouse-out  (handler-fn (reset! mod-showing-1? false))}
                    "%"]
           :popover [re-com/popover-content-wrapper
                     :close-button? false
                     :body [:div {:style {:text-align "center"}}
                            [:h2 "\"modulo\" operator"]
                            "returns the remainder after dividing two numbers"]]]]]
      [:div.flex.pa.alice-prime-1
       [known-number "PRIME #" @prime]]
      [:div.flex.alice-equal-1.equal-sign
       [:div "="]]
      [:div.flex.pa.alice-number.mixed-1
       [known-number "ALICE PUBLIC" (when (and @prime @base @alice-secret)
                                      (exp-mod @base @alice-secret @prime))]]
      [:div.flex.pa.alice-bob-number.mixed-1
       [known-number "BOB PUBLIC" (when (and @prime @base @bob-secret)
                                    (exp-mod @base @bob-secret @prime))]]
      [:div.flex.alice-secret-3
       [:div.exp
        [known-number "" @alice-secret]]
       [:div.mod
        [re-com/popover-anchor-wrapper
           :showing? mod-showing-2?
           :position :below-center
           :anchor [:span.pulse
                    {:on-mouse-over (handler-fn (reset! mod-showing-2? true))
                     :on-mouse-out  (handler-fn (reset! mod-showing-2? false))}
                    "%"]
           :popover [re-com/popover-content-wrapper
                     :close-button? false
                     :body [:div {:style {:text-align "center"}}
                            [:h2 "\"modulo\" operator"]
                            "returns the remainder after dividing two numbers"]]]]]
      [:div.flex.pa.alice-prime-2
       [known-number "PRIME #" @prime]]
      [:div.flex.alice-equal-2.equal-sign
       [:div "="]]
      [:div.flex.pa.alice-shared-secret.mixed-2
       [known-number "SECRET KEY" (when (and @prime @base @bob-secret @alice-secret)
                                       (exp-mod (exp-mod @base @bob-secret @prime)
                                                @alice-secret
                                                @prime))]]
      [:div.flex.pa.bob-name
       [:h1 "BOB"]]
      [:div.flex.pa.bob-secret-1
       [input-number "BOB SECRET" bob-secret 91]]
      [:div.flex.pa.bob-secret-msg
       [:div [:span "<"]
        " Enter a secret number for Bob. For subtle reasons, it should not have any common factors with the "
        [:span.base "BASE #"]]]
      [:div.flex.pa.bob-base
       [known-number "BASE #" @base]]
      [:div.flex.bob-secret-2
       [:div.exp
        [known-number "" @bob-secret]]
       [:div.mod
        [re-com/popover-anchor-wrapper
           :showing? mod-showing-3?
           :position :below-left
           :anchor [:span.pulse
                    {:on-mouse-over (handler-fn (reset! mod-showing-3? true))
                     :on-mouse-out  (handler-fn (reset! mod-showing-3? false))}
                    "%"]
           :popover [re-com/popover-content-wrapper
                     :close-button? false
                     :body [:div {:style {:text-align "center"}}
                            [:h2 "\"modulo\" operator"]
                            "returns the remainder after dividing two numbers"]]]]]
      [:div.flex.pa.bob-prime-1
       [known-number "PRIME #" @prime]]
      [:div.flex.bob-equal-1.equal-sign
       [:div "="]]
      [:div.flex.pa.bob-number.mixed-1
       [known-number "BOB PUBLIC" (when (and @prime @base @bob-secret)
                                    (exp-mod @base @bob-secret @prime))]]
      [:div.flex.pa.bob-alice-number.mixed-1
       [known-number "ALICE PUBLIC" (when (and @prime @base @alice-secret)
                                      (exp-mod @base @alice-secret @prime))]]
      [:div.flex.bob-secret-3
       [:div.exp
        [known-number "" @bob-secret]]
       [:div.mod
        [re-com/popover-anchor-wrapper
           :showing? mod-showing-4?
           :position :below-left
           :anchor [:span.pulse
                    {:on-mouse-over (handler-fn (reset! mod-showing-4? true))
                     :on-mouse-out  (handler-fn (reset! mod-showing-4? false))}
                    "%"]
           :popover [re-com/popover-content-wrapper
                     :close-button? false
                     :body [:div {:style {:text-align "center"}}
                            [:h2 "\"modulo\" operator"]
                            "returns the remainder after dividing two numbers"]]]]]
      [:div.flex.pa.bob-prime-2
       [known-number "PRIME #" @prime]]
      [:div.flex.bob-equal-2.equal-sign
       [:div "="]]
      [:div.flex.pa.bob-shared-secret.mixed-2
       [known-number "SECRET KEY" (when (and @prime @base @alice-secret @bob-secret)
                                       (exp-mod (exp-mod @base @alice-secret @prime)
                                                @bob-secret
                                                @prime))]]]
     [:div.col-xs-12
      [:p "*In 2015, computer scientists discovered the "
       [:a {:href "https://weakdh.org/"
            :target "_blank"}
           "Logjam attack"]
       ", a security vulnerability against Diffie–Hellman key exchanges that use 512-bit to 1024-bit keys. This can be avoided by using prime numbers that are 2048-bit or larger."]]]))
