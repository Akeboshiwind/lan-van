(ns lan-van.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [lan-van.events :as events]
            [lan-van.views :as views]
            [lan-van.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/window-resize])
  (re-frame/dispatch-sync [::events/get-dropups])
  (dev-setup)
  (mount-root))
