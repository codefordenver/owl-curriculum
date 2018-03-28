(ns owlet.components.search-bar
  (:require [owlet.helpers :refer [clean-search-term]]
            [re-com.core :refer [typeahead]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defonce search-model (reagent/atom {}))

(defonce suggestion-count (reagent/atom 16))

(defn toggle-suggestions []
  (if-let [suggestions (aget (js/document.getElementsByClassName "rc-typeahead-suggestions-container") 0)]
    (let [hidden (.-hidden suggestions)]
      (when-not (nil? suggestions)
        (set! (.-hidden suggestions) (not hidden))))))

(defn reset-search []
  (let [search (aget (js/document.getElementsByClassName "form-control") 0)]
    (set! (.-value search) "")
    (.blur search)))

(defn search-bar []
  (let [branches (rf/subscribe [:activity-branches])
        tags (rf/subscribe [:tags])
        activity-titles (rf/subscribe [:activity-titles])
        activity-platforms (rf/subscribe [:activity-platforms])
        platform-names (map #(:name %) @activity-platforms)
        search-collections (concat (map #(:name %) @tags) (map #(:name %) @branches) @activity-titles platform-names)
        result-formatter #(-> {:term %})
        suggestion-renderer #(:term %)
        special-char-pattern (re-pattern "[^A-Za-z0-9]")
        suggestions-for-search
        (fn [s]
          (if (< 1 (count s))
            (reset! suggestion-count 16)
            (reset! suggestion-count 0))
          (into []
                (take @suggestion-count
                      (for [n (distinct search-collections)
                            :when (re-find (re-pattern (str "(?i)" (string/replace s special-char-pattern #(str "\u005C" %)))) n)]
                        (result-formatter n)))))
        change-handler (fn [t]
                         (let [platform-search-names (map #(->kebab-case (:name %)) @activity-platforms)
                               platform-names (map #(:name %) @activity-platforms)
                               platform-index (.indexOf platform-names (:term t))
                               current-filter (when-not (nil? @(rf/subscribe [:activities-by-filter]))
                                                 (string/lower-case (:display-name @(rf/subscribe [:activities-by-filter]))))
                               current-activity (when-not (nil? @(rf/subscribe [:activity-in-view]))
                                                    (string/lower-case (get-in @(rf/subscribe [:activity-in-view]) [:fields :title])))
                               active-view  (when-not (nil? @(rf/subscribe [:active-view]))
                                               (string/lower-case (name @(rf/subscribe [:active-view]))))]
                           (if (>= platform-index 0)
                             (if (and (= current-filter (nth platform-names platform-index))
                                      (= active-view "filtered-activities-view"))
                               (js/setTimeout #(reset-search) 100)
                               (rf/dispatch [:filter-activities-by-search-term (nth platform-search-names platform-index)]))
                             (if (or (and (= current-activity (string/lower-case (:term t)))
                                          (= active-view "activity-view"))
                                     (= (string/lower-case (:term t)) current-filter))
                               (js/setTimeout #(reset-search) 100)
                               (rf/dispatch [:filter-activities-by-search-term (:term t)])))))]
    [:div.search-bar-wrap {:on-blur #(toggle-suggestions)
                           :on-focus #(toggle-suggestions)}
     [typeahead
      :class "search"
      :width "100%"
      :on-change change-handler
      :suggestion-to-string suggestion-renderer
      :debounce-delay 100
      :change-on-blur? true
      :rigid? true
      :data-source suggestions-for-search
      :model search-model
      :placeholder "What would you like to learn today? How about HTML, or LED circuits?"
      :render-suggestion suggestion-renderer
      :disabled? @(rf/subscribe [:loading-state])]]))
