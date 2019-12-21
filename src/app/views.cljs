(ns app.views
  (:require
    [re-frame.core :as rf]
    [app.components :as comps]
    [app.utils :as utils]
    [cljss.core :as css :refer-macros [defstyles defkeyframes] :refer [inject-global]]
    ["@material-ui/core/styles" :as styles]
    ["@material-ui/core/Container" :default Container]
    ["@material-ui/core/CssBaseline" :default CssBaseline]
    ["@material-ui/core/AppBar" :default AppBar]
    ["@material-ui/core/Button" :default Button]
    ["@material-ui/core/Paper" :default Paper]
    ["@material-ui/core/ToolBar" :default ToolBar]
    ["@material-ui/core/Typography" :default Typography]
    ["@material-ui/core/styles" :default Typography]
    ["@material-ui/icons/Menu" :default MenuIcon]
    ["@material-ui/icons/Copyright" :default CopyIcon]
    ["@material-ui/core/IconButton" :default IconButton]
    ["@material-ui/core/Tabs" :default Tabs]
    ["@material-ui/core/Tab" :default Tab]
    ))

;; -- Domino 5 - View Functions ----------------------------------------------
;; Theme
(defn my-theme
  "This is the junk theme I'm using for Material UI"
  []
  (styles/createMuiTheme
    (clj->js
      {:palette {:type      "light"
                 :primary   {:main "#162738"}
                 :secondary {:main "#5BD378"}}
       :status  {:danger "orange"}}
      )))

;; Hacky CSS
(inject-global {
                :body             {:width "960px" :height "800px" :margin "0 auto 0 auto !important"}
                ".app"            {:margin "0 auto 0 auto"}
                ".summary-chart"  {:width "400px" :height "500px"}
                ".summary-text"   {:float "right"}
                ".foo"            {:width "80%" :margin "0 auto 0 auto" :padding-top "10px"}
                ".foo-body"       {:padding "10px" :height "100%"}
                ".foo .nav ul li" {:float "left" :width "50px" :text-decoration "none" :list-style "none"}
                ".active"         {:color "red" :text-decoration "none" :pointer-events "none" :cursor "default"}
                ".header-image"   {:height "300px" :width "300px !important" :margin "0 auto 0 auto"}
                ".vertical-middle" {:vertical-align "middle"}
                }
  )

;; I've moved the testdata subscription into the root component
;; This really is a reagent component and so it will take care of re-rendering
;; any components where there props change
;; In the example below, chart-data are "props" of the "build-summary-component" component
;; reagent knows that chart-data has been updated so it will re-render the build-summary-component
;; any time this data changes
(defn ui []
  (let [chart-data @(rf/subscribe [:testdata])
        active-panel (rf/subscribe [:active-panel])
        query-in-progress (rf/subscribe [:query-in-progress])
        gql-submitted @(rf/subscribe [:gql-submitted])
        ]
    [:> CssBaseline
      [:div.foo
       [comps/header]
       [:> styles/ThemeProvider {:theme (my-theme)}
        [:> Paper
         [:> AppBar {:position "static"}
          [:> Tabs {:on-change utils/on-tab-change :value @active-panel}
           [:> Tab {:label "Home" :id "panel1" :aria-controls "home"}]
           [:> Tab {:label "Graphs" :id "panel2" :aria-controls "graphs"}]]]

         [:div.foo-body
          [:> Typography {:component "h1" :display "block" } "Some random text goes here"]
          (if (= @query-in-progress true) [comps/query-running])
          (condp = @active-panel
            0 [comps/fake]
            1 [comps/graph-panel chart-data gql-submitted])]
         ]
        ]
       ]
      [:div.footer {:style {:text-align "right"}} [:> CopyIcon {:className "vertical-middle"}] " " (.getFullYear (js/Date.))]]
    ))