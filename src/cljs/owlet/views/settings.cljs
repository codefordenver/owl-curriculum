(ns owlet.views.settings
  (:require [reagent.core :refer [atom]]
            [re-com.core :refer [checkbox]]))

(defn settings-view []
  (let [roles (atom {:content-creator false
                     :teacher false
                     :student false})
        _ (add-watch roles :foo (fn [k & rest] (prn rest)))]
    (fn []
      [:div.information-wrap
       [:div.information-inner-wrap.col-xs-12.col-lg-8
        [:div.information-inner
         [:div.inner-height-wrap
          [:h1 [:mark "My Settings"]]
          [:h2 "Choose roles:"]
          [checkbox
           :label "Student"
           :model     (:student @roles)
           :on-change #(swap! roles update :student not)]
          [checkbox
           :label "Teacher"
           :model     (:teacher @roles)
           :on-change #(swap! roles update :teacher not)]
          [checkbox
           :label "Content Creator"
           :model     (:content-creator @roles)
           :on-change #(swap! roles update :content-creator not)]]]]])))