(ns owlet.views.about
  (:require [owlet.components.email-notification :refer [email-notification]]
            [cljsjs.chartjs]
            [reagent.core :as reagent]
            [ajax.core :refer [GET]]
            [owlet.config :as config]))

(def stats-endpoint "/api/github/stats")

(def hide-labels? (reagent/atom (<= (.-innerWidth js/window) 800)))
(def prev-hidden? (reagent/atom @hide-labels?))

(defn create-chart [labels data]
  (let [ctx (.getContext (.getElementById js/document "chart") "2d")]
       (def chart (js/Chart.
                    ctx
                    (clj->js {:type "line"
                              :data {:labels labels
                                     :datasets [{:label "Commits"
                                                 :backgroundColor "rgba(220, 0, 0, 1)"
                                                 :borderColor "rgba(220, 0, 0, 1)"
                                                 :data data
                                                 :fill false
                                                 :lineTension 0
                                                 :pointRadius 0}]}
                              :options {:scaleShowValues true
                                        :scales {:xAxes [{:ticks {:autoSkip false}}]}}})))))

(defn handle-stats [response]
  (let [res (js->clj (clj->js response) :keywordize-keys true)
        all (get-in res [:body :labels])
        reduced (get-in res [:body :reduced-labels])
        data (get-in res [:body :totals])]
    (set! (.-onresize js/window) (fn []
                                   (let [w (.-innerWidth js/window)]
                                     (reset! hide-labels? (<= w 800))
                                     (when (not= @prev-hidden? @hide-labels?)
                                       (reset! prev-hidden? (<= w 800))
                                       (set! (-> chart .-data .-labels .-length) 0)
                                       (if (true? @hide-labels?)
                                         (doseq [l reduced]
                                           (.push (-> chart .-data .-labels) l))
                                         (doseq [l all]
                                           (.push (-> chart .-data .-labels) l)))
                                       (.update chart)))))
    (if @hide-labels?
      (create-chart reduced data)
      (create-chart all data))))

(defn about-view []
  (reagent/create-class
    {:component-did-mount
      (fn []
        (GET stats-endpoint {:handler handle-stats
                             :format :json}))
     :reagent-render
     (fn []
       [:div.information-wrap
         [email-notification]
         [:div.information-inner-wrap.col-xs-12.col-lg-8
          [:div.information-inner
           [:h1 [:mark "What is Owlet?"]]
           [:h3 "Free, self-guided projects for creative learning"]
           [:p "Owlet is a collection of self-guided activities designed for middle school students to learn technology skills through creative exploration. It's free and available to everyone, especially teachers looking for new ideas and students (of any age) looking to learn."]
           [:p "We originally developed this platform to meet the specific needs of a small group of new technology teachers working in Denver Public Schools (DPS) via OpenWorld Learning (OWL), a local non-profit. We set out to create a platform that addressed their particular challenges in terms of curriculum content creation, delivery, accessibility, and keeping middle school   students engaged (no small feat)."]
           [:p "From the beginning, Owlet has been heavily shaped by feedback from teachers, and most importantly, the students. We are also "
            [:a {:href "https://blog.mmmanyfold.com/tagged/owlet"} "inspired by powerful ideas"]
            " from computer scientists/educators such as Alan Kay, Simon Papert (constructivism), Kathy Sierra, Linda Liukas, Ted Nelson, Elon Musk, Tim Bell, etc… the list goes on. "]
           [:br]
           [:img {:src "../img/logo-cfd.png"
                  :width "40%"}]
           [:p "This project is independently produced and maintained by a team of Code for Denver volunteers, who have been developing it since March 2016. It’s open source and "
            [:a {:href "https://github.com/codefordenver/owlet"} "available on GitHub"] "."][:br]
           [:canvas#chart]
           [:img {:src "../img/logo-contentful.png"
                  :width "37%"}]
           [:p "Special thanks to "
            [:a {:href "https://www.contentful.com/"} "Contentful"]
            " for generously providing us with a pro bono subscription plan! We love Contentful's developer-friendly content management APIs and optimized microservices architecture for creating and delivering digital content."]]]
         [:div.information-inner-wrap.col-xs-12.col-lg-4
          [:div.information-inner.contributors
           [:h3 [:mark "Contributors"]]
           [:h4 "Project Leads:"]
           "Michelle Lim"[:br]
           "David Viramontes"[:br][:br]
           [:h4 "Software Development:"]
           "David Viramontes"[:br]
           "Michelle Lim"[:br]
           "Tyler Perkins"[:br]
           "Zaden Ruggiero-Boune"[:br][:br]
           [:h4 "Graphic Design:"]
           "Trinh Nguyen"[:br][:br]
           [:h4 "Content:"]
           "Michelle Lim"[:br]
           "Katie Frank"[:br]
           "Tony Gilbert-Davis"[:br][:br][:br]
           [:h3 [:mark "Contact"]]
           [:p "Michelle Lim" [:br]
            [:a {:href "mailto:elle@mmmanyfold.com"} "elle@mmmanyfold.com"]]]]])}))
