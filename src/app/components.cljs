(ns app.components
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [app.utils :as utils]
            [cljss.core :as css :refer-macros [defstyles defkeyframes] :refer [inject-global]]
            ["@material-ui/core/Card" :default Card]
            ["@material-ui/core/FormControl" :default FormControl]
            ["@material-ui/core/CardContent" :default CardContent]
            ["@material-ui/core/CardMedia" :default CardMedia]
            ["@material-ui/core/CardActionArea" :default CardActionArea]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/Grid" :default Grid]
            ["@material-ui/core/TextField" :default TextField]
            ["@material-ui/styles" :as styles]
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
  ]))

(defn- reset-chart-data [chart build-data]
  "Populate Chart data using GraphQL query"
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

(defn show-the-charts [chart-data]
  [:> Grid {:container true :alignItems "center"}
   (if (not= chart-data "") (rf/dispatch [:query-in-progress false]))
   (if (not= chart-data "")
     [:> Grid {:item true :md true}
      [build-summary-component chart-data]])
   (if (not= chart-data "")
     [:> Grid {:item true :md true}
      [show-build-summary-text chart-data]])
   [:> Grid {:container true}
    [:> Grid {:item true :xs true}
     (if (and (= chart-data "")
              (= @(rf/subscribe [:query-in-progress]) false))
       [:> Button {:color "secondary" :variant "contained" :on-click utils/query-and-notify} "Render"])

     (if (not= chart-data "")
       [:> Button {:color "primary" :variant "contained" :on-click #(rf/dispatch [:clear-testdata])} "Clear"])
     ]]]
)

;; Form stuff
(def events
  {:get (fn [path] @(rf/subscribe [:value path]))
   :save! (fn [path value] (rf/dispatch [:set-value path value]))
   :update! (fn [path save-fn value]
              (rf/dispatch [:update-value save-fn path value]))
   :gql-details (fn [] @(rf/subscribe [:gql-details]))})

(defn test-submit []
  (rf/dispatch [:gql-submitted true])
  (utils/query-and-notify))

(defn gql-details-form
  []
   [bind-fields
    [:div.form
     [:form
      [:input {:field :text :id :gql.url}]
      [:input {:field :text :id :gql.token}]]
      ]
    events]
  )

(defn show-the-gql-details-page []
  [:div.formouter
   [gql-details-form]
   [:> Button {:color "secondary" :variant "contained" :on-click test-submit} "Submit"]
   ])

(defn graph-panel
  "Show the graphs panel, but only if we have stored gql endpoint and token info"
  [chart-data gql-submitted]
  (println "graph pane" gql-submitted chart-data)
    (if (true? gql-submitted)
      (show-the-charts chart-data)
      (show-the-gql-details-page)))

(defn fake
  "Simple component to display some dynamic text"
  []
  [:div.test
   [:> Typography {:compnent "h2" :gutterBottom true} @(rf/subscribe [:test])]
   [:> Button {:color "secondary" :variant "contained" :on-click #(rf/dispatch [:update-data "newdatagoeshere"])} "Click Me!"]
   [:> Button {:color "primary" :variant "contained" :on-click #(rf/dispatch [:clear-data])} "Clear Me!"]])


(defn header
  "Card header"
  []
  [:> Card
   [:> CardActionArea {}
    [:> CardMedia {
                   :className "header-image"
                   :on-click #(utils/on-tab-change nil 0)
                   :component "img"
                   :alt       "guy"
                   :image     "https://raw.githubusercontent.com/tallesl/Rich-Hickey-fanclub/master/cartoon/resized.png"}]
    [:> CardContent
     [:> Typography {:variant "h5" :component "h5" :gutterBottom true} "Matt learns CLJS"]]]])

;[:div.form
; [:input {:field :text :id :gql.url}]
; [:input {:field :text :id :gql.token}]
; ]
;[:form {:id "gql-data"}
; [:> FormControl {:fullWidth true :margin "full"}
;  [:> TextField {:label "GraphQL Url"} [:input {:field  :text :id :gql.url}]]
;  [:> TextField {:label "GraphQL Token"} [:input {:field :text :id :gql.token}]]
;  [:> Button {:color "secondary" :onClick test-submit :variant "contained"} "Submit"]
;  ]

;[bind-fields
; [:form {:id "gql-data"}
;  [:> FormControl {:fullWidth true }
;   [:> TextField {:label "GraphQL Url"} [:input {:field :text :id :gql.url}]]
;   [:> TextField {:label "GraphQL Token"} [:input {:field :text :id :gql.token}]]
;   [:> Button {:color "secondary" :onClick test-submit :variant "contained"} "Submit"]
;   ]]
;  events]
