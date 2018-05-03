(ns owlet.components.creation.general-activity-text-fields
  (:require [reagent.core :as reagent]
            [owlet.components.creation.markdown-editor :refer [simplemde]]
            [owlet.events.create-activity :as create]))


(defn general-activity-text-fields []
  (reagent/create-class
    {:component-did-mount
     (fn []
       (simplemde :why :blur ::create/text)
       (simplemde :prereqs :blur ::create/text)
       (simplemde :materials :blur ::create/text))

     :reagent-render
     (fn []
       [:div#general-activity-info
        [:h5 [:mark "Summary"]]
        [:textarea#summary {:maxLength "255"
                            :rows "3"
                            :placeholder "In a nutshell, what will we be doing? (255 character limit)"}]
        [:h5 [:mark "Why?"]]
        [:textarea#why {:placeholder "Why is this important? (optional)"}]
        [:h5 [:mark "Pre-requisites"]]
        [:textarea#prereqs {:placeholder "What concepts or tools should we already know? Link to other Owlet activities or lessons if applicable. (optional)"}]
        [:h5 [:mark "Materials"]]
        [:textarea#materials {:placeholder "Materials, resources, and/or handouts needed. (optional)"}]])}))
