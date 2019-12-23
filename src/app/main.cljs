(ns app.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [app.events]
            [app.subs]
            [app.views :as views]))

;; -- Entry
(defn render
  []
  (reagent/render [views/ui] (js/document.getElementById "app")))

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])
  (render))


;; -- Dev
(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (render))
