(ns owlet.views.settings
  (:require [reagent.core :refer [atom]]
            [re-com.core :refer [checkbox]]
            [re-frame.core :as rf]
            [owlet.views.login-only :refer [login-only-view]]
            [owlet.helpers :refer [keywordize-name]]))

(defn settings [roles]
  [:div.information-wrap
   [:div.information-inner-wrap.col-xs-12.col-sm-11
    [:div.information-inner
     [:div.inner-height-wrap
      [:h1 [:mark "My Settings"]]
      [:h2 "Choose role(s):"]
      [checkbox
       :style {:align-self "center"}
       :label "Student"
       :model     (:student @roles)
       :on-change #(swap! roles update :student not)]

      [checkbox
       :style {:align-self "center"}
       :label "Teacher"
       :model     (:teacher @roles)
       :on-change #(swap! roles update :teacher not)]
      [checkbox
       :style {:align-self "center"}
       :label "Content Creator"
       :model     (:content-creator @roles)
       :on-change #(swap! roles update :content-creator not)]
      (when (:content-creator @roles)
       [:div.settings-section
        [:h3 "Create Activity"]
        [:ul
         [:li "Interactive:"]
         [:ul
          [:li [:a {:href "#/create/klipse-panel-activity"}
                 "Coding, Multi-Panel"]
               " — Python, JavaScript, and/or Clojure ["
               [:a {:href "#/activity/hello-world"} "Example"]
               "]"]
          [:li [:a {:href "#/create/klipse-slides-activity"}
                 "Coding, Slides"]
               " — Python, JavaScript, and/or Clojure ["
               [:a {:href "#/activity/#!34hdwSOWpiEEWSWaqQGcGC"} "Example"]
               "]"]]]])]]]])


(defn settings-view []
  (let [roles (atom {:content-creator false
                     :teacher false
                     :student false})
        _ (add-watch roles :foo (fn [k & rest] (prn rest)))]
    (fn []
      (if @(rf/subscribe [:my-id])
        [settings roles]
        [login-only-view]))))
