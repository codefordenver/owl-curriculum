(ns owlet.components.creation.select-tags
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn select-tags [klipse?]
  (let [tags @(rf/subscribe [:tags])]
    [:div#select-tags
     [:form
      (doall (for [t tags
                   :let [tag-name (:name t)
                         checked? (reagent/atom false)]]
               (if klipse?
                 (when (:klipseRelated t)
                   ^{:key (gensym "tag-")}
                    [:div
                     [:input {:type "checkbox"}]
                     [:label tag-name]])
                 ^{:key (gensym "tag-")}
                  [:div
                   [:input {:type "checkbox"}]
                   [:label tag-name]])))]]))
