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
      [:h1#title [:mark "Get started by choosing a branch below"]]
      [:br]
      (doall
        (for [b (sort-by :name @activity-branches)
                :let [pair (vector (:color b) (:name b))
                      branch-key (->kebab-case (-> pair
                                                   second
                                                   keyword))]]
          ^{:key (gensym "branch-")}
           [branch pair branch-key]))]]))
