(ns lightfeathercc.views
  (:require [clojure.data.json :as json])
  (:use [hiccup core page]))

(defn filter-tickets [tickets swimlane]
  "Retrieves all tickets from a given swimlane"
  (filter (fn [t]
            (= (:swimlane t) swimlane))
          tickets))

(defn header []
  [:head
    [:title "Lightfeather Coding Challenge"]
    (include-css "/css/style.css")])

(defn kanban-title []
  [:h1 {:class "kanban-title"} "Kanban Challenge"])

(defn swimlane [title tickets]
  [:div {:id title
         :class "swimlane"}
    [:div {:class "swimlane-header"}
          title]
    [:div {:class "swimlane-body"}
          tickets]
    (if (= title "Backlog")
      [:div {:id "add-card"} "+ Add Card"])])

(defn ticket [{ticket-id :ticket-id
               description :description
               date :date
               author :author}]
  [:div {:id ticket-id
         :class "ticket-card"}
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
      [:p (json/write-str tickets)]

      (let [backlog-tickets (filter-tickets tickets "backlog")]
        (swimlane "Backlog" (doall (map ticket backlog-tickets))))
      (let [in-progress-tickets (filter-tickets tickets "in-progress")]
        (swimlane "In Progress" (doall (map ticket in-progress-tickets))))
      (let [complete-tickets (filter-tickets tickets "complete")]
        (swimlane "Complete" (doall (map ticket complete-tickets))))

      ]))
