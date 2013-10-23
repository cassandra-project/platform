rem Installation
rem curl -i -X GET http://localhost:8080/cassandra/api/prj

 curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\washingmachine.json --header Content-type:application/json https://cassandra.iti.gr:8443/cassandra/api/app
rem curl -i -X GET http://localhost:8080/cassandra/api/pers?inst_id=5093c5920f8b86179085ef7d
rem  curl -i -X DELETE http://localhost:8080/cassandra/api/inst/5093c5920f8b86179085ef7d