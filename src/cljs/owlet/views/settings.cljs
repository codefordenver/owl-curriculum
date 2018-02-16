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
        [:h4 [:i [:b "NOTE: "]
              "CONTENT CREATION FUNCTIONALITY IS CURRENTLY IN DEVELOPMENT. IT DOES NOT WORK - YET!"
              [:br] "THE LINKS BELOW ARE JUST A PREVIEW OF WHAT'S TO COME :)"]
         [:br][:br]]
        [:h3 "CREATE"]
        [:ul
         [:li [:b "A general purpose, read-only activity"]
          [:ul
           [:li [:a.semi-bold {:href "#/create/embed-activity"}
                    "Instructional medium of your choice"]
                [:ul
                 [:li "Embed a slideshow iframe, video iframe, or your own HTML ["
                      [:a {:href "#/activity/#!5g8tVqDGTeo2aMUey0M8G"} "see example"]
                      "]"]]]]]
         [:li [:b "An activity w/ built-in code evaluation"]
          [:ul
           [:li [:a.semi-bold {:href "#/create/klipse-panel-activity"}
                    "MULTIPLE code evaluators & text-based instruction"]
                [:ul
                 [:li "Supports Python, JavaScript, and/or Clojure ["
                      [:a {:href "#/activity/hello-world"} "see example"]
                      "]"]]]
           [:li [:a.semi-bold {:href "#/create/klipse-slides-activity"}
                    "A SINGLE code evaluator & slides-based instruction"]
                [:ul
                 [:li "Supports Python, JavaScript, or Clojure ["
                      [:a {:href "#/activity/#!34hdwSOWpiEEWSWaqQGcGC"} "see example"]
                      "]"]]]]]]])]]]])

(defn settings-view []
  (let [roles (atom {:content-creator false
                     :teacher false
                     :student false})
        _ (add-watch roles :foo (fn [k & rest] (prn rest)))]
    (fn []
      (if @(rf/subscribe [:my-id])
        [settings roles]
        [login-only-view]))))
