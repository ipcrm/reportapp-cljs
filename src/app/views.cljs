(ns app.views
  (:require
    [re-frame.core :as rf]
    [app.components :as comps]
    ["@material-ui/core/styles" :as styles]
    ["@material-ui/core/CssBaseline" :default CssBaseline]
    ["@material-ui/core/AppBar" :default AppBar]
    ["@material-ui/core/Button" :default Button]
    ["@material-ui/core/Paper" :default Paper]
    ["@material-ui/core/ToolBar" :default ToolBar]
    ["@material-ui/core/Typography" :default Typography]
    ["@material-ui/core/styles" :default Typography]
    ["@material-ui/icons/Menu" :default MenuIcon]
    ["@material-ui/core/IconButton" :default IconButton]
    ["@material-ui/core/Tabs" :default Tabs]
    ["@material-ui/core/Tab" :default Tab]
    ))

;; -- Domino 5 - View Functions ----------------------------------------------
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

(defn on-tab-change
  "When a nav tab is clicked, this function sets the active panel which causes the correct panel to show"
  [_, v]
  (rf/dispatch [:set-active-panel v]))

;; I've moved the testdata subscription into the root component
;; This really is a reagent component and so it will take care of re-rendering
;; any components where there props change
;; In the example below, chart-data are "props" of the "build-summary-component" component
;; reagent knows that chart-data has been updated so it will re-render the build-summary-component
;; any time this data changes
(defn ui []
  (let [chart-data @(rf/subscribe [:testdata])
        active-panel (rf/subscribe [:active-panel])
        query-in-progress (rf/subscribe [:query-in-progress])]
    [:> CssBaseline
     [:div.foo
      [:> styles/ThemeProvider {:theme (my-theme)}
       [:> Paper
        [:> AppBar {:position "static"}
         [:> Tabs {:on-change on-tab-change :value @active-panel}
          [:> Tab {:label "Home" :id "panel1" :aria-controls "home"}]
          [:> Tab {:label "Graphs" :id "panel2" :aria-controls "graphs"}]]]

          [:div.foo-body
           [:> Typography {:component "h1" :display "block" } "Hello world, it is now"]
           (if (= @query-in-progress true) [comps/query-running])
           (condp = @active-panel
             0 [comps/fake]
             1 [comps/graph-panel chart-data])]
        ]
       ]
      ]
     ]
    ))