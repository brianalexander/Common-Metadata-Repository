(ns cmr.umm-spec.test.noaa-instrument-xpath-test
  "Tests getting  NOAA instrument from alternative xpath for ISO19115." 
  (:require
   [clojure.java.io :as io]
   [clojure.test :refer :all]
   [cmr.umm-spec.json-schema :as js]
   [cmr.umm-spec.models.umm-common-models :as cmn]
   [cmr.umm-spec.test.location-keywords-helper :as lkt]
   [cmr.umm-spec.umm-spec-core :as core]
   [cmr.umm-spec.util :as util]))

(def test-context (lkt/setup-context-for-test))

(def expected-noaa-platforms
  (let [instruments [(cmn/map->InstrumentType {:ShortName "AMSR2" :LongName "Advanced Microwave Scanning Radiometer 2"})]]
    (seq (map #(assoc % :Instruments instruments) util/not-provided-platforms))))

(defn noaa-example-file
  "Returns an example ISO19115 metadata file with NOAA instrument xpath."
  []
  (io/file (io/resource "example_data/iso19115/ISOExample-NOAA-Instrument-XPath.xml")))

(defn mixed-example-file
  "Returns an example ISO19115 metadata file with both regular instrument xpath and NOAA instrument xpath."
  []
  (io/file (io/resource "example_data/iso19115/ISOExample-Mixed-Instrument-XPath.xml")))

(deftest test-noaa-example-file 
  "Verify the returned platforms is equal to expected-noaa-platforms."
  (let [metadata (slurp (noaa-example-file))
        umm (js/parse-umm-c (core/parse-metadata test-context :collection :iso19115 metadata))]
    (is (= expected-noaa-platforms (:Platforms umm)))))
         
(deftest test-mixed-example-file 
  "Verify that the returned platforms doesn't include the not-provided-platform."
  (let [metadata (slurp (mixed-example-file))
        umm (js/parse-umm-c (core/parse-metadata test-context :collection :iso19115 metadata))]
    (is (< 0 (count (:Platforms umm))))
    (is (= nil (some #(= (first expected-noaa-platforms) %) (:Platforms umm))))))