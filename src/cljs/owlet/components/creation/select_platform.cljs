(ns owlet.components.creation.select-platform
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn select-platform [klipse?]
  (let [platforms @(rf/subscribe [:activity-platforms])
        platform (reagent/atom nil)]
    [:div#select-platform
     [:form
      (doall (for [p platforms
                   :let [platform-name (:name p)]]
              (if klipse?
                (when (= platform-name "OWLET")
                  ^{:key (gensym "platform-")}
                   [:div
                    [:input {:type "radio"
                             :disabled true
                             :checked true}]
                    [:label platform-name]])
                ^{:key (gensym "platform-")}
                 [:div
                  [:input {:type "radio"}]
                  [:label platform-name]])))]]))
