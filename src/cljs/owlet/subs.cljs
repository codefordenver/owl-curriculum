(ns owlet.subs
  (:require [re-frame.core :as rf]
            [owlet.config :as config]
            [owlet.rf-util :refer [reg-getter]]))


(reg-getter :active-view [:active-view])

(reg-getter :my-id [:my-identity :firebase-id])

(reg-getter :showing-bg-img-upload [:showing-bg-img-upload])

(reg-getter :my-background-image-url [:my-identity :private :background-image-url])

(reg-getter :my-roles [:my-identity :private :roles])

(reg-getter :activity-branches [:activity-branches])

(reg-getter :activities-by-branch [:activities-by-branch])

(reg-getter :activities-by-filter [:activities-by-filter])

(reg-getter :activity-in-view [:activity-in-view])

(reg-getter :loading-state [:app :loading?])

(reg-getter :app-title [:app :title])

(reg-getter :tags [:tags])

(reg-getter :activity-titles [:activity-titles])

(reg-getter :activity-platforms [:activity-platforms])

(reg-getter :route-params [:app :route-params])

(reg-getter :subscriber-info [:app :route-opts])

(reg-getter :on-app-failure [:on-app-failure])

(rf/reg-sub
  :filter-bar-terms
  (fn [db _]
    (if (nil? (:activities-by-filter db))
      (:filter-bar-terms db)
      (let [activities-by-filter (:activities-by-filter db)
            filters (:display-name activities-by-filter)
            activity-branches (distinct (apply concat (map #(get-in % [:fields :branches])
                                                           (:activities activities-by-filter))))
            activity-platforms (distinct (map #(get-in % [:fields :platform])
                                              (:activities activities-by-filter)))
            activity-tags (distinct (apply concat (map #(get-in % [:fields :tags])
                                                       (:activities activities-by-filter))))
            branches (distinct (map #(hash-map :name (:name %) :type "Branch" :checked (if filters
                                                                                         (clojure.string/includes? filters (:name %))
                                                                                         false))
                                    activity-branches))
            platforms (distinct (map #(hash-map :name (:name %) :type "Platform" :checked (if filters
                                                                                            (clojure.string/includes? filters (:name %))
                                                                                            false))
                                     activity-platforms))
            tags (distinct (map #(hash-map :name (:name %) :type "Tag" :checked (if filters
                                                                                  (clojure.string/includes? filters (:name %))
                                                                                  false))
                                activity-tags))]
        (prn (concat branches platforms tags))
        (concat (:filters activities-by-filter)
                (filter (fn [t]
                          (some #(= t %) (concat branches platforms tags)))
                        (:filter-bar-terms db)))))))
