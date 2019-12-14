(ns app.main-test
  (:require
      [cljs.test :refer-macros [deftest is testing run-tests]]
      [app.main :as main]
      [app.utils :as utils] ))

(def testdata
  {:data {:Build [{:finishedAt "2019-12-04T15:47:09.930Z"
                   :id "A2MO4H2RG_ipcrmdemo/samplemicronaut/0b8dd7fd4957e3b0890d6285528d1a9fbba49537/39"
                   :push {:after {:sha "0b8dd7fd4957e3b0890d6285528d1a9fbba49537"}}
                   :repo {:name "samplemicronaut" :owner "ipcrmdemo"}
                   :startedAt "2019-12-04T15:46:41.697Z"
                   :status "passed"
                   :timestamp "2019-12-04T15:47:10.467Z"}
                  {:finishedAt "2019-12-04T15:37:19.934Z"
                   :id "A2MO4H2RG_ipcrmdemo/samplemicronaut/c683b98865457b05cdfb50704d7da92215a5a552/38"
                   :push {:after {:sha "c683b98865457b05cdfb50704d7da92215a5a552"}}
                   :repo {:name "samplemicronaut" :owner "ipcrmdemo"}
                   :startedAt "2019-12-04T15:36:52.720Z"
                   :status "passed"
                   :timestamp "2019-12-04T15:37:20.366Z"}
                  {:finishedAt "2019-12-04T15:24:18.887Z"
                   :id "A2MO4H2RG_ipcrmdemo/samplemicronaut/b3cbfd15c8611d1800b598cb1ab5667c7290f47f/37"
                   :push {:after {:sha "b3cbfd15c8611d1800b598cb1ab5667c7290f47f"}}
                   :repo {:name "samplemicronaut" :owner "ipcrmdemo"}
                   :startedAt "2019-12-04T15:23:50.126Z"
                   :status "passed"
                   :timestamp "2019-12-04T15:24:19.138Z"}
                  {:finishedAt "2019-12-04T15:18:34.074Z"
                   :id "A2MO4H2RG_ipcrmdemo/samplemicronaut/7abadbe420ba7d935b532d6a50a2a6eb12bb7eb4/36"
                   :push {:after {:sha "7abadbe420ba7d935b532d6a50a2a6eb12bb7eb4"}}
                   :repo {:name "samplemicronaut" :owner "ipcrmdemo"}
                   :startedAt "2019-12-04T15:18:07.309Z"
                   :status "failed"
                   :timestamp "2019-12-04T15:18:34.408Z"}
                  {:finishedAt nil
                   :id "A2MO4H2RG_ipcrmdemo/samplemicronaut/db6d9b350d050cba0270070a8d6eb616fa7ce2ed/35"
                   :push {:after {:sha "db6d9b350d050cba0270070a8d6eb616fa7ce2ed"}}
                   :repo {:name "samplemicronaut" :owner "ipcrmdemo"}
                   :startedAt "2019-12-04T15:10:52.263Z"
                   :status "passed"
                   :timestamp "2019-12-04T15:11:18.294Z"}]}})

(deftest test-numbers
  (testing "simple addition test"
    (is (= 1 1))))

(deftest test1
  (testing "test1 should add the numbers"
    (is (= (main/test1 1 2) 3))))

(deftest extract-build-info
  (testing "extract-build-info should only return valid records from the GQL output"
    (is (= (count (utils/extract-build-info testdata)) 4))))

(deftest get-failed-builds
  (testing "get-failed-builds should only return records that are failed builds"
    (is
      (=
        (count (app.utils/get-failed-builds (utils/extract-build-info testdata)))
        1))
    ))
