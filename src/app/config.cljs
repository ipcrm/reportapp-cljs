(ns app.config
  (:require [re-frame.core :as rf]))

(def thequery "query GetBuildData { Build (orderBy: [timestamp_desc], first: 1000){ id timestamp finishedAt startedAt status push { after { sha } } repo { name owner } }}")
(defn get-api-key
  "Return key"
  []
  (:token (:gql @(rf/subscribe [:gql-details])))
  )

(defn get-api-url
  "Return URL"
  []
  (:url (:gql @(rf/subscribe [:gql-details])))
  )
