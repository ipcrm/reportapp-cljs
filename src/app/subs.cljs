(ns app.subs
  (:require [re-frame.core :as rf]))

;; -- Domino 4 - Query  -------------------------------------------------------
(rf/reg-sub
  :test
  (fn [db _]                                                 ;; db is current app state. 2nd unused param is query vector
    (println "I fired!")
    (:test db)))                                             ;; return a query computation over the application state

(rf/reg-sub
  :testdata
  (fn [db _]
    (println "I fired testdata!")
    (:testdata db)))

(rf/reg-sub
  :query-in-progress
  (fn [db _]
    (println "I fired query-in-progress!")
    (:query-in-progress db)))

(rf/reg-sub
  :active-panel
  (fn [db _]
    (:active-panel db)))

(rf/reg-sub
  :gql-submitted
  (fn [db _]
    (println "GQL Submitted" (:gql-submitted db))
    (:gql-submitted db)))

(rf/reg-sub
  :gql-details
  (fn [db _]
    (:gql-details db)))

(rf/reg-sub
  :value
  :<- [:gql-details]
  (fn [doc [_ path]]
    (get-in doc path)))
