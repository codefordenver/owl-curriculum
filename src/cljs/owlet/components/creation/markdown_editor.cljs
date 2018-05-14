(ns owlet.components.creation.markdown-editor
  (:require cljsjs.simplemde
            [re-frame.core :as rf]))


(defn simplemde
  "Creates and returns a new Markdown editor, attaching it to the DOM element
  indicated by textarea-id. It makes use of an instance of CodeMirror to do the
  editing. When an event of the given type is fired by the instance, it will be
  handled by firing a re-frame event with the given rf-evt-id. The remaining
  members of the event vector will then be the current text in the editor, the
  textarea-id, and any more event arguments you provide. For standard values of
  codemirror-evt-type, see https://codemirror.net/doc/manual.html#events
  The textarea-id and codemirror-evt-type arguments may be provided as
  keywords, strings, or symbols. The remaining arguments will be literally
  passed to the event vector.
  "
  [textarea-id codemirror-evt-type rf-evt-id & more-evt-args]
  (let [area-id (name textarea-id)

        mde (js/SimpleMDE. (clj->js
                             {:element (js/document.getElementById area-id)
                              :forceSync true
                              :lineWrapping true
                              :autosave {:enabled true :uniqueId area-id}}))

        listener (fn [_js-evt]
                   (let [users-text (.value mde)
                         rf-event (into [rf-evt-id users-text textarea-id]
                                        more-evt-args)]
                     (rf/dispatch rf-event)))]
    (-> mde
      (goog.object/get "codemirror")   ; Adv. optimization munges .-codemirror
      (.on (name codemirror-evt-type) listener))

    mde))

