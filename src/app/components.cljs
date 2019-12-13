(ns app.components
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [app.utils :as utils]))


(defn mount-chart [comp]
  (if (not (= comp ""))
    (let [
          new-build-data (app.utils/extract-build-info (reagent.core/props comp))
          context (.getContext (.getElementById js/document "build-summary-chart") "2d")
          chart-data {:type "bar"
                      :data {:labels   ["Total" "Successful" "Failed"]
                             :datasets [{:data            [(count new-build-data)
                                                           (count (app.utils/get-success-builds new-build-data))
                                                           (count (app.utils/get-failed-builds new-build-data))]
                                         :label           "Build Data"
                                         :backgroundColor "#90EE90"}]}}]
      (print chart-data)
      (js/Chart. context (clj->js chart-data)))))

(defn update-chart [comp]
  (mount-chart comp))

(defn chart-inner []
  (reagent/create-class
    {:component-did-mount mount-chart
     :component-did-update update-chart
     :display-name        "Build Summary"
     :reagent-render      (fn [comp]
                            [:div.summary-chart
                             [:canvas {:id "build-summary-chart" :width "250px" :height "180px"}]]
                            )}))

(defn chart-outer [chart-data]
  [chart-inner @chart-data])

(defn show-build-summary-text
      []
      [:div.summary-text
        [:table
          [:tr
           [:th "builds"]
           [:th "success"]
           [:th "fails"]]

         (if (not (= @(rf/subscribe [:testdata]) []))
            [:tr
             [:td (count (utils/extract-build-info @(rf/subscribe [:testdata])))]
             [:td (count (utils/get-success-builds (utils/extract-build-info @(rf/subscribe [:testdata]))))]
             [:td (count (utils/get-failed-builds (utils/extract-build-info @(rf/subscribe [:testdata]))))]
             ]
         )
         ]
       ])
