(ns lan-van.coeffects
  (:require [re-frame.core :refer [reg-cofx]]))

(reg-cofx
 ::window-size
 (fn [coeffects _]
   (assoc coeffects
          :window-size {:width (.-innerWidth js/window)
                        :height (.-innerHeight js/window)})))

(def months
  [:january
   :february
   :march
   :april
   :may
   :june
   :july
   :august
   :september
   :october
   :november
   :december])

(reg-cofx
 ::month
 (fn [coeffects _]
   (assoc coeffects
          :month (get months (.getMonth (js/Date.)) :january))))
