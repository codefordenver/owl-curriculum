(ns owlet.components.activity.embed
  (:require-macros [purnam.core :refer [?]])
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [re-frame.core :as rf]
            [clojure.string :refer [lower-case]]
            [reagent.core :as reagent]))

(def indexh (reagent/atom 0))
(def valid? (reagent/atom false))

(def klipse-container-class ".klipse-container")

(add-watch valid? :is-valid
  (fn [key atom old-state new-state]
    (let [iframe (js/document.querySelector "#klipseSlides")]
      (.postMessage (.-contentWindow iframe) (.stringify js/JSON (clj->js {:method "configure"
                                                                           :args [{:controls new-state}]})) "*"))))

(defn handle-eval [e]
  (let [activity @(rf/subscribe [:activity-in-view])
        output (clojure.string/trim (clojure.string/replace e.detail.resultElement.display.wrapper.innerText "OUTPUT:" ""))
        input (? e.detail.srcCode)
        unacceptable-inputs (get-in activity [:fields :codeValidation (keyword (str @indexh)) :unacceptableInputs])]
    (if-let [expected-output (get-in activity [:fields :codeValidation (keyword (str @indexh)) :expectedOutput])]
      (reset! valid? (and (= output expected-output)
                          (not (some #(= % input) unacceptable-inputs))))
      (reset! valid? true))))

(defn handle-slide-change [e]
  (when (> (.-newIndexh e) @indexh)
    (if @valid?
      (let [activity @(rf/subscribe [:activity-in-view])]
        (reset! indexh (.-newIndexh e))
        (let [prev-expected-output (get-in activity [:fields :codeValidation (keyword (str (- @indexh 1)) :expectedOutput)])]
          (if-let [new-expected-output (get-in activity [:fields :codeValidation (keyword (str @indexh)) :expectedOutput])]
            (reset! valid? (= prev-expected-output new-expected-output))
            (reset! valid? true))))
      (.preventDefault e))))

(defn activity-embed [embed tags preview]
  (reagent/create-class
    {:component-did-mount
     (fn []
       (js/srcDoc.set (js/document.getElementById "klipseSlides"))
       (.addEventListener js/document "klipse-snippet-evaled" handle-eval)
       (.addEventListener js/window "iframeslidewillchange" handle-slide-change))
     :reagent-render
     (fn []
       (let [preview-url (-> preview :sys :url)
             activity @(rf/subscribe [:activity-in-view])
             activity-type (get-in activity [:sys :contentType :sys :id])]
         [:div.activity-embed-wrap
          (if-not embed
            [:div.activity-preview
             [:img {:src preview-url :width "100%"}]]
            [:div.embed-container
             (case activity-type
               "activity" {"dangerouslySetInnerHTML"
                           #js{:__html embed}}
               "klipseActivity" [:iframe#klipseSlides {:srcDoc (get-in activity [:fields :iframeContent])
                                                       :frameBorder "0"
                                                       :scrolling "no"
                                                       :width "576"
                                                       :height "420"
                                                       :allowFullScreen true}])])
          (when tags
            [:div.activity-tags-wrap
             [:div.tags
               "TAGS: "]
             (for [tag tags :let [name (:name tag)]]
                 ^{:key (gensym "tag-")}
                 [:div.tag.inactive {:on-click #(rf/dispatch [:show-tag name])}
                   [:span name]])])]))}))
