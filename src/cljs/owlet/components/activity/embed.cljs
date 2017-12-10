(ns owlet.components.activity.embed
  (:require [re-frame.core :as rf]))

(defn- generic-responsive-iframe
  "returns an responsive iframe"
  [iframe-code]
  (.replace iframe-code (js/RegExp. "/\\\"/g,'\\''")))

(defn activity-embed [embed tags preview]
  (let [preview-url (-> preview :sys :url)]
    [:div.activity-embed-wrap.box-shadow
     (if-not embed
       [:div.activity-preview
        [:img {:src preview-url :width "100%"}]]
       [:div.embed-container
        {"dangerouslySetInnerHTML"
         #js{:__html (generic-responsive-iframe embed)}}])
     (when tags
       [:div.activity-tags-wrap
        [:div.tags
          "TAGS: "]
        (for [tag tags]
         ^{:key (gensym "tag-")}
          [:div.tag {:on-click #(rf/dispatch [:show-tag tag])}
            [:span tag]])])]))
