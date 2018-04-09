(ns owlet.components.filter-bar
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [cljsjs.jquery]
            [goog.string :as goog-string]))

(defonce filters (reagent/atom '()))

(defn element-is-in-view [el right?]
  (let [rect (.getBoundingClientRect el)
        parent-rect (.getBoundingClientRect (.-parentElement el))]
      (if right?
        (>= (.-right rect) (.-right parent-rect))
        (<= (.-left rect) (.-left parent-rect)))))

(defn scroll-filters [right?]
  (let [filter-elements (array-seq (js/document.getElementsByClassName "filter"))
        scroll-to-rect (if right?
                        (.getBoundingClientRect (first (filter #(element-is-in-view % right?) filter-elements)))
                        (.getBoundingClientRect (last (filter #(element-is-in-view % right?) filter-elements))))
        filter-items-rect (.getBoundingClientRect (js/document.getElementById "filter-items"))
        current-scroll (.scrollLeft (js/jQuery "#filter-items"))]
    (prn (if right?
           (.-textContent (first (filter #(element-is-in-view % right?) filter-elements)))
           (.-textContent (last (filter #(element-is-in-view % right?) filter-elements)))))
    (if right?
      (.scrollLeft (js/jQuery "#filter-items") (+ current-scroll (- (.-left scroll-to-rect) (.-left filter-items-rect))))
      (.scrollLeft (js/jQuery "#filter-items") (+ current-scroll (- (.-right scroll-to-rect) (.-right filter-items-rect)))))))


(defn filter-bar []
  (if (some #(= @(rf/subscribe [:active-view]) %) [:branches-view :filtered-activities-view])
    [:div#filter-bar
     [:span.arrow-left {:on-click #(scroll-filters false)}
      (goog-string/unescapeEntities "&lt;")]
     [:div#filter-items
      (doall
        (for [term @(rf/subscribe [:filter-bar-terms])
              :let [name (:name term)
                    type (:type term)
                    filter-term (hash-map :name (:name term)
                                          :type (:type term))]]
           (case type
             "Branch"   ^{:key (gensym "branch-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click (fn [e]
                                               (if (.-checked (.-target e))
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (conj @filters filter-term)))])
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (remove #(= % filter-term) @filters)))])))
                                   :defaultChecked (:checked term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]]
             "Platform" ^{:key (gensym "platform-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click (fn [e]
                                               (if (.-checked (.-target e))
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (conj @filters filter-term)))])
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (remove #(= % filter-term) @filters)))])))
                                   :defaultChecked (:checked term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]]
             "Tag"      ^{:key (gensym "tag-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click (fn [e]
                                               (if (.-checked (.-target e))
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (conj @filters filter-term)))])
                                                (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (remove #(= % filter-term) @filters)))])))
                                   :defaultChecked (:checked term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]])))]
     [:span.arrow-right {:on-click #(scroll-filters true)}
      (goog-string/unescapeEntities "&gt;")]]
    [:div#spacer]))
