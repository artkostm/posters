# Posters
Design notes
------

Table: <b>events</b>
- category 
- date 
- event_name 
- ids
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
For REST

POST /posters/categories/
{
	"user_id":"",
	"category":"",
	"event_name":"",
	"date":""
}

if there is a row with the combination (category, event_name, date)
then update that row
otherwise create new row with the information received
