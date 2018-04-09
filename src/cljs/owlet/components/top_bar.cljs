(ns owlet.components.top-bar
  (:require [owlet.components.login :refer [login-component]]
            [owlet.components.search-bar :refer [search-bar]]
            [re-frame.core :as rf]))

(defn top-bar []
 [:div
  [:div.flex-row.box-shadow.top-bar
   [:a {:href "#"}
    [:div.owlet-logo-div.flex-row
     [:img.owlet-owl {:src "../img/owlet-owl.png"
                      :alt "Owlet home"}]
     [:div.owlet-text
       [:h1 "OWLET"]
       [:p "TECHNOLOGY EDUCATION MADE SIMPLER"]]]]
   [:div.search-container
    [search-bar]]
   [:div.navigation
    [:a {:href "#/about"}
     [:button "ABOUT US"]]
    [:a {:href "#/branches"}
     [:button "EXPLORE"]]
    (when @(rf/subscribe [:my-id])
     [:a {:href "#/settings"}
      [:button [:i.fa.fa-gear]]])
    [login-component]]]])
