(ns owlet.components.login
  (:require [re-frame.core :as rf]
            [owlet.auth0 :as auth0]
            [owlet.firebase :as fb]))

(defn signup-button
  []
  [:button.btn-signup
   {:type     "button"
    :on-click #(auth0/show-lock :initialScreen :signUp)}
   "Sign Up"])

(defn login-button
  []
  [:button.btn-login
   {:type     "button"
    :on-click #(auth0/show-lock :initialScreen :login)}
   "Log in"])


(defn logout-button
  []
  [:button.btn-logout
   {:type     "button"
    :on-click #(rf/dispatch [:log-out])}
   "Log out"])


(defn login-component
  []
  (if @(rf/subscribe [:my-id])
    [logout-button]
    [login-button]))
