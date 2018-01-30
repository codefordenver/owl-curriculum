(ns owlet.components.creation.select-branches
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-branches [klipse?]
  (let [branches @(rf/subscribe [:activity-branches])]
    [:div#select-branch.box-shadow
     [:form
      (doall (for [b branches
                   :let [branch-name (:name b)
                         checked? (reagent/atom false)]]
              (if klipse?
                (when (= branch-name "Computer Science")
                  ^{:key (gensym "branch-")}
                   [:div
                    [checkbox
                     :model true
                     :disabled? true
                     :on-change #(swap! checked? %)
                     :label branch-name]])
                ^{:key (gensym "branch-")}
                 [:div
                  [checkbox
                   :model checked?
                   :on-change #(reset! checked? %)
                   :label branch-name]])))]]))
