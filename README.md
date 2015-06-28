# webapp
A web-app that shows hystrix and curator in action

Also check greeting service

## zookeeper and curator
The webapp uses the curator library to lookup the greeting service in zookeeper

## hystrix
The service call (and lookup) is wrapped in a hystric command (GreetingCommand)
