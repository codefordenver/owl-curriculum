(ns owlet.components.activity.embed
  (:require [re-frame.core :as rf]))

(defn check-slides [iframe-code]
  (let [cleaned-iframe (clojure.string/replace iframe-code #"\"" "'")
        iframe-url (re-find #"//.*?[^']*" cleaned-iframe)]
    (if-not (nil? (re-find #"slides.com" iframe-url))
        (if (nil? (re-find #"\?" iframe-url))
          (let [post-message-url (str iframe-url "?postMessageEvents=true")]
            (clojure.string/replace cleaned-iframe iframe-url post-message-url))
          (let [post-message-url (str iframe-url "&postMessageEvents=true")]
            (clojure.string/replace cleaned-iframe iframe-url post-message-url)))
        cleaned-iframe)))

(defn activity-embed [embed tags preview]
  (let [preview-url (-> preview :sys :url)
        activity @(rf/subscribe [:activity-in-view])
        activity-type (get-in activity [:sys :contentType :sys :id])]
    [:div.activity-embed-wrap.box-shadow
     (if-not embed
       [:div.activity-preview
        [:img {:src preview-url :width "100%"}]]
       [:div.embed-container
        {"dangerouslySetInnerHTML"
         #js{:__html (case activity-type
                       "klipseActivity" (check-slides embed)
                       "activity" embed)}}
        (when (= activity-type "klipseActivity")
          (.addEventListener js/window "message" post-message-handler))])
     (when tags
       [:div.activity-tags-wrap
        [:div.tags
          "TAGS: "]
        (for [tag tags]
         ^{:key (gensym "tag-")}
          [:div.tag {:on-click #(rf/dispatch [:show-tag tag])}
            [:span tag]])])]))
