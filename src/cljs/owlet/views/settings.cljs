(ns owlet.views.settings
  (:require [reagent.core :refer [atom]]
            [re-com.core :refer [checkbox]]
            [re-frame.core :as rf]
            [owlet.firebase :as fb]
            [owlet.views.login-only :refer [login-only-view]]))

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
               "]"]]]])]]]])


(defn settings-view []
  (let [roles (atom {:content-creator false
                     :teacher false
                     :student false})
        _ (add-watch roles :watcher
                     (fn [key atm _ new-state]
                       (when (apply fb/legal-db-value? (map val new))
                          (prn "valid"))))]
    (fn []
      (if @(rf/subscribe [:my-id])
        [settings roles]
        [login-only-view]))))
