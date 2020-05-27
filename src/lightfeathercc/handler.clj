(ns lightfeathercc.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.data.json :as json]
            [java-time :as jt]
            [lightfeathercc.views :as views]
            [lightfeathercc.cipher :as cipher]))

; ----- Load in configuration file.
(def config-path "./resources/public/config.json")
(def config (json/read-str (slurp config-path)
                           :key-fn (fn [k] (keyword (clojure.string/lower-case k))))) ;TODO: fix this with arrow

; ----- Create datastore and state manipulation functions.
(def datastore (atom {:next-id 0
                      :swimlanes #{:backlog :in-progress :complete}
                      :tickets []}))

(defn current-time []
  (let [t (jt/local-date)]
    (jt/format "yyyy/MM/dd" t)))

(defn save-state []
  "Writes out the current state to a file."
  (let [state (json/write-str @datastore)
        encoded-state (cipher/encode state (:shift config))]
    (println "Writing current state to disk.")
    (with-open [w (clojure.java.io/writer (:loads config) :append false)]
      (.write w encoded-state))))

(defn load-state []
  "Loads the state from a file if the file exists. If it does not, it saves
   the current state."
  (if (.exists (clojure.java.io/as-file (:loads config)))
    (let [encoded-state (slurp (:loads config))
          state-str (cipher/decode encoded-state (:shift config))
          state (json/read-str
                   state-str
                   :key-fn keyword
                   :value-fn (fn [k v]
                                 (if (= :swimlane k)
                                     (keyword v)
                                     v)))]
      (reset! datastore state))
    (save-state)))

(defn add-ticket [description date author]
  "Adds a ticket to the datastore"
  (let [current-datastore @datastore
        next-id (:next-id current-datastore)
        current-tickets (:tickets current-datastore)]
    ; Add the new ticket to the datastore
    (swap! datastore assoc-in [:tickets] (conj current-tickets {:id (str "ticket-" next-id)
                                                                  :description description
                                                                  :date date
                                                                  :author author
                                                                  :swimlane :backlog}))
    ; Increment the unique id counter.
    (swap! datastore assoc-in [:next-id] (inc next-id))))

(defn get-ticket [ticket-id]
  "returns a ticket with the given id"
  (let [current-datastore @datastore
        current-tickets (:tickets current-datastore)]
    (loop [index 0]
      (if (= (:id (current-tickets index)) ticket-id)
        {:index index :ticket (current-tickets index)}
        (recur (inc index))))))

(defn move-ticket [ticket-id swimlane]
  "Updates the swimlane location of the given ticket"
  (let [current-datastore @datastore
        swimlanes (:swimlanes current-datastore)
        {index :index} (get-ticket ticket-id)]
    (if (contains? swimlanes swimlane)
      (swap! datastore assoc-in [:tickets index :swimlane] swimlane)
      (throw (Exception. "The provided swimlane does not exist.")))))


; ----- Test that the functions are working properly.
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(add-ticket "a fun new ticket" (current-time) "Alexander Maricich")
(move-ticket "ticket-0" :in-progress)
(move-ticket "ticket-1" :in-progress)
(move-ticket "ticket-2" :in-progress)
(move-ticket "ticket-3" :in-progress)
(move-ticket "ticket-4" :in-progress)
(move-ticket "ticket-5" :in-progress)
(move-ticket "ticket-6" :complete)

; ----- Try to load the previous state if one exists.
(load-state)

; ----- Define routes and route specific functionality.
(defn current-state []
  (json/write-str @datastore))
(defn new-task [{title :title
                 description :description} data]
  "new-task")
(defn update-swimlane [{ticket-id :ticket-id
                        swimlane :swimlane} data]
  "update-swimlane")

(defroutes app-routes
  (GET "/" [] (views/kanban @datastore))
  (GET "/api/kanban" [] (current-state))
  (PUT "/api/kanban" [data] (new-task data))
  (POST "/api/kanban" [data] (update-swimlane data))
  (route/not-found "Not Found"))

; ----- Launch the webserver.
(def app
  (wrap-defaults app-routes site-defaults))
