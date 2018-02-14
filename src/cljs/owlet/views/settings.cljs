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
                role-list (set @my-roles)]
            (if (contains? role-list new-role)
              (rf/dispatch [:update-user-roles! (disj role-list new-role)])
              (rf/dispatch [:update-user-roles! (conj role-list new-role)]))))]
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
           :model    (role-assignment :teacher)
           :on-change #(role-change-handler :teacher)]

          [checkbox
           :style {:align-self "center"}
           :label "Content Creator"
           :model     (role-assignment :content-creator)
           :on-change #(role-change-handler :content-creator)]

          (when (contains? (set @my-roles) :content-creator)
            [:div.settings-section
             [:h3 "Create Activity"]
             [:ul
              [:li "Interactive:"]
              [:ul
               [:li [:a {:href "#/create/klipse-panel-activity"}
                     "Coding, Multi-Panel"]
                " — Python, JavaScript, and/or Clojure ["
                [:a {:href "#/activity/hello-world"} "Example"]
                "]"]]]])]]]])))


(defn settings-view []
  (if @(rf/subscribe [:my-id])
    [settings]
    [login-only-view]))
