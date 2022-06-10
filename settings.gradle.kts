
rootProject.name = "smallbank"

include("domain")
include("infra")
include("rest-api")

project(":domain").name = "smallbank-domain"
project(":infra").name = "smallbank-infra"
project(":rest-api").name = "smallbank-rest-api"
