(ns owlet.components.activity.info
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [owlet.helpers :refer [showdown]]))

(defn activity-info [platform summary why preRequisites materials]
  (let [showing? (reagent/atom false)
        platform-name (:name platform)
        platform-search-name (:search-name platform)
        platform-color (:color platform)
        platform-download (:requiresDownload platform)
        platform-free (:free platform)
        set-as-showdown (fn [title field & [class]]
                          [:div {:class class
                                 "dangerouslySetInnerHTML"
                                        #js{:__html (.makeHtml showdown (str title field))}}])]
    [:div.activity-info-wrap.box-shadow
     [:div
       [:span.h3 "Platform"] [:br]
       [re-com/popover-anchor-wrapper
          :showing? showing?
          :position :below-right
          :anchor [:button.platform.btn
                   {:on-click #(rf/dispatch [:show-platform platform-search-name])
                    :style {:background-color platform-color}
                    :on-mouse-over (handler-fn (reset! showing? true))
                    :on-mouse-out  (handler-fn (reset! showing? false))}
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
     [:br]
     [set-as-showdown "<span class=\"h3\">Summary</span><br>" summary]
     (when why
      [set-as-showdown "<span class=\"h3\">Why?</span><br>" why])
     (when preRequisites
      [set-as-showdown "<span class=\"h3\">Pre-requisites</span><br>" preRequisites])
     (when materials
      [set-as-showdown "<span class=\"h3\">Materials</span>" materials "list-title"])]))
