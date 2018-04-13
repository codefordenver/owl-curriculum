(ns owlet.components.back
  (:require [re-frame.core :as rf]))

(defn back []
  [:a {:href "#/branches"
       :on-click #(rf/dispatch [:show-branches])}
    [:i.fa.fa-caret-left.back {:aria-hidden "true"}]])
