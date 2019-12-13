(ns app.utils
  (:require [re-frame.core :as rf]))

(defn is-date-string-parse-able?
  "Can we convert this date string to a number?"
  [x]
  (number? (js/Date.parse x)))

;; TODO: This has got to be built-in
(defn is-not-nil?
  "Is this value nil?"
  [x]
  (not (= nil x)))

;; TODO: Try spec here
(defn is-valid-build?
  [x]
  "Is this build record valid?"
      (and  (contains? x :finishedAt)
            (contains? x :startedAt)
            (is-not-nil? (:startedAt x))
            (is-not-nil? (:finishedAt x))
            (is-date-string-parse-able? (:startedAt x))
            (is-date-string-parse-able? (:finishedAt x))))

(defn extract-build-info [data]
      "Extract all build-data and return it only if valid"
      (if (not (= data ""))
        (into [] (filter is-valid-build? (-> data :data :Build)))
        []))

(defn get-build-time
  "Get in one build element, Figure out duration"
  [data]
      (let [start  (.parse js/Date (:startedAt data))
            end    (.parse js/Date (:finishedAt data))]
            (.round js/Math (- end start))))

(defn compute-total-time
  "Given an array of build elements, calculate the total build time for all elements"
  [data]
      (reduce
        (fn [acc x] (+ acc (get-build-time x)))
        0
        data))

;; Get all builds
;; Validate they have start/end times
;; Compute total time of builds & count
;; Divide by count
(defn get-build-time-avg
  "Calculate average build time for a given set of build data."
  [build-data]
      (let [
            build-count (count build-data)
            total-time (compute-total-time build-data)]
           (js/Math.round (/ total-time build-count))
           ))

(defn get-success-builds
  "Get the data for only successful builds"
  [build-data]
      (filter (fn [x] (= (:status x) "passed")) build-data))

(defn get-failed-builds
  "Get the data for only failed builds"
  [build-data]
      (filter (fn [x] (= (:status x) "failed")) build-data))

(defn get-build-rate
  "Get the success rate for a given set of build data (%)"
  [type build-data]
      (let [
            build-count (count build-data)
            builds (count (if (= type "success")
                            (do
                              (print "success builds data")
                              (get-success-builds build-data)
                              )
                            (do
                              (print "failed builds data")
                              (get-failed-builds build-data)))
                              )
            ]
           (print build-count)
           (print builds)
           (js/Math.round (* (/ builds build-count) 100))
           ))

(defn get-build-times
  "Get all the build times and return a sorted list"
  [build-data]
      (->
        (map (fn [n] (get-build-time n)) build-data)
        (sort)
      ))

(defn get-median-build-time
  "Get the median build time"
  [build-data]
      (let [
           build-times (count (get-build-times build-data))
           low-middle (js/Math.floor (/ (- build-times 1) 2))
           high-middle (js/Math.ceil (/ (- build-times 1) 2))
           ]
      (js/Math.round (/ (+ low-middle high-middle) 2))))

(defn create-build-data-labels
  "Create series labels from build data"
  [build-data]
      (->
        (map (fn [b] (.toDateString (new js/Date (:timestamp b))) ) build-data)
        (reverse)
      ))

(defn create-failed-build-data-labels
  "Create series labels from build data"
  [build-data]
      (->
        (map (fn [b] (.toDateString (new js/Date (:timestamp b))) ) (get-failed-builds build-data))
        (reverse)
        ))

(defn create-success-build-data-labels
  "Create series labels from build data"
  [build-data]
      (->
        (map (fn [b] (.toDateString (new js/Date (:timestamp b))) ) (get-success-builds build-data))
        (reverse)
        ))

(defn convert-to-chart-map
  "Converts the ouptut of (frequencies (get-x-builds data)) to a js object usable by a graph"
  [freq-build-data]
      (map (fn [[k v]]
               (js-obj :x k :y v)) freq-build-data))

(defn trim-timestamps
  "Remove the time/timezone from timestamp in build-data"
  [build-data]
      (map
        (fn [v]
            (first (js->clj (.split (v :timestamp) "T"))))
        build-data))

(defn build-series-data
      "Return map that can be used to plot data from build data. [type] success, failed, total "
      [type build-data]
      (case type
        "success"
          (convert-to-chart-map (frequencies (trim-timestamps (get-success-builds build-data))))
        "failed"
          (convert-to-chart-map (frequencies (trim-timestamps (get-failed-builds build-data))))
        "total"
          (convert-to-chart-map (frequencies (trim-timestamps build-data)))
        ))

