rem curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\project.json --header Content-type:application/json http://localhost:8080/cassandra/api/prj
remcurl -i -X GET http://localhost:8080/cassandra/api/prj

rem Scenario
 curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\scenario.json --header Content-type:application/json http://localhost:8080/cassandra/api/scn
rem curl -i -X GET http://localhost:8080/cassandra/api/scn?prj_id=5093a1e10f8b46f1e30f6917
rem curl -i -X  PUT -d  "name=TestProject2" http://localhost:8080/cassandra/api/scn/5093a8a70f8b46f1e30f6919 
curl -i -X DELETE http://localhost:8080/cassandra/api/scn/5093a3210f8b46f1e30f6918