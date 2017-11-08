(ns lan-van.coeffects
  (:require [re-frame.core :refer [reg-cofx]]))

(reg-cofx
 ::window-size
 (fn [coeffects _]
   (assoc coeffects
          :window-size {:width (.-innerWidth js/window)
                        :height (.-innerHeight js/window)})))
