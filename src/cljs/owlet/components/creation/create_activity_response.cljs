(ns owlet.components.creation.create-activity-response
  (:use-macros [purnam.core :only [! def*]])
  (:require cljsjs.toastr))

(def* toastr-opts
      {:positionClass "toast-bottom-full-width"
       :timeOut       false})

(defn create-activity-response-component [response]
  (let [_ (! js/toastr.options toastr-opts)
        {:keys [show?]} {:show? false}]
    (when show?
      (if (= response :ok)
        (js/toastr.success (str "Success: Your activity has been submitted"))
        (js/toastr.error (str "Error: "))))
    [:div.error]))
