# lein-clj-apigateway

A Leiningen plugin to import/export rest api definitions on AWS API Gateway.

## Usage

N.B. Importing to existing rest api is not currently supported. see [this plugin]().

Install the plugin into your local.

    git clone git@github.com:hatappo/lein-clj-apigateway.git
    cd lein-clj-apigateway
    lein install


Create following configuration into `project.clj`

    :apigateway
      {"development" [{:region "ap-northeast-1"  ; Your region in develpment.
                       :rest-api-id "xxxxxxxxxx" ; Your rest api id in develpment.
                       :stage "dev"}]})          ; Target api stage.
      {"production" [{:region "ap-northeast-1"   ; Your region in production.
                      :rest-api-id "yyyyyyyyyy"  ; Your rest api id in production.
                      :stage "prod"}]})          ; Target api stage.

Then run

    $ lein apigaetway export prod

`export` task that will export swagger json of the rest api definition and write to a file.
And run

    $ lein lambda update production

`import` task that will import swagger json file of the rest api definition and create new rest api.
