rem Installation
curl -i -X GET http://localhost:8080/cassandra/api/prj
curl -i -X GET http://localhost:8080/cassandra/api/scn?prj_id=5093a1e10f8b46f1e30f6917
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\installation.json --header Content-type:application/json http://localhost:8080/cassandra/api/inst
curl -i -X GET http://localhost:8080/cassandra/api/inst/5093c0380f8b86179085ef7c
rem curl -i -X DELETE http://localhost:8080/cassandra/api/inst/5093c0380f8b86179085ef7c