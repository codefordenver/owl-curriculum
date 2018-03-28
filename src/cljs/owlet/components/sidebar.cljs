(ns owlet.components.sidebar
  (:require [owlet.components.login :refer [login-component]]
            [re-frame.core :as rf]))

(defn sidebar-component []
  [:div#sidebar.navbar
   [:div.owlet-logo-div
    [:a.owlet-image {:href "#"}
      [:img.owlet-owl {:src "../img/owlet-owl.png"
                       :alt "Owlet home"}]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a.navigation.branch-icon {:href "#/branches"}
     "Branches"]
    (when @(rf/subscribe [:my-id])
      [:a.navigation.settings-icon {:href "#/settings"}
       "Settings"])
    [:a.navigation.about-icon {:href "#/about"}
     "About"]]])
