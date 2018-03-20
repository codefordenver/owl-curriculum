(ns owlet.views.settings
  (:require [reagent.core :refer [atom]]
            [re-com.core :refer [checkbox]]
            [re-frame.core :as rf]
            [owlet.views.login-only :refer [login-only-view]]))

(defn settings []
  (let [role-set (rf/subscribe [:my-roles])
        toggle-role #(rf/dispatch [:toggle-user-role %])]
    [:div.information-wrap
     [:div.information-inner-wrap.col-xs-12.col-sm-11
      [:div.information-inner
       [:div.inner-height-wrap
        [:h1 [:mark "My Settings"]]
        [:h2 "Choose role(s):"]

        [checkbox
         :style {:align-self "center"}
         :label "Student"
         :model     (contains? @role-set :student)
         :on-change #(toggle-role :student)]

        [checkbox
         :style {:align-self "center"}
         :label "Teacher"
         :model     (contains? @role-set :teacher)
         :on-change #(toggle-role :teacher)]

        [checkbox
         :style {:align-self "center"}
         :label "Content Creator"
         :model     (contains? @role-set :content-creator)
         :on-change #(toggle-role :content-creator)]

        (when (contains? @role-set :content-creator)
         [:div.settings-section
          [:h4 [:i [:b "NOTE: "]
                "CONTENT CREATION FUNCTIONALITY IS CURRENTLY IN DEVELOPMENT. IT DOES NOT WORK - YET!"
                [:br] "THE LINKS BELOW ARE JUST A PREVIEW OF WHAT'S TO COME :)"]
           [:br][:br]]
          [:h3 "CREATE..."]
          [:ul
           [:li [:b "A general purpose, read-only activity"]
            [:ul
             [:li [:a.semi-bold {:href "#/create/general-activity"}
                      "Instructional medium of your choice"]
                  [:ul
                   [:li "Embed a slideshow iframe, video iframe, or your own HTML ["
                        [:a {:href "#/activity/#!5g8tVqDGTeo2aMUey0M8G"} "see example"]
                        "]"]]]]]
           [:li [:b "A coding activity with built-in code evaluation"]
            [:ul
             [:li [:a.semi-bold {:href "#/create/klipse-panel-activity"}
                      "MULTIPLE code evaluators & text-based instruction"]
                  [:ul
                   [:li "Supports Python, JavaScript, and/or Clojure ["
                        [:a {:href "#/activity/#!3PR1mUaAI0msuao28W2SCs"} "see example"]
                        "]"]]]
             [:li [:a.semi-bold {:href "#/create/klipse-slides-activity"}
                      "A SINGLE code evaluator & slides-based instruction"]
                  [:ul
                   [:li "Supports Python, JavaScript, or Clojure ["
                        [:a {:href "#/activity/#!34hdwSOWpiEEWSWaqQGcGC"} "see example"]
                        "]"]]]]]]])]]]]))

(defn settings-view []
  (if @(rf/subscribe [:my-id])
    [settings]
    [login-only-view]))
