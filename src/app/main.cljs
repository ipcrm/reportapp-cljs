(ns app.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [re-graph.core :as re-graph]
            [app.utils :as utils]
            [app.config :as config]
            [app.components :as comp]
            ))

(def thequery "query GetBuildData { Build (orderBy: [timestamp_desc], first: 1000){ id timestamp finishedAt startedAt status push { after { sha } } repo { name owner } }}")


;; -- Domino 2 - Event Handlers -----------------------------------------------
(rf/reg-event-db                                            ;; sets up initial application state
 :initialize                                                ;; usage:  (dispatch [:initialize])
 (fn [_ _]                                                  ;; the two parameters are not important here, so use _
   {:testdata "" :test "saweet"}
   )                                                        ;; What it returns becomes the new application state
 )

(rf/reg-event-db
 :update-data
 (fn [db [_ new-data]]
   (assoc db :test (str new-data (:test db)))))

(rf/reg-event-db
 :clear-data
 (fn [db _]
   (assoc db :test "")))

(rf/reg-event-db
 :retrieve-gql-data
 (fn [db [_ new-data]]
   (print new-data)
   (assoc db :testdata new-data))
 )

;; -- Domino 4 - Query  -------------------------------------------------------
(rf/reg-sub
 :test
 (fn [db _]                                                 ;; db is current app state. 2nd unused param is query vector
   (println "I fired!")
   (:test db)))                                             ;; return a query computation over the application state

(rf/reg-sub
 :testdata
 (fn [db _]                                                 ;; db is current app state. 2nd unused param is query vector
   (println "I fired testdata!")
   (:testdata db)))                                         ;; return a query computation over the application state

;; -- Domino 5 - View Functions ----------------------------------------------
(defn fake []
  [:div.test
   [:h1 @(rf/subscribe [:test])]
   ])

;; I've moved the testdata subscription into the root component
;; This really is a reagent component and so it will take care of re-rendering
;; any components where there props change
;; In the example below, chart-data are "props" of the "build-summary-component" component
;; reagent knows that chart-data has been updated so it will re-render the build-summary-component
;; any time this data changes
(defn ui []
  (let [chart-data @(rf/subscribe [:testdata])]
    [:div.foo
     [:h1 "Hello world, it is now"]
     ;; reagent-style react component - will re-render when ratoms from a subscribe change
     [fake]
     [:button {:type "button" :on-click #(rf/dispatch [:update-data "newdatagoeshere"])} "Click Me!"]
     [:button {:type "button" :on-click #(rf/dispatch [:clear-data])} "Clear Me!"]
     [:button {:type "button" :on-click #(rf/dispatch [::re-graph/query thequery {} [:retrieve-gql-data]])} "GQL Data!"]
     ;; native react component (ratoms from a subscription will not re-render when data changes)
     [app.components/build-summary-component chart-data]
     ]))

(defn test1
  [a b]
  (+ a b))

;; -- Entry
(defn render
  []
  (reagent/render [ui]
                  (js/document.getElementById "app"))
  )

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])                          ;; put a value into application state
  (rf/dispatch
   [::re-graph/init
    {
     :ws-url nil
     :http-url (app.config/get-api-url)                                           ;;TODO: Put    ;; override the http url (defaults to /graphql)
     :http-parameters {:with-credentials? false             ;; any parameters to be merged with the request, see cljs-http for options
                       :oauth-token (app.config/get-api-key)}}])                  ;; TODO: Put this in a config file
  (render))                                                 ;; mount the application's ui into '<div id="app" />'

