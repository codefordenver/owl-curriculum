(ns owlet.views.filtered-activities
  (:require [re-frame.core :as rf]
            [owlet.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet.components.back :refer [back]]
            [owlet.helpers :refer [showdown]]
            [owlet.components.email-notification :refer [email-notification]]))

(defn filtered-activities-view []
  (let [filtered-activities @(rf/subscribe [:activities-by-filter])]
    [:div.branch-activities-wrap
     [email-notification]
     (if-not filtered-activities
       [:h2.pushed-left [:mark [:b "Loading..."]]]
       (if (= filtered-activities "error")
         [:h2.pushed-left [:mark.box [back] [:b "Nothing here. Try a different search above."]]]
         (let [{:keys [display-name activities & description]} filtered-activities]
           [:div
            [:h2.pushed-left [:mark [back] display-name]]
            (when description
              ; filtering by platform
              [:div
                 [:div {:class "platform-description"
                        "dangerouslySetInnerHTML"
                               #js{:__html (.makeHtml showdown description)}}]
                 [:div {:style {:margin-left "15px"}}
                  [:h3 {:style {:margin-bottom "15px"
                                :margin-top "40px"}}
                    [:mark "Activities"]]]])
            [:div.flexcontainer-wrap
             (if (seq activities)
               (for [activity activities
                     :let [fields (:fields activity)
                           entry-id (get-in activity [:sys :id])]]
                 ^{:key [entry-id (gensym "key-")]} [activity-thumbnail fields entry-id display-name])
              [:p.no-activities [:mark (str "Nothing yet, but we're working on it.")]])]])))]))
