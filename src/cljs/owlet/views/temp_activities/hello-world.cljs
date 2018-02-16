(ns owlet.views.temp-activities.hello-world
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [owlet.components.activity.title :refer [activity-title]]
            [owlet.components.back :refer [back]]))

(defn temp-hello-world-view []
  [:div.activity
   [:div.activity-wrap
    [:div.activity-header.col-xs-12
     [activity-title "Hello world!" "mslim"]]
    [:div.activity-content.col-xs-12.col-lg-7
     [:div.activity-info-wrap
        [:h2 "1. Type this code below:"]
        [:div.code-block "print(\"Hello world!\")"]
        [klipse-component "Python" "# type here"]
        [:h5 [:b "Fun fact: "]
            "Printing \"Hello world!\" to the screen as your first program in a new language is considered good luck ~(˘▾˘)~"]
        [:h5 [:b "Values in \"quotes\" are called "
              [:i "strings. "]]
            "\"Hello world!\" is a string."]]
     [:div.activity-info-wrap
        [:h2 "2. For this one, just copy & paste:"]
        [:div.code-block "print(\"./\u005C_/\\\u00A0\u00A0\u00A0\u00A0\u00A0-\u00A0-\u00A0-\\n(\u00A0'\u00A0'\u00A0)\u00A0\u00A0/\u00A0Hello\u00A0\\\u00A0\\n(\u00A0\u00A0\u00A0\u00A0\u00A0)\u00A0<\u00A0\u00A0World!\u00A0|\u00A0\\n.|\u00A0|\u00A0|\u00A0\u00A0\u00A0\\\u00A0\u00A0\u00A0*\u00A0\u00A0\u00A0/\\n(__|__)\u00A0\u00A0\u00A0\u00A0-\u00A0-\u00A0-\")"]
        [klipse-component "Python" "# paste here"]
        [:h5 "This time, the output has multiple lines. Look closely at the original print statement. "
            [:b "How does it tell the program to start a new line?"]]]
     [:div.activity-info-wrap
        [:h2 "3. Using "
             [:span.inline-code "\\n"]
             ", complete the print statement below to create this output:"]
        [:div.code-block "Hello world!"
             [:br] "Hello world!"
             [:br] "Hello world!"]
        [klipse-component "Python" "# fill in the quotes\nprint(\"\")"]]
     [:div.activity-info-wrap
        [:h2 "4. Now let’s do some math…"]
        [:h5 [:b "Basic Operators:"]]
        [:ul
         [:li "Add: " [:span.inline-code "2 + 3"]]
         [:li "Subtract: " [:span.inline-code "5 - 1"]]
         [:li "Multiply: " [:span.inline-code "2 * 3"]]
         [:li "Divide: " [:span.inline-code "40 / 2"]]
         [:li "Exponent: " [:span.inline-code "2 ** 3"]]]
        [klipse-component "Python" "# print the rest below\nprint(2 + 3)\nprint(5 - 1)"]
        [:h5 [:b "Operations can be nested using parentheses:"]]
        [klipse-component "Python" "# try replacing 5 with (2 + 3)\nprint((1 + 3) * 5)"]]
     [:div.activity-info-wrap
        [:h2 "5. Strings can also be added"]
        [:h5 [:b "Edit the code below to add a space between \"Hello\" and \"world\":"]]
        [klipse-component "Python" "print(\"Hello\" + \"world!\")"]]
     [:div.activity-info-wrap
        [:h2 "6. Strings can be multiplied, too!"]
        [:h5 [:b "Edit the code below to make it say \"Hello\" ten times:"]]
        [klipse-component "Python" "print(\"Hello\" * 3)"]]
     [:div.activity-info-wrap
        [:h2 "7. Rewrite this print statement to get the same output, using less code:"]
        [klipse-component "Python" "print(\"Hello world!\\nHello world!\\nHello world!\")" true]
        [:h5 "Use " [:i "multiplication "] "here to stay \""
             [:b "DRY"]
             "\"— aka: "
             [:b "D"]
             "on’t "
             [:b "R"]
             "epeat "
             [:b "Y"]
             "ourself."
             [:br] "Yes, "
             [:a {:href "https://en.wikipedia.org/wiki/Don%27t_repeat_yourself"
                  :target "_blank"}
                 "this is a real thing 😎"]
             [:br]"—a very important principle of programming."]]
     [:div.activity-info-wrap
        [:h1 "⚡⚡Challenges⚡⚡"]
        [:h3 "1. Print your full name"]
        [klipse-component "Python" "# type here"][:br]
        [:h3 "2. Print 10 lines with 10 ampersands(&) in each row, like this:"]
        [:div.code-block
         "&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"
         [:br]"&&&&&&&&&&"]
        [klipse-component "Python" "# type here"][:br]
        [:h3 "3. Print this sketch:"]
        [:div.code-block
         "..__ __ __ __ __"[:br]
         "./__/__/__/__/__/|"[:br]
         "/__/__/__/__/__/|/"[:br]
         "|__'__'__'__'__|/"]
        [klipse-component "Python" "# type here"]]]]])
