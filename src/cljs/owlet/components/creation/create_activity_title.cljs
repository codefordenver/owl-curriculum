(ns owlet.components.creation.create-activity-title
  (:require [owlet.components.back :refer [back]]
            [re-com.core :refer [h-box]]))

(defn create-activity-title []
  [:div.activity-title-wrap
   [h-box
    :size "1"
    :align :center
    :style {:background-color "black"
            :font-size "2.4rem"
            :padding ".1em .3em"};
    :children [[back]
               [:div {:style {:flex-grow "1"}}
                [:input {:type "text"
                         :name "title"
                         :placeholder "Activity Title"}]]]]
   [:h5.author [:p "Created by: "
                [:input {:type "text"
                         :name "author"
                         :placeholder "Author"}]]]])
