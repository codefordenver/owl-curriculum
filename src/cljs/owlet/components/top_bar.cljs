(ns owlet.components.top-bar
  (:require [owlet.components.login :refer [login-component]]
            [owlet.components.search-bar :refer [search-bar]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [owlet.helpers :refer [class-names]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [cljsjs.jquery]))

(defonce top-bar-classes
  (reagent/atom #{"top-bar"}))

(defonce filters (reagent/atom '()))

(def scroll-delta (atom `(0 0)))

(def swap-scroll (comp (partial drop-last) conj))

(defn push-scroll [collection n]
  (conj (drop 1 collection) n))

(defn update-scroll! [n]
  (swap! scroll-delta push-scroll n))

(defn change-scroll! [n]
  (swap! scroll-delta swap-scroll n))

(defn check-scroll [contentNodeRef]
  (update-scroll! (.-scrollTop contentNodeRef))
  (let [delta (apply - @scroll-delta)
        search (aget (js/document.getElementsByClassName "form-control") 0)]
    (when (>= delta 50)
      (do
        (swap! top-bar-classes conj "hidden-top-bar")
        (.blur search)
        (change-scroll! (.-scrollTop contentNodeRef))))
    (when (<= delta -50)
        (swap! top-bar-classes disj "hidden-top-bar")
        (change-scroll! (.-scrollTop contentNodeRef)))))

(defn top-bar []
  (reagent/create-class
    {:component-did-mount
     (fn []
       (let [contentNodeRef (aget (js/document.getElementsByClassName "content") 0)]
         (.addEventListener contentNodeRef "scroll" #(check-scroll contentNodeRef))))
     :reagent-render
     (fn []
       [:div
        [:div.flex-row.box-shadow {:class (class-names @top-bar-classes)}
         [:a {:href "#"}
          [:div.owlet-logo-div.flex-row
           [:img.owlet-owl {:src "../img/owlet-owl.png"
                            :alt "Owlet home"}]
           [:div.owlet-text
             [:h1 "OWLET"]
             [:p "TECHNOLOGY EDUCATION MADE SIMPLER"]]]]
         [:div.search-container
          [search-bar]]
         [:div.navigation
          [:a {:href "#/about"}
           [:button "ABOUT US"]]
          [:a {:href "#/branches"}
           [:button "EXPLORE"]]
          (when @(rf/subscribe [:my-id])
           [:a {:href "#/settings"}
            [:button [:i.fa.fa-gear]]])
          [login-component]]]
        (when-not (some #(= @(rf/subscribe [:active-view]) %) [:welcome-view :about-view :settings-view :activity-view])
          [:div.flex-row.filter-bar
           [:span.arrow-left]
           (doall
             (for [term (shuffle @(rf/subscribe [:filter-bar-terms]))
                   :let [name (:name term)
                         type (:type term)]]
                (case type
                  "Branch"   ^{:key (gensym "branch-")}
                              [:div
                               [:input {:type "checkbox"
                                        :on-click (fn [e]
                                                    (if (.-checked (.-target e))
                                                     (rf/dispatch [:filter-activities-by-selected-terms (swap! filters conj term)])
                                                     (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (remove #(= % term) @filters))])))}]
                               [:label name]]
                  "Platform" ^{:key (gensym "platform-")}
                              [:div
                               [:input {:type "checkbox"
                                        :on-click (fn [e]
                                                    (if (.-checked (.-target e))
                                                     (rf/dispatch [:filter-activities-by-selected-terms (swap! filters conj term)])
                                                     (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (remove #(= % term) @filters))])))}]
                               [:label name]]
                  "Tag"      ^{:key (gensym "tag-")}
                              [:div
                               [:input {:type "checkbox"
                                        :on-click (fn [e]
                                                    (if (.-checked (.-target e))
                                                     (rf/dispatch [:filter-activities-by-selected-terms (swap! filters conj term)])
                                                     (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (remove #(= % term) @filters))])))}]
                               [:label name]])))
           [:span.arrow-right]])])}))
