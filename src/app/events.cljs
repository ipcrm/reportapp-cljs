(ns app.events
  (:require
    [camel-snake-kebab.core :as csk]
    [day8.re-frame.http-fx :as http-fx]
    [medley.core :as medley]
    [app.config :as config]
    [ajax.core :as ajax]
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

(rf/reg-event-fx
  ::graphql-fx-success
  (fn [_ [_ event response]]
    {:dispatch (conj event (update response :extensions #(medley/map-keys csk/->kebab-case %)))}))

(rf/reg-event-fx
  ::graphql-fx-failure
  (fn [_ [_ event response]]
    (when event
      {:dispatch (conj event response)})))

(defn ->request-map
  [{:keys [query variables on-success on-failure url token] :as request}]
  (print "TOKEN" token)
  {:method          :post
   :uri             url
   :headers         {"Authorization" (str "Bearer" " " token)} ;; Probably better ways to concat strings
   :params          (cond-> {:query query}
                            variables (assoc :variables variables))
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [::graphql-fx-success on-success]
   :on-failure      [::graphql-fx-failure on-failure]})

(defn graphql-effect
  [request]
  (let [seq-graphql-maps (if (sequential? request) request [request])]
    (http-fx/http-effect (map ->request-map seq-graphql-maps))))

(rf/reg-fx
  :graphql
  graphql-effect)

(rf/reg-event-fx
  :graphql-some-stuff
  [(rf/inject-cofx :gql-url) (rf/inject-cofx :gql-token)]
  (fn [cofx _]
    {:graphql [{:query config/thequery
                :url (:gql-url cofx)
                :token (:gql-token cofx)
                :on-success [::graphql-some-stuff-success]
                :on-failure [::graphql-some-stuff-failure]
                }]}))

(rf/reg-event-fx
  ::graphql-some-stuff-success
  (fn [_ [_ response]]
    (rf/dispatch [:retrieve-gql-data response])))

(rf/reg-event-fx
  ::graphql-some-stuff-failure
  (fn [_ [_ response]]
    (println "failure" response)))

(rf/reg-cofx
  :gql-url
  (fn [cofx _]
    (assoc cofx :gql-url (get @(rf/subscribe [:gql-details]) "url"))))

(rf/reg-cofx
  :gql-token
  (fn [cofx _]
    (assoc cofx :gql-token (get @(rf/subscribe [:gql-details]) "token"))))
