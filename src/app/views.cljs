(ns app.views
  (:require
    [re-frame.core :as rf]
    [app.config :as config]
    [re-graph.core :as re-graph]
    [app.components :as comps]))

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
     [:button {:type "button" :on-click #(rf/dispatch [::re-graph/query config/thequery {} [:retrieve-gql-data]])} "GQL Data!"]
     ;; native react component (ratoms from a subscription will not re-render when data changes)
     [:div.summary-chart [comps/build-summary-component chart-data]]
     [:div.summary-text [comps/show-build-summary-text chart-data]]
     ]))