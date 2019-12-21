(ns app.events
  (:require
    [re-frame.core :as rf]))
;; -- Domino 2 - Event Handlers -----------------------------------------------
(rf/reg-event-db                                            ;; sets up initial application state
  :initialize                                                ;; usage:  (dispatch [:initialize])
  (fn [_ _]                                                  ;; the two parameters are not important here, so use _
    {:testdata ""
     :test "default data"
     :active-panel 0
     :query-in-progress false
     :gql-details {}
     :gql-submitted false}))

(rf/reg-event-db
  :update-data
  (fn [db [_ new-data]]
    (assoc db :test (str new-data (:test db)))))

(rf/reg-event-db
  :clear-testdata
  (fn [db _]
    (assoc db :testdata "")))

(rf/reg-event-db
  :clear-data
  (fn [db _]
    (assoc db :test "")))

(rf/reg-event-db
  :query-in-progress
  (fn [db [_ newdata]]
    (assoc db :query-in-progress newdata)))

(rf/reg-event-db
  :gql-submitted
  (fn [db [_ value]]
    (assoc db :gql-submitted value)))

(rf/reg-event-db
  :retrieve-gql-data
  (fn [db [_ new-data]]
    (assoc db :testdata new-data))
  )

(rf/reg-event-db
  :set-active-panel
  (fn [db [_ value]]
    (assoc db :active-panel value)))

(rf/reg-event-db
  :set-value
  (fn [db [_ path value]]
    (assoc-in db (into [:gql-details] path) value)))

(rf/reg-event-db
  :update-value
  (fn [db [_ f path value]]
    (update-in db (into [:gql-details] path) f value)))