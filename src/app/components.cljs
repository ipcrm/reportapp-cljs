(ns app.components
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [app.utils :as utils]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/Button" :default Button]))

(defn show-build-summary-text
  "Display a table with the build info"
  []
  (let [data (utils/extract-build-info @(rf/subscribe [:testdata]))]
    [:div.summary-text
     [:table
      [:tr
       [:th "builds"]
       [:th "success"]
       [:th "fails"]]

      [:tr
       [:td (count data)]
       [:td (count (utils/get-success-builds data))]
       [:td (count (utils/get-failed-builds data))]
       ]]
     ]
    ))

(defn- reset-chart-data [chart build-data]
  (let [context (.getContext (.getElementById js/document "build-summary-chart") "2d")
        new-build-data (app.utils/extract-build-info build-data)
        chart-data {:type "bar"
                    :data {:labels ["Total" "Successful" "Failed"]
                           :datasets [{:data [(count new-build-data)
                                              (count (app.utils/get-success-builds new-build-data))
                                              (count (app.utils/get-failed-builds new-build-data))]
                                       :label "Build Data"
                                       :backgroundColor "#90EE90"}]}}]
    (let [c (js/Chart. context (clj->js chart-data))]
      (reset! chart c))))

(defn- show-build-summary
  "this is not a react component - it is a callback used when the component first updates"
  [chart build-data]
  (fn []
    (reset-chart-data chart build-data)))

(defn- update-build-summary
  "this is not a react component - it is an update handler "
  [chart]
  (fn [comp]
    (let [data (reagent/props comp)]
      (reset-chart-data chart data))))

;; this is a native react component
;; it does not get wrapped in reagent "ceremony"
;; However, like all react components, it has props!
;; by convention, we will treat this component's props as being graphql data
(defn build-summary-component
  [d]
  (let [chart (atom nil)]
    (reagent/create-class
     {:component-did-mount (show-build-summary chart d)
      :component-did-update (update-build-summary chart)
      :display-name "Build Summary"
      :reagent-render (fn [] [:canvas {:id "build-summary-chart" :width "250px" :height "180px"}])})))

(defn query-running
  "Display a loading image when query is running"
  []
  [:img {:src "https://stackoverflow.com/content/img/progress-dots.gif" :alt "Loading..."}])

(defn graph-panel [chart-data]
  [:div.graph-panel
   (if (and (= chart-data "")
            (= @(rf/subscribe [:query-in-progress]) false)) [:button {:type "button" :on-click utils/query-and-notify} "Render Graphs!"])

   (if (not= chart-data "")
     (do
       (rf/dispatch [:query-in-progress false])
       [:div.summary-chart
        [:button {:type "button" :on-click #(rf/dispatch [:clear-testdata])} "Clear Graphs!"]
        [build-summary-component chart-data]
        [show-build-summary-text chart-data]]))
   ]
  )

(defn fake
  "Simple component to display some dynamic text"
  []
  [:div.test
   [:> Typography {:compnent "h2" :gutterBottom true} @(rf/subscribe [:test])]
   [:> Button {:color "secondary" :variant "contained" :on-click #(rf/dispatch [:update-data "newdatagoeshere"])} "Click Me!"]
   [:> Button {:color "primary" :variant "contained" :on-click #(rf/dispatch [:clear-data])} "Clear Me!"]])



