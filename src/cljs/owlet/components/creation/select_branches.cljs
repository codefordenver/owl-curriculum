(ns owlet.components.creation.select-branches
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn select-branches [klipse?]
  (let [branches @(rf/subscribe [:activity-branches])]
    [:div#select-branches
     [:form
      (doall (for [b branches
                   :let [branch-name (:name b)
                         checked? (reagent/atom false)]]
              (if klipse?
                (when (= branch-name "Computer Science")
                  ^{:key (gensym "branch-")}
                   [:div
                    [:input {:type "checkbox"
                             :disabled true
                             :checked true}]
                    [:label branch-name]])
                ^{:key (gensym "branch-")}
                 [:div
                  [:input {:type "checkbox"}]
                  [:label branch-name]])))]]))
