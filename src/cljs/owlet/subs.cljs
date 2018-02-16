(ns owlet.subs
  (:require [re-frame.core :as rf]
            [owlet.config :as config]
            [owlet.rf-util :refer [reg-getter]]))


(rf/reg-sub
  :active-view
  (fn [db _]
    (:active-view db)))


(reg-getter :my-id [:my-identity :firebase-id])

(reg-getter :showing-bg-img-upload [:showing-bg-img-upload])

(reg-getter :my-background-image-url [:my-identity :private :background-image-url])

(reg-getter :my-roles [:my-identity :private :my-roles])

(rf/reg-sub
  :activity-branches
  (fn [db]
    (get-in db [:activity-branches])))

(rf/reg-sub
  :activities-by-branch
  (fn [db]
    (get-in db [:activities-by-branch])))

(rf/reg-sub
  :activities-by-filter
  (fn [db]
    (get-in db [:activities-by-filter])))

(rf/reg-sub
  :activity-in-view
  (fn [db]
    (get-in db [:activity-in-view])))

(rf/reg-sub
  :loading-state
  (fn [db]
    (get-in db [:app :loading?])))

(reg-getter :app-title [:app :title])

(reg-getter :tags [:tags])

(reg-getter :activity-titles [:activity-titles])

(reg-getter :activity-platforms [:activity-platforms])

(reg-getter :route-params [:app :route-params])

(reg-getter :subscriber-info [:app :route-opts])

(rf/reg-sub
  :on-app-failure
  (fn [db _]
    (:on-app-failure db)))
