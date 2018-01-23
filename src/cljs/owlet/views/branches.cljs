(ns owlet.views.branches
  (:require [re-frame.core :as rf]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet.components.branch :refer [branch]]
            [owlet.components.email-notification :refer [email-notification]]))

(defn branches-view []
  (let [activity-branches (rf/subscribe [:activity-branches])]
    [:div.branches
     [email-notification]
     [:section
      [:h1#title [:mark "Get started by choosing a branch below"]]
      [:br]
      (doall
        (for [b @activity-branches
                :let [pair (vector (get-in b [:fields :color]) (get-in b [:fields :name]))
                      branch-key (->kebab-case (-> pair
                                                   second
                                                   keyword))]]
          ^{:key (gensym "branch-")}
           [branch pair branch-key]))]]))
