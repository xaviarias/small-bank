
rootProject.name = "small-bank"

include("rest-api")
include("dlt")

project(":rest-api").name = "${rootProject.name}-rest-api"
project(":dlt").name = "${rootProject.name}-dlt"
