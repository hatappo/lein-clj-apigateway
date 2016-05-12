(ns leiningen.apigateway
  (:require [clojure.java.io :refer [make-parents]])
  (:import [com.amazonaws.auth DefaultAWSCredentialsProviderChain AWSCredentials]
           [com.amazonaws.services.apigateway.model GetExportRequest GetExportResult ImportRestApiRequest ImportRestApiResult]
           [com.amazonaws.services.apigateway AmazonApiGatewayClient]
           [com.amazonaws.regions Regions]
           ;[com.amazonaws.service.apigateway.importer ApiImporterMain]
           (java.nio ByteBuffer)))

(defn- usage []
  (println "Usage:  lein apigateway <sub-command> <env>")
  (println "Ex:     lein apigateway export dev")
  (println "        lein apigateway import dev")
  (println))

(def ^AWSCredentials aws-credentials
  (.getCredentials (DefaultAWSCredentialsProviderChain.)))

(defn- create-apigateway-client [region]
  (-> (AmazonApiGatewayClient. aws-credentials)
      (.withRegion (Regions/fromName region))))

(defn- api-definition-filepath [env rest-api-id]
  (str "./apigateway/" env "/" rest-api-id ".json"))

(defn- ^GetExportResult export-rest-api
  ([rest-api-id region] (export-rest-api rest-api-id region "prod"))
  ([rest-api-id region stage] (export-rest-api rest-api-id region stage "application/json"))
  ([rest-api-id region stage accepts]
   (println "Exporting API" rest-api-id "(" stage ")in region" region)
   (let [client (create-apigateway-client region)]
     (.getExport client (-> (GetExportRequest.)
                            (.withExportType "swagger")
                            (.withParameters {"extensions" "integrations,authorizers"})
                            (.withAccepts accepts)
                            (.withStageName stage)
                            (.withRestApiId rest-api-id))))))

(defn- ^ImportRestApiResult import-rest-api [file region]
  (println "Importing API definition" file)
  (let [client (create-apigateway-client region)
        body (-> (slurp file) (.getBytes "UTF-8") (ByteBuffer/wrap))]
    (.importRestApi client (-> (ImportRestApiRequest.)
                               (.withFailOnWarnings true)
                               (.withBody body)))))

;(defn- update-rest-api [rest-api-id file region stage]
;  (let [args (into-array ["--update" rest-api-id "--deploy" stage "--region" region file])]
;    (ApiImporterMain/main args)))


(defn apigateway
  "Operates AWS API Gateway."
  [project & [task env]]
  (if-not env
    (usage)
    (let [deployments (get-in project [:apigateway env])]
      (condp = task

        "update" (println "Update is not suported yet.")
        ;"update" (doseq [{:keys [rest-api-id region stage]} deployments]
        ;           (let [src-file (api-definition-filepath env rest-api-id)]
        ;             (update-rest-api rest-api-id src-file region stage))
        ;           (println "Updated API" rest-api-id "(" stage ") in" region "is done."))

        "import" (doseq [{:keys [rest-api-id region]} deployments]
                   (let [src-file (api-definition-filepath env rest-api-id)
                         result (import-rest-api src-file region)
                         gen-id (.getId result)]
                     (println "Imported API" rest-api-id "in" region "is done. API" gen-id "is created.")))

        "export" (doseq [{:keys [rest-api-id region stage]} deployments]
                   (let [result (export-rest-api rest-api-id region stage)
                         api-definition (-> result .getBody .array String.)
                         dst-file (api-definition-filepath env rest-api-id)]
                     (make-parents dst-file)
                     (spit dst-file api-definition)
                     (println "Exported API" rest-api-id "(" stage ") is done. API definition" dst-file "is generated.")))

        (usage)))))
