(ns lan-van.events
  (:require [re-frame.core :as rf :refer [reg-event-db
                                          reg-event-fx
                                          trim-v
                                          inject-cofx]]
            [cljs.spec.alpha :as s]
            [lan-van.db :as db]
            [lan-van.effects :as e]
            [lan-van.coeffects :as ce]
            [day8.re-frame.http-fx]
            [expound.alpha :as expound]
            [ajax.core :as ajax]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (expound/expound-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw :lan-van.db/db)))

(reg-event-db
 ::initialize-db
 [check-spec-interceptor]
 (fn  [_ _]
   db/default-db))

(reg-event-db
 ::set-van-location
 [check-spec-interceptor
  trim-v]
 (fn [db [location]]
   (assoc db :location location)))


(reg-event-fx
 ::xhrio-error
 [check-spec-interceptor
  trim-v]
 (fn [cofx [response]]
   {::e/log (str response)}))

(reg-event-fx
 ::get-van-location
 [check-spec-interceptor]
 (fn [cofx _]
   (let [db (:db cofx)
         van-status (:van-status db)]
     (merge {}
            (when (= van-status :tracking)
              {:http-xhrio {:method :get
                            :uri "/api/v1/van/location"
                            :format (ajax/json-request-format)
                            :response-format (ajax/json-response-format {:keywords? true})
                            :on-success [::set-van-location]
                            :on-failure [::xhrio-error]}})))))

(reg-event-fx
 ::window-resize
 [check-spec-interceptor
  (inject-cofx ::ce/window-size)]
 (fn [cofx _]
   (let [db (:db cofx)]
     {:db (assoc db
                 :window (:window-size cofx))})))

(reg-event-db
 ::set-current-dropup
 [check-spec-interceptor
  trim-v]
 (fn [db [response]]
   (assoc db :current (:currentDropUpId response))))

(reg-event-fx
 ::get-current-dropup
 [check-spec-interceptor]
 (fn [cofx _]
   (let [db (:db cofx)
         van-status (:van-status db)]
     (merge {}
            (when (= van-status :tracking)
              {:http-xhrio {:method :get
                            :uri "/api/v1/dropups/current"
                            :format (ajax/json-request-format)
                            :response-format (ajax/json-response-format {:keywords? true})
                            :on-success [::set-current-dropup]
                            :on-failure [::xhrio-error]}})))))

(reg-event-db
 ::set-dropups
 [check-spec-interceptor
  trim-v]
 (fn [db [dropups]]
   (assoc db :dropups dropups)))

(reg-event-fx
 ::get-dropups
 [check-spec-interceptor]
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri "/api/v1/dropups"
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-dropups]
                 :on-failure [::xhrio-error]}}))

(reg-event-db
 ::set-van-status
 [check-spec-interceptor
  trim-v]
 (fn [db [{:keys [status]}]]
   (assoc db :van-status (keyword status))))

(reg-event-fx
 ::get-van-status
 [check-spec-interceptor]
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri "/api/v1/van/status"
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-van-status]
                 :on-failure [::xhrio-error]}}))
