# Posters
Design notes:
 - [First](https://github.com/artkostm/posters/blob/master/FIRST_DN.md)
 - [GraphQL](https://github.com/artkostm/posters/blob/master/GRAPHQL.md)
 
Next steps:

1) Update Assignment table for volunteers and users

- Update primary key and add something like this `index("idx_a", (volunteerId, userId), unique = true)` if we wouldn't integrate with Redis

2) Need to decide whether we should use Redis or not (possible no)
3) Update request model for user assignment
4) Update GraphQL schemas
5) Secure API (with secured context or using the Middleware)
6) Update Slick queries as a good fit for GraphQL
7) Add logging

...To be continued

★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　　　.　 °☆ 　. ● ¸ .　　　★　° .　 • ○ ° ★　 .　　　　　　　*　.　 ☾ ° 　¸.* ● ¸ 　　　　° ☾ °☆ 　. * ¸.　　　★　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ °★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● 　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ . 　　　° ☾　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.. °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　° ☾ ° 　¸. ● ¸ .　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾
