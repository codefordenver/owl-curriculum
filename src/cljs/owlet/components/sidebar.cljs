(ns owlet.components.sidebar
  (:require [owlet.components.login :refer [login-component]]))

(defn sidebar-component []
  [:div#sidebar
   [:div#owlet-logo-div
    [:a#owlet-image {:href "#"}
      [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a.navigation.branch-icon {:href "#/branches"}
     [:div.branch-icon]]
    [:a.navigation.settings-icon {:href "#/settings"}
     [:div.settings-icon]]
    [:a.navigation.about-icon {:href "#/about"}
     [:div.about-icon]]]])
