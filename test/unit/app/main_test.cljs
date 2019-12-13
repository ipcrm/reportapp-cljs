(ns app.main-test (:require
      [cljs.test :refer-macros [deftest is testing run-tests]]
      [app.main :as main]))

(deftest test-numbers
      (is (= 1 1)))


(deftest test1
  (is (= (main/test1 1 2) 3)))