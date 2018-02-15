(ns owlet.components.activity.embed
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [re-frame.core :as rf]
            [clojure.string :refer [lower-case]]
            [reagent.core :as reagent]))

(defn activity-embed [embed tags preview]
  (reagent/create-class
    {:component-did-mount
     (fn []
       (js/srcDoc.set (js/document.getElementById "klipseSlides")))
     :reagent-render
     (fn []
       (let [preview-url (-> preview :sys :url)
             activity @(rf/subscribe [:activity-in-view])
             activity-type (get-in activity [:sys :contentType :sys :id])]
         [:div.activity-embed-wrap
          (if-not embed
            [:div.activity-preview
             [:img {:src preview-url :width "100%"}]]
            [:div.embed-container
             (case activity-type
               "activity" {"dangerouslySetInnerHTML"
                           #js{:__html embed}}
               "klipseActivity" [:iframe#klipseSlides {:srcDoc (get-in activity [:fields :iframeContent])
                                                       :frameBorder "0"
                                                       :scrolling "no"
                                                       :width "576"
                                                       :height "420"
                                                       :allowFullScreen true}])])
          (when tags
            [:div.activity-tags-wrap
             [:div.tags
               "TAGS: "]
             (for [tag tags :let [name (:name tag)]]
                 ^{:key (gensym "tag-")}
                 [:div.tag.inactive {:on-click #(rf/dispatch [:show-tag name])}
                   [:span name]])])]))}))
