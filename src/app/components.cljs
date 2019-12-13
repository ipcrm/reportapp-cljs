(ns app.components
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [app.utils :as utils]))

(defn show-build-summary-text
      []
      [:div.summary-text
        [:table
          [:tr
           [:th "builds"]
           [:th "success"]
           [:th "fails"]]

          [:tr
           [:td (count (utils/extract-build-info @(rf/subscribe [:testdata])))]
           [:td (count (utils/get-success-builds (utils/extract-build-info @(rf/subscribe [:testdata]))))]
           [:td (count (utils/get-failed-builds (utils/extract-build-info @(rf/subscribe [:testdata]))))]
           ]]
       ])

(defn show-build-summary
  [build-data]
  (let [
        new-build-data (app.utils/extract-build-info build-data)
        context (.getContext (.getElementById js/document "build-summary-chart") "2d")
        chart-data {:type "bar"
                    :data {:labels   ["Total" "Successful" "Failed"]
                           :datasets [{:data            [(count new-build-data)
                                                         (count (app.utils/get-success-builds new-build-data))
                                                         (count (app.utils/get-failed-builds new-build-data))]
                                       :label           "Build Data"
                                       :backgroundColor "#90EE90"}]}}]
    (print chart-data)
    (js/Chart. context (clj->js chart-data))
    ))

(defn build-summary-component
      []
      (reagent/create-class
        {:component-did-mount #(show-build-summary @(rf/subscribe [:testdata]))
         :display-name        "Build Summary"
         :reagent-render      (fn []
                                  [:div.summary-chart
                                    [:canvas {:id "build-summary-chart" :width "250px" :height "180px"}]]
                                  )}))