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
  (fn [db _]                                                 ;; db is current app state. 2nd unused param is query vector
    (println "I fired testdata!")
    (:testdata db)))                                         ;; return a query computation over the application state
