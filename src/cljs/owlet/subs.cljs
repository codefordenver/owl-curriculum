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
    (let [branches (:activity-branches db)
          platforms (:activity-platforms db)
          tags (:tags db)
          filter-bar-terms ()]
      (as-> filter-bar-terms ts
        (conj ts (map #(hash-map :name (:name %)
                                 :type "Branch")
                      branches))
        (conj ts (map #(hash-map :name (:name %)
                                 :type "Platform")
                      platforms))
        (conj ts (map #(hash-map :name (:name %)
                                 :type "Tag")
                      tags))
        (apply concat ts)))))
