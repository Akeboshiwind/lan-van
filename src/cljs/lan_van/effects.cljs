(ns lan-van.effects
  (:require [re-frame.core :refer [reg-fx]]))

(reg-fx
 ::log
 (fn [message]
   (.log js/console message)))

(defonce audio
  (let [audio (atom (js/Audio. "/audio/loop.ogg"))]
    (set! (.-loop @audio) true)
    #_
    (.addEventListener js/window
                       "ended"
                       (fn []
                         (set! (.-src audio) "/audio/loop.ogg")
                         (set! (.-loop audio) true)))
    audio))

(reg-fx
 ::play-nyan-music?
 (fn [playing?]
   (if playing?
     (.play @audio)
     (.pause @audio))))
