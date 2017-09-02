(ns owlet.components.sidebar
  (:require [owlet.components.login :refer [login-component]]
            [re-frame.core :as rf]
            [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [reagent.core :as reagent]))

(defn sidebar-component []
  (let [showing-branch? (reagent/atom false)
        showing-about? (reagent/atom false)]
    [:div#sidebar
     [:div#owlet-logo-div
      [:a#owlet-image {:href "#"}
        [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
     [:div.menu
      [:div.login
       [login-component]]
      [:a.navigation.branch-icon {:href "#/branches"}
       [:div.branch-icon]]
      [:a.navigation.branch-icon {:href "#/about"}
       [:div.about-icon]]]]))
