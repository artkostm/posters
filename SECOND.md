Next steps:

1) Update Assignment table for volunteers and users

- Update primary key and add something like this `index("idx_a", (volunteerId, userId), unique = true)` if we wouldn't integrate with Redis

2) Need to decide whether we should use Redis or not (possible *no*)
3) Update request model for user assignment
4) Update GraphQL schemas
5) Secure API (with secured context or using the Middleware)
6) Update Slick queries as a good fit for GraphQL
```scala
for {
	days <- Table.map(_.categories.arrayElements)
	if days ~>>"name" isSetBind(names)
} yield days
```

7) Add logging
