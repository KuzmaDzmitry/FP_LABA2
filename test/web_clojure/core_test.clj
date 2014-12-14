(ns web-clojure.core-test
  (:require [clojure.test :refer :all]
            [web-clojure.core :refer :all]
            [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as httpkit]))

(deftest test-get-code-content
  (testing "Processes 404 code"
    (with-redefs-fn {#'httpkit/get (fn [& _] (atom {:status 404}))} #(is (=[:not-found]))))
(get-code-content "url")
(testing "Processes 10000code as unknown error"
 (with-redefs-fn {#'httpkit/get (fn [& _] (atom {:status 10000}))} #(is (=[:unknown-error])))))
 (get-code-content "url")
 (deftest test-links-filter
  (testing "Returns absolute links"
    (is (=(links-filter ["http://www.dys-add.com/index", "relative.html", "/another.html", "http://www.dys-add.com"]) ["http://www.dys-add.com/index", "http://www.dys-add.com"]))))