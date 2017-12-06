(ns lan-van.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [lan-van.subs :as subs]
            [lan-van.events :as events]
            [reagent.core :as r]
            [clojure.string :as str]))

(defn dispatch-resize-event
  []
  (dispatch [::events/window-resize]))

(defonce resize-event
  (.addEventListener js/window
                     "resize"
                     dispatch-resize-event))

(defn dispatch-location-poll-event
  []
  (dispatch [::events/get-van-location])
  (dispatch [::events/get-van-status])
  (dispatch [::events/get-current-dropup]))

(defonce location-poll
  (js/setInterval dispatch-location-poll-event 1000))

(defn dispatch-dropups-poll-event
  []
  (dispatch [::events/get-dropups]))

(defonce dropups-poll
  (js/setInterval dispatch-dropups-poll-event (* 1000 60)))

(defn gmap-inner []
  (let [gmap    (atom nil)
        options (clj->js {:zoom 18
                          :clickableIcons false
                          :disableDefaultUI true
                          :disableDoubleClickZoom true
                          :draggable false
                          :keyboardShortcuts false
                          :scrollwheel false})
        update  (fn [comp]
                  (let [props (r/props comp)
                        {:keys [latitude longitude]} (:location props)
                        height (:height props)
                        latlng (js/google.maps.LatLng. latitude longitude)]
                    (set! (.-height (.-style (:canvas @gmap)))
                          (str height "px"))
                    (.trigger js/google.maps.event
                              (:map @gmap)
                              "resize")
                    (.setPosition (:marker @gmap) latlng)
                    (.panTo (:map @gmap) latlng)))]

    (r/create-class
     {:reagent-render
      (fn []
        [:div#map-canvas])

      :component-did-mount
      (fn [comp]
        (let [canvas  (.getElementById js/document "map-canvas")
              gm      (js/google.maps.Map. canvas options)
              marker-opts {:clickable false
                           :icon "/images/NyanVan.gif"
                           :map gm
                           :optimized false
                           :title "Lan Van"}
              marker  (js/google.maps.Marker. (clj->js marker-opts))]
          (reset! gmap {:map gm
                        :marker marker
                        :canvas canvas}))
        (update comp))

      :component-did-update update
      :display-name "gmap-inner"})))

(defn gmap-outer [opts]
  (let [location (subscribe [::subs/location])
        height (subscribe [::subs/height])
        van-status (subscribe [::subs/van-status])]
    (fn []
      [:div opts
       (when-not (= @van-status :tracking)
         [:div.overlay
          [:div
           "OFFLINE"]])
       [gmap-inner {:location @location
                    :height @height}]])))

(defn dropups
  [opts]
  (let [dropups (subscribe [::subs/dropups])
        current (subscribe [::subs/current])
        height (subscribe [::subs/height])]
    (fn []
      [:div#dropouts (merge opts {:style {:height (str @height "px")}})
       [:table
        [:thead
         [:tr
          [:th "Name"]
          [:th "Time"]]]
        [:tbody
         (doall
          (for [d @dropups]
            (let [names (map :name (:people d))
                  current? (= @current (:id d))]
              ^{:key (if current? "current" (:id d))}
              [:tr {:class (when current? "current")}
               [:td (str/join ", " names)]
               [:td (:time d)]])))]]])))

(defn play-button
  []
  (let [playing? (subscribe [::subs/playing?])]
    (fn []
      [:div#play-button
       {:on-click (fn []
                    (dispatch [::events/toggle-music]))
        :class (if @playing?
                 "playing"
                 "paused")}])))

(defn main-panel
  []
  (let [christmas? (subscribe [::subs/christmas?])]
    (fn []
      [:div
       (when @christmas?
         [:div#snow])
       [:div.container-fluid
        [:div.row.row-no-padding
         [gmap-outer {:class "col-sm-10 col-xs-12"}]
         [dropups {:class "col-sm-2 col-xs-12"}]
         [play-button]]]])))
