(ns lightfeathercc.views
  (:require [clojure.data.json :as json]
            [clojure.string :as s])
  (:use [hiccup core page]))

(defn format-swimlane [swimlane]
  (let [split (s/split  swimlane #"-")
        cap (doall (map s/capitalize split))]
    (s/join "" cap)))

(defn filter-tickets [tickets swimlane]
  "Retrieves all tickets from a given swimlane"
  (filter (fn [t]
            (= (:swimlane t) swimlane))
          tickets))

(defn header []
  [:head
    [:title "Lightfeather Coding Challenge"]
    [:script {:src "https://code.jquery.com/jquery-3.5.1.min.js"}]
    [:script {:src "/js/script.js"}]
    (include-css "/css/style.css")])

(defn kanban-title []
  [:h1 {:class "kanban-title"} "Kanban Challenge"])

(defn swimlane [title tickets color]
  [:div {:id (s/replace title #" " "")
         :class "swimlane"}
    [:div {:class "swimlane-header"
           :style (str "background-color: " color ";")}
          title]
    [:div {:class "swimlane-body"
           :style (str "background-color: " color ";")}
      [:div {:class "tickets-container"} tickets]
      (if (= title "Backlog")
        [:div {:id "add-card"} "+ Add Card"])]])

(defn ticket [{ticket-id :id
               description :description
               date :date
               author :author
               swimlane :swimlane}]
  [:div {:id ticket-id
         :class "ticket-card"
         :swimlane (format-swimlane (name swimlane))}
    [:div {:class "ticket-navigation-left"} "<"]
    [:div {:class "ticket-content"}
      [:div {:class "ticket-description"} description]
      [:div {:class "ticket-date"} date]
      [:div {:class "ticket-author"} author]]
    [:div {:class "ticket-navigation-right"} ">"]])

(defn kanban [{next-id :next-id
               swimlanes :swimlanes
               tickets :tickets}]
  (html5
    (header)
    [:body
      (kanban-title)
      [:div {:class "content-container"}
        (let [backlog-tickets (filter-tickets tickets :backlog)]
          (swimlane "Backlog" (doall (map ticket backlog-tickets)) "gray"))
        (let [in-progress-tickets (filter-tickets tickets :in-progress)]
          (swimlane "In Progress" (doall (map ticket in-progress-tickets)) "blue"))
        (let [complete-tickets (filter-tickets tickets :complete)]
          (swimlane "Complete" (doall (map ticket complete-tickets)) "green"))
      ]]))
