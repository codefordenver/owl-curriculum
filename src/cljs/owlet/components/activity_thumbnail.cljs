(ns owlet.components.activity-thumbnail
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [re-frame.core :as rf]
            [clojure.string :refer [lower-case]]
            [reagent.core :as reagent]))

(defn activity-thumbnail [fields entry-id display-name]
  (let [preview-image-url (get-in fields [:preview :sys :url])
        image (or preview-image-url "img/default-thumbnail.png")
        {:keys [title summary platform tags]} fields
        platform-name (:name platform)
        platform-search-name (:search-name platform)
        platform-color (:color platform)
        platform-download (:requiresDownload platform)
        platform-free (:free platform)
        route-param (first (keys @(rf/subscribe [:route-params])))
        showing? (reagent/atom false)]
    [:div.col-xs-12.col-md-6.col-lg-4
     [:div.activity-thumbnail-wrap
      [:a {:href (str "#/activity/#!" entry-id)}
       [:div.activity-thumbnail
        [:div.image {:style {:background-image (str "url('" image "')")}}]
        [:mark.title title]]]
      [:div.platform-wrap
       [:b "PLATFORM"][:br]
       [re-com/popover-anchor-wrapper
          :showing? showing?
          :position :below-right
          :anchor [:div.platform.btn
                   {:on-click #(rf/dispatch [:show-platform platform-search-name])
                    :style {:background-color platform-color}
                    :on-mouse-over (when (not= route-param :platform)
                                     (handler-fn (reset! showing? true)))
                    :on-mouse-out  (when (not= route-param :platform)
                                     (handler-fn (reset! showing? false)))}
                   platform-name]
          :popover [re-com/popover-content-wrapper
                    :close-button? false
                    :body "Click for more info"]]
       [:span.platform-details
          " > "
          (cond (true? platform-free) [:b "FREE"]
                (false? platform-free) [:span {:style {:color "green"
                                                       :font-weight "bold"}} "$"])
          (when platform-download
                [:span " | " [:b "download required"]])]]
      [:div.summary summary]
      (when tags
        (for [tag tags :let [name (:name tag)]]
            ^{:key (gensym "tag-")}
            [:div {:class (if (= (lower-case name) (lower-case display-name))
                           "tag"
                           "inactive tag")
                   :on-click #(rf/dispatch [:show-tag name])}
              [:span name]]))]]))
