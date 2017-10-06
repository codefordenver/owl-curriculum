(ns owlet.components.activity.challenge
  (:require [owlet.helpers :refer [showdown]]))

(defn activity-challenge [challenge]
  [:div.activity-challenge-wrap.box-shadow
    [:div.list-title
     [:p [:span.h3 "⚡⚡ challenge⚡⚡ "]]]
    [:div {"dangerouslySetInnerHTML"
           #js{:__html (.makeHtml showdown challenge)}}]])
