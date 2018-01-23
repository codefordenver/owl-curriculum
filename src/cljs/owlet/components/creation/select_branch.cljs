(ns owlet.components.creation.select-branch
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-branch []
  (let [branches @(rf/subscribe [:activity-branches])]
    [:div {:id "select-branch"
           :style {}}
     [:form
      (doall (for [b branches
                   :let [branch-name (get-in b [:fields :name])
                         checked? (reagent/atom false)]]
               ^{:key (gensym "branch-")}
                [:div
                 [checkbox
                  :model checked?
                  :on-change #(swap! checked? not)
                  :label branch-name]]))]]))
