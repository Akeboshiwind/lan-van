(ns lan-van.db
  (:require [cljs.spec.alpha :as s]))

(s/def ::latitude float?)

(s/def ::longitude float?)

(s/def ::location
  (s/keys :req-un [::latitude
                   ::longitude]))

(s/def ::name string?)

(s/def ::person
  (s/keys :req-un [::name]))

(s/def ::id int?)
(s/def ::people (s/+ ::person))
(s/def ::time string?)

(s/def ::dropup
  (s/keys :req-un [::id
                   ::people
                   ::time]))

(s/def ::dropups (s/* ::dropup))

(s/def ::current ::id)

(s/def ::van-status
  #{:tracking
    :disconnected
    :connected})

(s/def ::width int?)
(s/def ::height int?)

(s/def ::window
  (s/keys :req-un [::width
                   ::height]))

(s/def ::db
  (s/keys :req-un [::location
                   ::dropups
                   ::current
                   ::van-status
                   ::window]))


(def default-db
  {:location {:latitude 0
              :longitude 0}
   :dropups []
   :current -1
   :van-status :disconnected
   :window {:width 0
            :height 400}})
