# First design notes

Design notes
------

Table: <b>events</b>
- category 
- date 
- event_name 
- ids
******
Integration with Heroku Postgres

The DATABASE_URL for the Heroku Postgres add-on follows this naming convention:
```regex
postgres://<username>:<password>@<host>/<dbname>
```
However the Postgres JDBC driver uses the following convention:
```regex
jdbc:postgresql://<host>:<port>/<dbname>?user=<username>&password=<password>
```

```java
private static Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI(System.getenv("DATABASE_URL"));

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
}
```
******
Add Postgres driver:
```sbt
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1208"
```
And add configuration
```HOCON
db {
    url = ${DATABASE_URL}
}
```
After that add configuration
```scala
case class DbConfig(url: String, user: String, password: String)
...
lazy val db = {
    val uri = new URI(config.getString("db.url"))
    val user = uri.getUserInfo().split(":")(0);
    val password = uri.getUserInfo().split(":")(1);
    DbConfig(s"jdbc:postgresql://${uri.getHost()}:${uri.getPort()}${uri.getPath()}", user, password)
}
```
******
Table model:
```scala
val events: TableQuery[Events] = TableQuery[Events]
```
******
Prepare DB:
```scala
val tables = List(events)
val createIfNotExist = db.run(MTable.getTables).map( v => {
    val names = v.map(_.name.name)
    tables.filter(table => !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
    //db.run(DBIO.sequence(createIfNotExist))
})
val deleteOldEvents: Query[Events,(String, String, String, String), Seq] = events.filter{event =>
	DateTime.parse(event.date) < DateTime.now
}
val setupAction: DBIO[Unit] = DBIO.seq(createIfNotExist, deleteOldEvents.delete)
val setupFuture: Future[Unit] = db.run(setupAction)
```
******
Create finatra module to start db creation process:
```scala
class Server extends HttpServer {
  override val modules = Seq(DbStartupModule)
  ...
}
```
And here is the module itself:
```scala
object DbStartupModule extends TwitterModule {
  override def singletonStartup(injector: Injector) {
    // initialize JVM-wide resources
  }
  override def singletonShutdown(injector: Injector) {
    // shutdown JVM-wide resources
  }
  override def singletonPostWarmupComplete(injector: Injector) {
    // perform functions that need to happen after we've bound
    // ports but before the server has started
  }
}
```
******
For REST
```curl
POST /posters/categories/
```
```json
{
	"user_id":"",
	"category":"",
	"event_name":"",
	"date":""
}
```
******
- if there is a row with the combination (category, event_name, date)
-- then update that row
- otherwise create new row with the information received
******
For event info scraping:
```haskell
media.link -> 
	#event-description => String
	.fotorama__img => List[String] (can be empty)
	#comments => List[Comment] (can be empty)
		.comments__content => Comment
			head .author => String
			head .date => String
			.comment_txt => String
```
★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　　　.　 °☆ 　. ● ¸ .　　　★　° .　 • ○ ° ★　 .　　　　　　　*　.　 ☾ ° 　¸.* ● ¸ 　　　　° ☾ °☆ 　. * ¸.　　　★　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ °★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● 　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ . 　　　° ☾　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.. °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　° ☾ ° 　¸. ● ¸ .　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾
