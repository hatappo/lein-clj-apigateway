(defproject lein-clj-apigateway "0.1.0-SNAPSHOT"
  :description "Leiningen plugin for AWS API Gateway deployment"
  :url "https://github.com/hatappo/clj-apigateawy-deploy"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.amazonaws/aws-java-sdk-core "1.10.72"]
                 [com.amazonaws/aws-java-sdk-api-gateway "1.10.72"]
                 [com.amazonaws/aws-apigateway-importer "1.0.1" :exclusions [com.amazonaws/aws-java-sdk-core joda-time]]]
                 ;[com.amazonaws/aws-apigateway-importer "1.0.3" :exclusions [com.amazonaws/aws-java-sdk-core joda-time]]]
  :eval-in-leiningen true)
