rem Activity
 curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\washing.json --header Content-type:application/json http://localhost:8080/cassandra/api/act
 rem curl -i -X GET http://localhost:8080/cassandra/api/pers?inst_id=5093c5920f8b86179085ef7d
rem curl -i -X GET http://localhost:8080/cassandra/api/act?pers_id=5093c78e0f8b86179085ef7f
 rem curl -i -X DELETE http://localhost:8080/cassandra/api/inst/5093c5920f8b86179085ef7d