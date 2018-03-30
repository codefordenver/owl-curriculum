(ns owlet.components.creation.markdown-editor
  (:require cljsjs.simplemde))

(defn simplemde [id]
  (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" id))
                      :forceSync true
                      :lineWrapping true
                      :autosave #js {:enabled true
                                     :uniqueId id}}))
