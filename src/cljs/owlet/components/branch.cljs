(ns owlet.components.branch
  (:require [re-frame.core :as rf]
            [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [camel-snake-kebab.core :refer [->kebab-case]]))


(defn branch [[color branch-name description] branch-key]
  (let [lines                (str/split (str/upper-case branch-name) " ")
        name-line1           (first lines)
        name-line2           (rest lines)
        hover-image          (reagent/atom "")
        showing?             (reagent/atom false)
        set-hover!  (fn [images]
                      ; Changes hover-image to a new, random URL, if avail.
                      (reset! hover-image
                              (-> images
                                  (disj @hover-image)
                                  seq
                                  rand-nth
                                  (or @hover-image))))
        activities-by-branch
                    (rf/subscribe [:activities-by-branch])]
    (fn []
      ; Form-2 component needed so hover-image atom is not redefined on
      ; every render.
      (let [counter        (-> @activities-by-branch
                               branch-key
                               :count)
            preview-images (-> @activities-by-branch
                               branch-key
                               :preview-urls
                               set)]
        [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
         [re-com/popover-anchor-wrapper
            :showing? showing?
            :position :below-center
            :anchor  [:div.branchwrap
                      {:on-mouse-over (handler-fn (reset! showing? true))
                       :on-mouse-out (handler-fn (reset! showing? false))
                       :style {:background-image (str "url('" @hover-image "')")}
                       :on-mouse-enter #(set-hover! preview-images)}
                      [:div.branch-bg
                        {:style {:background-color color
                                 :background-image (str "linear-gradient(to right, "
                                                        color
                                                        " 25%, rgba(0,0,0,0) 75%")}}
                        [:a.branch {:href (str "#/branch/" (->kebab-case branch-name))}
                         [:h2 [:mark name-line1]
                          (when (<= 1 (count name-line2))
                            [:span
                             [:br]
                             [:mark (str/join " " name-line2)]])]
                         [:div.counter
                          [:p counter]]]]]
            :popover [re-com/popover-content-wrapper
                      :close-button? false
                      :body description]]]))))
