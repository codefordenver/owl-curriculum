(ns owlet.events.contentful
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet.db :as db]
            [owlet.config :as config]
            [re-futil.getter-setter-pipeline :refer [reg-setter]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [cuerdas.core :as str]
            [clojure.string :as string]
            [owlet.helpers :refer [keywordize-name remove-nil]]))


(defonce space-endpoint
         (str config/server-url
              "/content/space?library-view=true&space-id="
              config/owlet-activities-3-space-id))

(defn keyword-kebab [x]
  (keywordize-name (->kebab-case x)))


(rf/reg-event-fx
  :get-content-from-contentful
  (fn [{db :db} route-args]
    (let [[_ route-dispatch route-param] route-args]
      (if-not (seq (:activities db))
        {:http-xhrio {:method          :get
                      :uri             space-endpoint
                      :response-format (ajax/json-response-format {:keywords? true})
                      :on-success      [:get-content-from-contentful-success route-args]
                      :on-failure      [:get-content-from-contentful-failure route-args]}}
        {:dispatch [route-dispatch route-param]}))))


(reg-setter
  :get-content-from-contentful-failure
  [:on-app-failure]
  :transform-fn (constantly
                  {:show? true
                   :msg   (str "Not able to retrieve content from Contentful: "
                               "Possibly missing ENV variables")}))


(rf/reg-event-fx
  :get-content-from-contentful-success
  (fn [{db :db} [_ route-args {activities :activities
                               platforms :platforms
                               tags :tags
                               branches :branches}]]
    (let [route-dispatch (second route-args)
          route-param (get route-args 2)
          activity-titles (remove-nil (map #(get-in % [:fields :title]) activities))
          branches-template (->> (mapv (fn [branch]
                                         (let [branch-name (get-in branch [:fields :name])]
                                           (hash-map (keywordize-name branch-name)
                                             {:activities   []
                                              :display-name branch-name
                                              :count        0
                                              :preview-urls []}))) branches)
                                 (into {}))

          activities-by-branch (->> (mapv (fn [branch]
                                            (let [[branch-key branch-vals] branch]
                                             (let [display-name (:display-name branch-vals)
                                                   matches (filterv (fn [activity]
                                                                      (let [activity-branch-names (map #(:name %) (get-in activity [:fields :branches]))]
                                                                        (some #(= display-name %) activity-branch-names)))

                                                              activities)
                                                   preview-urls (mapv #(get-in % [:fields :preview :sys :url]) matches)]
                                               (if (seq matches)
                                                 (hash-map branch-key
                                                   {:activities   matches
                                                    :display-name display-name
                                                    :count        (count matches)
                                                    :preview-urls preview-urls})
                                                 branch))))
                                      branches-template)
                                 (into {}))]
      {:db (assoc db
            :activity-platforms (map #(:fields %) platforms)
            :activities activities
            :activity-branches (map #(:fields %) branches)
            :tags (map #(:fields %) tags)
            :activities-by-branch activities-by-branch
            :activity-titles activity-titles)
       :dispatch-n (list [route-dispatch route-param]
                         [:set-loading-state! false])})))

; route dispatchers

(rf/reg-event-fx
  :show-about
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :about-view]
                       [:set-active-document-title! "About"])}))

(rf/reg-event-fx
  :show-not-found
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :not-found-view]
                       [:set-active-document-title! "Not Found"])}))

(rf/reg-event-fx
  :show-settings
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :settings-view]
                       [:set-active-document-title! "Settings"])}))

(rf/reg-event-fx
  :show-confirm
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :confirm-view route-param]
                       [:set-active-document-title! "Success"])}))

(rf/reg-event-fx
  :show-subscribed
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :subscribed-view route-param]
                       [:set-active-document-title! "Success"])}))

(rf/reg-event-fx
  :show-unsubscribe
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :unsubscribe-view]
                       [:set-active-document-title! "Unsubscribe"])}))

(rf/reg-event-fx
  :show-branches
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :branches-view]
                       [:set-active-document-title! "Branches"]
                       [:remove-filtered-activities]
                       [:set-filter-bar-terms])}))

(rf/reg-event-fx
  :show-branch
  (fn [_ [_ route-param use-normal]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "branch"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param]
                       [:set-filter-bar-terms])}))

(rf/reg-event-fx
  :show-platform
  (fn [_ [_ route-param use-normal]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "platform"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param]
                       [:set-filter-bar-terms])}))

(rf/reg-event-fx
  :show-tag
  (fn [_ [_ route-param use-normal]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "tag"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param]
                       [:set-filter-bar-terms])}))

(rf/reg-event-fx
  :show-activity
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :activity-view]
                       [:set-activity-in-view route-param])}))

(rf/reg-event-fx
  :show-klipse
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :klipse-activity-view]
                       [:set-activity-in-view route-param])}))

(rf/reg-event-fx
  :show-temp-print-errors
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :temp-print-errors-view]
                       [:set-active-document-title! "Hello world!"])}))

(rf/reg-event-fx
  :show-create-klipse-panel-activity
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :create-klipse-panel-activity-view]
                       [:set-active-document-title! "Create Multi-Panel Coding Activity"])}))

(rf/reg-event-fx
  :show-create-klipse-slides-activity
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :create-klipse-slides-activity-view]
                       [:set-active-document-title! "Create Slides-Based Coding Activity"])}))

