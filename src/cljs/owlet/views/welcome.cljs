(ns owlet.views.welcome
  (:require
    [owlet.components.login :refer [login-component]]
    [re-frame.core :as rf]))

(defn welcome-view []
  [:div.flexcontainer
    [:div.user-type
      [:p#largetext.text-shadow "Welcome to Owlet"]
      [:div.welcome-text.text-shadow
        [:p "Explore some of the awesome things you can do with multimedia & coding!"]
        [:p "¡Bienvenidx a Owlet! Explora lo que puedes hacer con programación y con multimedios."]]
      [:a {:href "#/branches"}
        [:button.btn.btn-branches "Go to Activities"]]]
    [:div.login-landing
      [login-component]]])
