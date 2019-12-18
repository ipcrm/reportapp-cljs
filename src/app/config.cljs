(ns app.config)

(def thequery "query GetBuildData { Build (orderBy: [timestamp_desc], first: 1000){ id timestamp finishedAt startedAt status push { after { sha } } repo { name owner } }}")
(defn get-api-key
  "Return key"
  []
  "")

(defn get-api-url
  "Return URL"
  []
  "")
