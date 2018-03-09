(ns owlet.views.settings
  (:require [reagent.core :refer [atom]]
            [re-com.core :refer [checkbox]]
            [re-frame.core :as rf]
            [owlet.views.login-only :refer [login-only-view]]))

(defn settings []
  (let [my-roles (rf/subscribe [:my-roles])
        role-assignment #(contains? (set @my-roles) (name %))
        role-change-handler
        (fn [new-role]
          (let [new-role (name new-role)
                role-set (set @my-roles)]
            (if (contains? role-set new-role)
              (rf/dispatch [:update-user-roles! (disj role-set new-role)])
              (rf/dispatch [:update-user-roles! (conj role-set new-role)]))))]
    (fn []
      [:div.information-wrap
       [:div.information-inner-wrap.col-xs-12.col-sm-11
        [:div.information-inner
         [:div.inner-height-wrap
          [:h1 [:mark "My Settings"]]
          [:h2 "Choose role(s):"]

          [checkbox
           :style {:align-self "center"}
           :label "Student"
           :model (role-assignment :student)
           :on-change #(role-change-handler :student)]

          [checkbox
           :style {:align-self "center"}
           :label "Teacher"
           :model     (role-assignment :teacher)
           :on-change #(role-change-handler :teacher)]

          [checkbox
           :style {:align-self "center"}
           :label "Content Creator"
           :model     (role-assignment :content-creator)
           :on-change #(role-change-handler :content-creator)]

          (when (contains? (set @my-roles) (name :content-creator))
           [:div.settings-section
            [:h4 [:i [:b "NOTE: "]
                  "CONTENT CREATION FUNCTIONALITY IS CURRENTLY IN DEVELOPMENT. IT DOES NOT WORK - YET!"
                  [:br] "THE LINKS BELOW ARE JUST A PREVIEW OF WHAT'S TO COME :)"]
             [:br][:br]]
            [:h3 "CREATE"]
            [:ul
             [:li [:b "A general purpose, read-only activity"]
              [:ul
               [:li [:a.semi-bold {:href "#/create/general-activity"}
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
                          "]"]]]]]]])]]]])))

(defn settings-view []
  (if @(rf/subscribe [:my-id])
    [settings]
    [login-only-view]))
