# GraphQL integration design

Useful links
 - [Sangria](https://github.com/sangria-graphql/sangria)
 - [Oficial site and documentation](http://sangria-graphql.org)
 - [Example with Finatra](https://github.com/chrisphelps/sangria-finatra)
 
 -----
 
 Request format (should be implemented):
 ```json
 {
  "query": "...",
  "operationName": "...",
  "variables": { "myVariable": "someValue", ... }
}
 ```
 Response format (no need to implement, just for fun):
 ```json
 {
  "data": { ... },
  "errors": [ ... ]
}
 ```

★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　　　.　 °☆ 　. ● ¸ .　　　★　° .　 • ○ ° ★　 .　　　　　　　*　.　 ☾ ° 　¸.* ● ¸ 　　　　° ☾ °☆ 　. * ¸.　　　★　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ °★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● 　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ . 　　　° ☾　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.. °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　° ☾ ° 　¸. ● ¸ .　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾
