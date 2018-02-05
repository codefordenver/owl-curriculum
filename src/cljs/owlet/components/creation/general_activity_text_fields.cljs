(ns owlet.components.creation.general-activity-text-fields
  (:require [reagent.core :as reagent]))

(defn general-activity-text-fields []
  (reagent/create-class
    {:component-did-mount
     (fn []
       (let [smde-why (js/SimpleMDE. #js {:element (js/document.querySelector "#why")
                                          :lineWrapping true
                                          :autosave #js {:enabled true
                                                         :uniqueId "why"}})
             smde-materials (js/SimpleMDE. #js {:element (js/document.querySelector "#materials")
                                                :lineWrapping true
                                                :autosave #js {:enabled true
                                                               :uniqueId "materials"}})
             smde-prereqs (js/SimpleMDE. #js {:element (js/document.querySelector "#prereqs")
                                              :lineWrapping true
                                              :autosave #js {:enabled true
                                                             :uniqueId "prereqs"}})]))
     :reagent-render
     (fn []
       [:div#general-activity-info
        [:h5 [:mark "Summary"]]
        [:input#summary {:type "text"
                         :maxLength "255"
                         :placeholder "In a nutshell, what will we be doing? (255 character limit)"}]
        [:h5 [:mark "Why?"]]
        [:textarea#why {:placeholder "Why is this important?"}]
        [:h5 [:mark "Pre-requisites"]]
        [:textarea#prereqs {:placeholder "What concepts or tools should we already know? Link to other Owlet activities or lessons if applicable."}]
        [:h5 [:mark "Materials"]]
        [:textarea#materials {:placeholder "Materials, resources, and/or handouts needed."}]])}))
