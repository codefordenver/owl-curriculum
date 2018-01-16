(ns owlet.components.lpsidebar
  (:require [owlet.components.login :refer [login-component]]
            [reagent.core :as reagent]
            [owlet.helpers :refer [class-names]]
            [re-frame.core :as rf]))

(def lpsidebar-state (reagent/atom false))

(defn toggle-lpsidebar []
  (swap! lpsidebar-state not))

(defn lpsidebar-component []
  (fn []
    (let [position? (if @lpsidebar-state
                      "80px"
                      "0")]
      [:div
       (if @lpsidebar-state
        [:div.lpsidebar-overlay.hidden-md-up.opened-sidebar {:on-click #(toggle-lpsidebar)}]
        [:div.lpsidebar-overlay.hidden-md-up])
       [:div.lpsidebar-wrap.hidden-md-up
        [:div.lpsidebar {:style {:width position?}}
         [:div#owlet-logo-div
          [:a#owlet-image {:href "#/"
                           :on-click #(toggle-lpsidebar)}
           [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
         [:div.menu
          [:div.login {:on-click #(toggle-lpsidebar)}
           [login-component]]
          [:a.navigation.branch-icon {:href     "#/branches"
                                      :on-click #(toggle-lpsidebar)}
           [:div.branch-icon]]
          (when @(rf/subscribe [:my-id])
            [:a.navigation.settings-icon {:href "#/settings"
                                          :on-click #(toggle-lpsidebar)}
             [:div.settings-icon]])
          [:a.navigation.about-icon {:href     "#/about"
                                     :on-click #(toggle-lpsidebar)}
           [:div.about-icon]]]
         (if @lpsidebar-state
           [:img#lpsidebar-opened.lpsidebar-toggle.hidden-md-up {:style {:left position?}
                                                                 :src      "img/owlet-tab-opened.png"
                                                                 :on-click #(toggle-lpsidebar)}]
           [:img#lpsidebar-closed.lpsidebar-toggle.hidden-md-up {:style {:left position?}
                                                                 :src      "img/owlet-tab-closed.png"
                                                                 :on-click #(toggle-lpsidebar)}])]]])))
