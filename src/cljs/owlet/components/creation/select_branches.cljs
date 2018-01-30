(ns owlet.components.creation.select-branches
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-branches []
  (let [branches @(rf/subscribe [:activity-branches])]
    [:div#select-branch.box-shadow
     [:form
      (doall (for [b branches
                   :let [branch-name (:name b)
                         checked? (reagent/atom false)]]
               ^{:key (gensym "branch-")}
                [:div
                 [checkbox
                  :model checked?
                  :on-change #(swap! checked? not)
                  :label branch-name]]))]]))
