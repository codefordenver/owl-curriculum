(ns owlet.components.creation.select-platform
  (:require [re-frame.core :as rf]
            [re-com.core :refer [radio-button]]
            [reagent.core :as reagent]))

(defn select-platform [klipse?]
  (let [platforms @(rf/subscribe [:activity-platforms])
        platform (reagent/atom nil)]
    [:div#select-platform.box-shadow
     [:form
      (doall (for [p platforms
                   :let [platform-name (:name p)]]
              (if klipse?
                (when (= platform-name "OWLET")
                  ^{:key (gensym "platform-")}
                   [:div
                    [radio-button
                     :model p
                     :value p
                     :disabled? true
                     :on-change #(reset! platform p)
                     :label platform-name]])
                ^{:key (gensym "platform-")}
                 [:div
                  [radio-button
                   :model platform
                   :value p
                   :on-change #(reset! platform p)
                   :label platform-name]])))]]))
