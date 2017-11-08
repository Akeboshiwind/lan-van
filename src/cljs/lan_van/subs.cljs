(ns lan-van.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::location
 (fn [db]
   (:location db)))

(reg-sub
 ::dropups
 (fn [db]
   (:dropups db)))

(reg-sub
 ::current
 (fn [db]
   (:current db)))

(reg-sub
 ::window
 (fn [db]
   (:window db)))

(reg-sub
 ::width
 :<- [::window]
 (fn [window _]
   (:width window)))

(reg-sub
 ::height
 :<- [::window]
 (fn [window _]
   (:height window)))

(reg-sub
 ::van-status
 (fn [db]
   (:van-status db)))
