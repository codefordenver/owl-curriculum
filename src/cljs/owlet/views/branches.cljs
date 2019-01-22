(ns owlet.views.branches
  (:require [re-frame.core :as rf]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet.components.branch :refer [branch]]
            [owlet.components.email-notification :refer [email-notification]]))

(defn branches-view []
  (let [activity-branches (rf/subscribe [:activity-branches])]
    [:div#branches
     [email-notification]
     [:section
      (doall
        (for [b (sort-by :name @activity-branches)
                :let [props (vector (:color b) (:name b) (:description b))
                      branch-key (->kebab-case (-> props
                                                   second
                                                   keyword))]]
          ^{:key (gensym "branch-")}
           [branch props branch-key]))]]))
