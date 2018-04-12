(ns owlet.views.login-only
  (:require [owlet.components.login :refer [login-component]]
            [owlet.auth0 :as auth0]))

(defn login-only-view []
  [:div.information-wrap
   [:div.information-inner-wrap.col-xs-12.col-lg-8
    [:div.information-inner
     [:div.inner-height-wrap
      [:h1 [:mark {:style {:cursor "pointer"}
                   :on-click #(auth0/show-lock :initialScreen :login)}
            "Log in"]]
      [:h2 "You must be logged in to view this page."]]]]])
