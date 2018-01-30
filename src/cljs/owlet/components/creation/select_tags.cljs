(ns owlet.components.creation.select-tags
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-tags [klipse?]
  (let [tags @(rf/subscribe [:tags])]
    [:div#select-tags.box-shadow
     [:form
      (doall (for [t tags
                   :let [checked? (reagent/atom false)
                         name (:name t)]]
               (if klipse?
                 (when (:klipseRelated t)
                   ^{:key (gensym "tag-")}
                    [:div
                     [checkbox
                      :model checked?
                      :on-change #(reset! checked? %)
                      :label name]])
                 ^{:key (gensym "tag-")}
                  [:div
                   [checkbox
                    :model checked?
                    :on-change #(reset! checked? %)
                    :label name]])))]]))
