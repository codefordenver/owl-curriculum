(ns owlet.views.about
  (:require [owlet.components.email-notification :refer [email-notification]]))

(defn about-view []
  (fn []
    [:div.information-wrap
      [email-notification]
      [:div.information-title-wrap
        [:h1 [:mark.white "What is Owlet?"]]]
      [:div.information-inner-wrap.col-xs-12.col-lg-8
       [:div.information-inner
        [:h3 "Free, self-guided projects for creative learning"]
        [:p "Owlet is a collection of self-guided activities designed for middle school students to learn technology skills through creative exploration. It's free and available to everyone, especially teachers looking for new ideas and students (of any age) looking to learn."]
        [:p "We originally developed this platform to meet the specific needs of a small group of new technology teachers working in Denver Public Schools (DPS) via OpenWorld Learning (OWL), a local non-profit. We set out to create a platform that addressed their particular challenges in terms of curriculum content creation, delivery, accessibility, and keeping middle school   students engaged (no small feat)."]
        [:p "From the beginning, Owlet has been heavily shaped by feedback from teachers, and most importantly, the students. We are also "
         [:a {:href "https://blog.mmmanyfold.com/tagged/owlet"} "inspired by powerful ideas"]
         " from computer scientists/educators such as Alan Kay, Simon Papert (constructivism), Kathy Sierra, Linda Liukas, Ted Nelson, Elon Musk, Tim Bell, etc… the list goes on. "]
        [:p "This project is independently produced and maintained by a team of Code for Denver volunteers, who have been developing it since March 2016. It’s open source and "
         [:a {:href "https://github.com/codefordenver/owlet"} "available on GitHub"] "."][:br]
        [:h3 "Contact"]
        [:p "Michelle Lim" [:br]
         [:a {:href "mailto:elle@mmmanyfold.com"} "elle@mmmanyfold.com"]]]]
      [:div.information-inner-wrap.col-xs-12.col-lg-4
       [:div.information-inner
        [:h3 "Contributors"]
        [:h4 "Project Leads"]
        "Michelle Lim"[:br]
        "David Viramontes"[:br][:br]
        [:h4 "Software Development"]
        "David Viramontes"[:br]
        "Michelle Lim"[:br]
        "Tyler Perkins"[:br]
        "Zaden Ruggiero-Boune"[:br][:br]
        [:h4 "Graphic Design"]
        "Trinh Nguyen"[:br][:br]
        [:h4 "Content"]
        "Michelle Lim"[:br]
        "Katie Frank"[:br]
        "Tony Gilbert-Davis"[:br][:br]]]]))
