(ns owlet.components.filter-bar
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [cljsjs.jquery]
            [goog.string :as goog-string]))

(defn element-is-in-view [el]
  (let [rect (.getBoundingClientRect el)
        parent-rect (.getBoundingClientRect (.-parentElement el))]
      (and (<= (.-right rect) (.-right parent-rect))
           (>= (.-left rect) (.-left parent-rect)))))

(defn scroll-filters [{{right? :right?} :direction}]
  (let [filter-elements (array-seq (js/document.getElementsByClassName "filter"))]
    (when-let [elements-in-view (not-empty (filter #(element-is-in-view %) filter-elements))]
      (let [scroll-to-rect (.getBoundingClientRect (if right?
                                                     (last elements-in-view)
                                                     (first elements-in-view)))
            filter-items-rect (.getBoundingClientRect (js/document.getElementById "filter-items"))
            current-scroll (.scrollLeft (js/jQuery "#filter-items"))
            scroll-distance (if right?
                              (+ current-scroll (- (.-left scroll-to-rect) (.-left filter-items-rect)))
                              (+ current-scroll (- (.-right scroll-to-rect) (.-right filter-items-rect))))]
        (.animate (js/jQuery "#filter-items") (clj->js {:scrollLeft scroll-distance}) (Math/abs (* 2 (- scroll-distance current-scroll))))))))

(defn toggle-filter [e filter-term]
  (let [filters (:filters @(rf/subscribe [:activities-by-filter]))]
    (if (.-checked (.-target e))
     (rf/dispatch [:filter-activities-by-selected-terms (distinct (conj filters filter-term))])
     (rf/dispatch [:filter-activities-by-selected-terms (distinct (remove #(= % filter-term) filters))]))))

(defn is-checked? [filter]
  (let [activities-by-filter @(rf/subscribe [:activities-by-filter])]
    (some #(= filter %) (:filters activities-by-filter))))

(defn filter-bar []
  (if (some #(= @(rf/subscribe [:active-view]) %) [:branches-view :filtered-activities-view])
    [:div#filter-bar
     [:span#arrow-left.arrow {:on-click #(scroll-filters {:direction {:right? false}})}
      (goog-string/unescapeEntities "&lt;")]
     [:div#filter-items
      (doall
        (for [term @(rf/subscribe [:filter-bar-terms])
              :let [name (:name term)
                    type (:type term)
                    filter-term (hash-map :name (:name term)
                                          :type (:type term))]]
           (when-not (= filter-term (:pre-filter @(rf/subscribe [:activities-by-filter])))
             (case type
               "Branch"   ^{:key (gensym "branch-")}
                           [:div.filter
                            [:input {:id (str name "-filter")
                                     :type "checkbox"
                                     :on-click #(toggle-filter % filter-term)
                                     :defaultChecked (is-checked? filter-term)}]
                            [:label {:for (str name "-filter")}
                             (clojure.string/upper-case name)]]
               "Platform" ^{:key (gensym "platform-")}
                           [:div.filter
                            [:input {:id (str name "-filter")
                                     :type "checkbox"
                                     :on-click #(toggle-filter % filter-term)
                                     :defaultChecked (is-checked? filter-term)}]
                            [:label {:for (str name "-filter")}
                             (clojure.string/upper-case name)]]
               "Tag"      ^{:key (gensym "tag-")}
                           [:div.filter
                            [:input {:id (str name "-filter")
                                     :type "checkbox"
                                     :on-click #(toggle-filter % filter-term)
                                     :defaultChecked (is-checked? filter-term)}]
                            [:label {:for (str name "-filter")}
                             (clojure.string/upper-case name)]]))))]
     [:span#arrow-right.arrow {:on-click #(scroll-filters {:direction {:right? true}})}
      (goog-string/unescapeEntities "&gt;")]]
    [:div#spacer]))
