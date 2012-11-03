rem Project
 curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\project.json --header Content-type:application/json http://localhost:8080/cassandra/api/prj
rem curl -i -X DELETE http://localhost:8080/cassandra/api/prj/50928a2e0f8b0b0dc2d6c9fc

rem curl -i -X GET http://localhost:8080/cassandra/api/prj/50928c000f8b0b0dc2d6c9fd

rem curl -i -X  PUT -d  "name=TestProject2" http://localhost:8080/cassandra/api/prj/50928c000f8b0b0dc2d6c9fd 