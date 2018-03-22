# GraphQL integration design

Useful links
 - [Sangria](https://github.com/sangria-graphql/sangria)
 - [Oficial site and documentation](http://sangria-graphql.org)
 - [Example with Finatra](https://github.com/chrisphelps/sangria-finatra)
 
 -----
 
 Request format (should be implemented, POST request):
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
GET request 
When receiving an HTTP GET request, the GraphQL query should be specified in the "query" query string. For example, if we wanted to execute the following GraphQL query:
```javascript
{
  me {
    name
  }
}
```
This request could be sent via an HTTP GET like so:
```http://myapi/graphql?query={me{name}}```

Query variables can be sent as a JSON-encoded string in an additional query parameter called variables. If the query contains several named operations, an operationName query parameter can be used to control which one should be executed.

Need to un/marshal input (variables): http://sangria-graphql.org/learn/#result-marshalling-and-input-unmarshalling

Authorization: http://sangria-graphql.org/learn/#authentication-and-authorisation and https://github.com/OlegIlyenko/sangria-auth-example

Maybe generate key and create tocken for each user logged in

For token, generate jwt using https://github.com/jasongoodwin/authentikat-jwt or https://github.com/input-output-hk/scrypto. Secret key should be set explicitly (for example, env variables). Claims: user role to defferentiate users (user sent secret and its status to server).


Create serializers for joda date time: https://github.com/sangria-graphql/sangria/issues/283 and https://gist.github.com/OlegIlyenko/a9c0e52540ce7090abaf


★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　　　.　 °☆ 　. ● ¸ .　　　★　° .　 • ○ ° ★　 .　　　　　　　*　.　 ☾ ° 　¸.* ● ¸ 　　　　° ☾ °☆ 　. * ¸.　　　★　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ °★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● 　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ . 　　　° ☾　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾ °★ . 　　★ ° . .　　　　.　☾ °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　　　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　　　　° ☾ ° 　¸. ● ¸ .　　★　° :.　 . • ° 　 .　 *　:.　.. °☆ 　. * ● ¸ .　　　★　° :.　 . • ○ ° ★　 .　 *　.　　. 　 ° 　. ● .　　　　° ☾ °☆ 　¸.● .　　★　　★ ° ☾ ☆ ¸. ¸ 　★　 :.　 . • ○ ° ★　 .　 *　.　.　　¸ .　　 ° 　¸. * ● ¸ .　° ☾ ° 　¸. ● ¸ .　★　° :.　 . • ° 　 .　 *　:.　.　¸ . ● ¸ 　　　★　　★☾
