(ns owlet.views.temp-activities.print-errors
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]))

(defn temp-print-errors-view []
  [:div.activity
   [:div.activity-wrap
    [:div.activity-header.col-xs-12
     [activity-title "Print Errors" "mslim"]]
    [:div.activity-content.col-xs-12.col-sm-7
     [:div.activity-info-wrap
      [:div.panel-heading.flexcontainer-wrap
       [:div.panel-number "1"]
       [:h2 "Fix the code:"]]
      [:div.temp-err
       [klipse-component "Python" "print(\"hola mundo\""]]]
     [:div.activity-info-wrap
      [:div.panel-heading.flexcontainer-wrap
       [:div.panel-number "2"]
       [:h2 "Fix the code:"]]
      [:div.temp-err
       [klipse-component "Python" "print \"failure is the first step to success)"]]]
     [:div.activity-info-wrap
      [:div.panel-heading.flexcontainer-wrap
       [:div.panel-number "3"]
       [:h2 "Fix the code:"]]
      [:div.temp-err
       [klipse-component "Python" "print (a joke is the fastest way to the truth\")"]]]
     [:div.activity-info-wrap
      [:div.panel-heading.flexcontainer-wrap
       [:div.panel-number "4"]
       [:h2 "Fix the code:"]]
      [:div.temp-err
       [klipse-component "Python" "prnt(\"hello world\")"]]]
     [:div.activity-info-wrap
      [:div.panel-heading.flexcontainer-wrap
       [:div.panel-number "5"]
       [:h2 "Fix the code:"]]
      [:div.temp-err
       [klipse-component "Python" "print(1 + 1) + 1)"]]]]]])
