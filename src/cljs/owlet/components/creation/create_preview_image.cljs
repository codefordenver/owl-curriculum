(ns owlet.components.creation.create-preview-image
  (:require [owlet.components.back :refer [back]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box modal-panel button alert-box progress-bar]]))

;TODO: add functionality for uploading to Contentful

(def show-upload? (r/atom false))

(defn upload-form []
  (let [upload-error (r/atom nil)
        progress-pct (r/atom 0)]
    (fn []
      [:form
       [:b "Upload an image file"]
       [:br]
       [:br]
       [:input#upload-input-id {:type "file"}]
       [:br]
       [:br]
       [:button
        {:class "btn btn-primary"
         :type  "button"
         :on-click nil}
        "UPLOAD "
        [:span.fa.fa-upload]]
       [:br]
       [:br]

       (if @upload-error
         [alert-box                 ; Have error. Show its message.
          :alert-type :warning
          :body       @upload-error
          :padding    "12px"]
         [:div                      ; No error. Show progress bar.
          [:br]
          [progress-bar
           :striped? true
           :model    progress-pct]])])))

(defn upload-modal []
  (fn []
    [v-box
     :children
     [[modal-panel
       :backdrop-color "grey"
       :backdrop-opacity 0.4
       :child
       [h-box
        :children
        [[upload-form]
         [button
          :class "btn-secondary"
          :label "x"
          :on-click nil]]]]]]))

(defn create-preview-image []
  [:div
   [:h5 [:mark "Preview Image"]]
   [:button.add-validation {:on-click #(swap! show-upload? not)
                            :style {:margin "10px 0"
                                    :font-size "1em"}}
    "UPLOAD"]
   (when @show-upload?
     [upload-modal])])
