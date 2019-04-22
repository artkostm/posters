To check all the queries using Doobie, just run com.artkostm.posters.worker.sqlchecker class.

Here is the result (please note that you required to be connected to the real Postgres instance):
```shell
[scala-execution-context-global-11] INFO org.flywaydb.core.internal.util.VersionPrinter - Flyway Community Edition 5.1.4 by Boxfuse
[scala-execution-context-global-11] INFO org.flywaydb.core.internal.database.DatabaseFactory - Database: jdbc:postgresql://192.168.99.100:5432/postgres (PostgreSQL 9.6)
[scala-execution-context-global-11] INFO org.flywaydb.core.internal.command.DbValidate - Successfully validated 3 migrations (execution time 00:00.019s)
[scala-execution-context-global-11] INFO org.flywaydb.core.internal.command.DbMigrate - Current version of schema "public": 0003
[scala-execution-context-global-11] INFO org.flywaydb.core.internal.command.DbMigrate - Schema "public" is up to date. No migration necessary.
[pool-1-thread-1] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
[pool-1-thread-1] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
  Update0 defined at EventStoreInterpreter.scala:79
  DELETE FROM events WHERE events.eventdate < ?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date  →  DATE (date)
  Query0[Day] defined at EventStoreInterpreter.scala:61
  SELECT "eventdate", "categories" FROM events e WHERE e.eventdate=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date  →  DATE (date)
  ✓ C01 eventdate  DATE  (date)  NOT NULL  →  Date
  ✓ C02 categories OTHER (jsonb) NOT NULL  →  PGobject
  Query0[Category] defined at EventStoreInterpreter.scala:67
  SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE
  t.eventdate = ? AND j->>'name' IN (?)
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 String  →  VARCHAR (text)
  ✓ C01 j OTHER (jsonb) NULL?  →  PGobject
  Query0[Category] defined at EventStoreInterpreter.scala:67
  SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE
  j->>'name' IN (?)
  ✓ SQL Compiles and TypeChecks
  ✓ P01 String  →  VARCHAR (text)
  ✓ C01 j OTHER (jsonb) NULL?  →  PGobject
  Query0[Category] defined at EventStoreInterpreter.scala:67
  SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE
  t.eventdate > ? AND t.eventdate < ? AND j->>'name' IN (?)
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 Date    →  DATE    (date)
  ✓ P03 String  →  VARCHAR (text)
  ✓ C01 j OTHER (jsonb) NULL?  →  PGobject
  Update0 defined at EventStoreInterpreter.scala:57
  INSERT INTO events ("eventdate", "categories") VALUES (?, ?)
  ON CONFLICT ON CONSTRAINT pk_events DO UPDATE SET categories=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date      →  DATE  (date)
  ✓ P02 PGobject  →  OTHER (jsonb)
  ✓ P03 PGobject  →  OTHER (jsonb)
  Query0[Day] defined at EventStoreInterpreter.scala:64
  SELECT "eventdate", "categories" FROM events e WHERE e.eventdate > ?
  AND e.eventdate < ?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date  →  DATE (date)
  ✓ P02 Date  →  DATE (date)
  ✓ C01 eventdate  DATE  (date)  NOT NULL  →  Date
  ✓ C02 categories OTHER (jsonb) NOT NULL  →  PGobject
  Update0 defined at InfoStoreInterpreter.scala:43
  DELETE FROM info
  WHERE NOT EXISTS (SELECT * FROM events WHERE categories::jsonb::text
  LIKE '%' || info.link || '%')
  ✓ SQL Compiles and TypeChecks
  Query0[EventInfo] defined at InfoStoreInterpreter.scala:39
  SELECT "link", "eventInfo" FROM info WHERE link=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 String  →  VARCHAR (text)
  ✓ C01 link      VARCHAR (varchar) NOT NULL  →  String
  ✓ C02 eventInfo OTHER   (jsonb)   NOT NULL  →  PGobject
  Update0 defined at InfoStoreInterpreter.scala:33
  INSERT INTO info ("link", "eventInfo") VALUES (?, ?)
  ON CONFLICT ON CONSTRAINT pk_info DO
  UPDATE SET "eventInfo"=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 String    →  VARCHAR (varchar)
  ✓ P02 PGobject  →  OTHER   (jsonb)
  ✓ P03 PGobject  →  OTHER   (jsonb)
  Query0[Intents] defined at VisitorStoreInterpreter.scala:38
  SELECT "eventdate", eventname, vids, uids FROM visitors WHERE
  eventdate = ? AND eventname = ?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 String  →  VARCHAR (text)
  ✓ C01 eventdate DATE    (date)    NOT NULL  →  Date
  ✓ C02 eventname VARCHAR (varchar) NOT NULL  →  String
  ✓ C03 vids      ARRAY   (_text)   NOT NULL  →  Array[String]
  ✓ C04 uids      ARRAY   (_text)   NOT NULL  →  Array[String]
  Update0 defined at VisitorStoreInterpreter.scala:42
  INSERT INTO visitors ("eventdate", "eventname", "vids", "uids")
  VALUES
  (?, ?, '{}', '{}')
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 String  →  VARCHAR (varchar)
  Update0 defined at VisitorStoreInterpreter.scala:47
  DELETE FROM visitors WHERE eventdate < ?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date  →  DATE (date)
  Update0 defined at VisitorStoreInterpreter.scala:50
  UPDATE visitors
  SET uids=array_remove(uids, ?::text), vids=array_remove(vids,
  ?::text)
  WHERE eventname=? AND eventdate=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 String  →  VARCHAR (text)
  ✓ P02 String  →  VARCHAR (text)
  ✓ P03 String  →  VARCHAR (text)
  ✓ P04 Date    →  DATE    (date)
  Update0 defined at VisitorStoreInterpreter.scala:60
  INSERT INTO visitors ("eventdate", "eventname", "vids", "uids")
  VALUES (?, ?, string_to_array(?, ',', ''), '{}') ON CONFLICT ON
  CONSTRAINT pk_visitors DO UPDATE SET vids=visitors.vids || ?::text
  WHERE visitors.eventdate=? AND visitors.eventname=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 String  →  VARCHAR (varchar)
  ✓ P03 String  →  VARCHAR (text)
  ✓ P04 String  →  VARCHAR (text)
  ✓ P05 Date    →  DATE    (date)
  ✓ P06 String  →  VARCHAR (text)
  Update0 defined at VisitorStoreInterpreter.scala:60
  INSERT INTO visitors ("eventdate", "eventname", "vids", "uids")
  VALUES (?, ?, '{}', string_to_array(?, ',', '')) ON CONFLICT ON
  CONSTRAINT pk_visitors DO UPDATE SET uids=visitors.uids || ?::text
  WHERE visitors.eventdate=? AND visitors.eventname=?
  ✓ SQL Compiles and TypeChecks
  ✓ P01 Date    →  DATE    (date)
  ✓ P02 String  →  VARCHAR (varchar)
  ✓ P03 String  →  VARCHAR (text)
  ✓ P04 String  →  VARCHAR (text)
  ✓ P05 Date    →  DATE    (date)
  ✓ P06 String  →  VARCHAR (text)
[scala-execution-context-global-11] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Shutdown initiated...
[scala-execution-context-global-11] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Shutdown completed.
```