(rf/reg-event-fx
  :show-create-general-activity
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :create-general-activity-view]
                       [:set-active-document-title! "Create Embedded Activity"])}))

(rf/reg-event-fx
  :show-welcome
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :welcome-view]
                       [:set-active-document-title! "^OvO^" :skip-caps])}))


; search & filter

(rf/reg-event-db
  :remove-filtered-activities
  (fn [db [_]]
    (assoc db :activities-by-filter nil)))

(rf/reg-event-db
  :filter-activities-by-search-term
  (fn [db [_ term]]
    (let [search-term (keywordize-name term)
          activities (:activities db)
          set-path (fn [path]
                    (set! (.-location js/window) (str "/#/" path)))]
      ;; by branch
      ;; ---------

      (if-let [filtered-set (search-term (:activities-by-branch db))]
        (do
          (set-path (str "branch/" (->kebab-case term)))
          (assoc db :activities-by-filter (hash-map :filter-type "Branch"
                                                    :preview-urls (:preview-urls filtered-set)
                                                    :count (:count filtered-set)
                                                    :display-name (:display-name filtered-set)
                                                    :activities (:activities filtered-set)
                                                    :pre-filter {:name (:display-name filtered-set)
                                                                 :type "Branch"})))

        ;; by tag
        ;; --------
        (let [filtered-set (filter (fn [a]
                                     (some #(= search-term (keyword-kebab (:name %))) (get-in a [:fields :tags]))) activities)]
          (if (seq filtered-set)
            (let [tags (:tags db)
                  display-name (:name (first (filter #(= search-term (keyword-kebab (:name %))) tags)))]
              (set-path (str "tag/" (->kebab-case term)))
              (assoc db :activities-by-filter (hash-map :activities filtered-set
                                                        :display-name (str/capital display-name)
                                                        :filter-type "Tag"
                                                        :pre-filter {:name display-name
                                                                     :type "Tag"})))

            ;; by activity name (title)
            ;; ------------------------

            (let [filtered-set (filter #(when (= (get-in % [:fields :title]) term) %) activities)]
              (if (seq filtered-set)
                (let [activity (first filtered-set)
                      activity-id (get-in activity [:sys :id])]
                  (set-path (str "activity/#!" activity-id))
                  (assoc db :activity-in-view activity))

                ;; by activity id
                ;; ------------------------

                (if-let [activity (some #(when (= (get-in % [:sys :id]) term) %) activities)]
                  (assoc db :activity-in-view activity)

                  ;; by platform
                  ;; -----------

                  (let [filtered-set (filter #(let [platform (get-in % [:fields :platform :search-name])]
                                                 (when (= platform term) %)) activities)
                        platform-name (get-in (first filtered-set) [:fields :platform :name])]
                    (if (seq filtered-set)
                      (let [description (some #(when (= platform-name (:name %)) (:description %))
                                          (:activity-platforms db))]
                        (set-path (str "platform/" term))
                        (assoc db :activities-by-filter (hash-map :activities filtered-set
                                                                  :display-name platform-name
                                                                  :description description
                                                                  :filter-type "Platform"
                                                                  :pre-filter {:name platform-name
                                                                               :type "Platform"})))
                      (assoc db :activities-by-filter "error"))))))))))))

(rf/reg-event-db
  :filter-activities-by-selected-terms
  (fn [db [_ selected-filters]]
    (let [activities (:activities db)
          set-path (fn [path]
                    (set! (.-location js/window) (str "/#/" path)))
          pre-filter (conj '() (get-in db [:activities-by-filter :pre-filter]))
          filtered-act (filter (fn [a]
                                 (every? true?
                                         (map
                                           (fn [t]
                                             (case (:type t)
                                               "Branch" (some #(= (:name %) (:name t)) (get-in a [:fields :branches]))
                                               "Platform" (= (:name t) (get-in a [:fields :platform :name]))
                                               "Tag" (some #(= (:name %) (:name t)) (get-in a [:fields :tags]))))
                                           (remove nil? (concat pre-filter selected-filters)))))
                               activities)]
      (if (and (empty? selected-filters)
               (every? nil? pre-filter))
        (assoc db :active-view :branches-view
                  :activities-by-filter nil)
        (assoc db :activities-by-filter (hash-map :filter-type "Multiple"
                                                  :display-name (clojure.string/join ", " (map #(:name %)
                                                                                               (if (empty? selected-filters)
                                                                                                 pre-filter
                                                                                                 (remove nil? (concat pre-filter selected-filters)))))
                                                  :activities filtered-act
                                                  :filters selected-filters
                                                  :pre-filter (get-in db [:activities-by-filter :pre-filter]))
                  :active-view :filtered-activities-view)))))

(rf/reg-event-db
  :set-filter-bar-terms
  (fn [db _]
    (let [{:keys [activity-branches activity-platforms tags filter-bar-terms]} db]
        (if (nil? filter-bar-terms)
          (as-> '() ts
            (conj ts (map #(hash-map :name (:name %)
                                     :type "Branch"
                                     :checked false)
                          activity-branches))
            (conj ts (map #(hash-map :name (:name %)
                                     :type "Platform"
                                     :checked false)
                          activity-platforms))
            (conj ts (map #(hash-map :name (:name %)
                                     :type "Tag"
                                     :checked false)
                          tags))
            (distinct (apply concat ts))
            (assoc db :filter-bar-terms (shuffle ts)))
          db))))
