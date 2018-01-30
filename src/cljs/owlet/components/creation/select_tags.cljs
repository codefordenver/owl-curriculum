(ns owlet.components.creation.select-tags
  (:require [re-frame.core :as rf]
            [re-com.core :refer [checkbox]]
            [reagent.core :as reagent]))

(defn select-tags [klipse?]
  (let [tags @(rf/subscribe [:tags])]
    [:div#select-tags.box-shadow
     [:form
      (doall (for [t tags
                   :let [tag-name (:name t)
                         checked? (reagent/atom false)]]
               (if klipse?
                 (when (:klipseRelated t)
                   ^{:key (gensym "tag-")}
                    [:div
                     [checkbox
                      :model checked?
                      :on-change #(reset! checked? %)
                      :label tag-name]])
                 ^{:key (gensym "tag-")}
                  [:div
                   [checkbox
                    :model checked?
                    :on-change #(reset! checked? %)
                    :label tag-name]])))]]))
