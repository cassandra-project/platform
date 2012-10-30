rem Project
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\project.json --header Content-type:application/json http://localhost:8080/cassandra/api/prj
curl -i -X GET http://localhost:8080/cassandra/api/prj
curl -i -X GET http://localhost:8080/cassandra/api/prj/500d0d47e4b087bdfeda08ff

rem Scenario
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\scenario.json --header Content-type:application/json http://localhost:8080/cassandra/api/scn
curl -i -X GET http://localhost:8080/cassandra/api/scn?prj_id=500d0d47e4b087bdfeda08ff

rem Sim Params
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\simparams.json --header Content-type:application/json http://localhost:8080/cassandra/api/smp/
curl -i -X GET http://localhost:8080/cassandra/api/smp?scn_id=500d1133e4b087bdfeda0900
curl -i -X GET http://localhost:8080/cassandra/api/smp/500d14b9e4b087bdfeda0901

rem Installation
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\installation.json --header Content-type:application/json http://localhost:8080/cassandra/api/inst
curl -i -X GET http://localhost:8080/cassandra/api/inst/500d1964e4b087bdfeda0902

rem Appliance
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\refridgerator.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\laptop.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\tv.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\vacuum.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\washing.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
rem insert the laptop again
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\laptop.json --header Content-type:application/json http://localhost:8080/cassandra/api/app
rem and then delete it
curl -i -X DELETE http://localhost:8080/cassandra/api/app/500d2108e4b04cef9589f6ca


rem Activities

curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\washing.json --header Content-type:application/json http://localhost:8080/cassandra/api/actD
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\entertainment1.json --header Content-type:application/json http://localhost:8080/cassandra/api/act
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\working.json --header Content-type:application/json http://localhost:8080/cassandra/api/act
rem Check the insertions
curl -i -X GET http://localhost:8080/cassandra/api/act?pers_id=500d2e96e4b04cef9589f6d2


rem example:
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\enter1.start.json --header Content-type:application/json http://localhost:8080/cassandra/api/distr
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\enter1.dur.json --header Content-type:application/json http://localhost:8080/cassandra/api/distr
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\enter1.day.json --header Content-type:application/json http://localhost:8080/cassandra/api/distr
curl -i --data @C:\workspace\platform\tests\TestAHouseScenario\enter1.end.json --header Content-type:application/json http://localhost:8080/cassandra/api/distr





