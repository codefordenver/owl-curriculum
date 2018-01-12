(ns owlet.views.klipse-activity-scroll
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]))

(defn klipse-activity-scroll-view []
  [:div.activity
   [:div.activity-wrap
    [:div.activity-header.col-xs-12
     [activity-title "Intro to Python" "mslim"]]
    [:div.activity-content.col-xs-12.col-lg-7
     [:div.activity-info-wrap.box-shadow
        [:h2 "1. Type this code in the evaluator below:"]
        [:div.code "print(\"Hello world!\")"]
        [klipse-component "Python" "# type here"]
        [:p [:b "Fun fact: "]
            "Printing \"Hello world!\" to the screen as your first program in a new language is considered good luck ~(˘▾˘)~"]
        [:p [:b "Values in quotes are called strings. "]
            "\"Hello world!\" is a string."]]
     [:div.activity-info-wrap.box-shadow
        [:h2 "2. For this one, just copy & paste…"]
        [:div.code "print(\"./\u005C_/\\\u00A0\u00A0\u00A0\u00A0\u00A0-\u00A0-\u00A0-\\n(\u00A0'\u00A0'\u00A0)\u00A0\u00A0/\u00A0Hello\u00A0\\\u00A0\\n(\u00A0\u00A0\u00A0\u00A0\u00A0)\u00A0<\u00A0\u00A0World!\u00A0|\u00A0\\n.|\u00A0|\u00A0|\u00A0\u00A0\u00A0\\\u00A0\u00A0\u00A0*\u00A0\u00A0\u00A0/\\n(__|__)\u00A0\u00A0\u00A0\u00A0-\u00A0-\u00A0-\")"]
        [klipse-component "Python" "# type here"]
        [:p "Based on the output, "
            [:b "how does the program know to start a new line?"]]]
     [:div.activity-info-wrap.box-shadow
        [:h2 "3. Your turn! Write a single print statement that returns this OUTPUT:"]
        [:div.code "Hello world!"
             [:br] "Hello world!"
             [:br] "Hello world!"]
        [klipse-component "Python" "# type here"]]
     [:div.activity-info-wrap.box-shadow
        [:h2 "4. Now let’s do some math. Python is very good at math…"]]]]])
