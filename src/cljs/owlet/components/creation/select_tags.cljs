(ns owlet.components.creation.select-platforms
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-tags [klipse?]
  (let [tags @(rf/subscribe [:tags])]
    [:div#select-tags
     [:form
      (doall (for [t tags
                   :let [checked? (reagent/atom false)
                         id (reagent/atom (:name t))]]
               (if klipse?
                 (when (:klipseRelated t)
                   ^{:key (gensym "tag-")}
                    [:div
                     [checkbox
                      :model checked?
                      :on-change #(reset! checked? %)
                      :label (:name t)]])
                 ^{:key (gensym "tag-")}
                  [:div
                   [checkbox
                    :model checked?
                    :on-change #(reset! checked? %)
                    :label (:name t)]])))]]))
