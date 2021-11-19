# Room occupancy manager

---
Room occupancy optimization - simple tool to allow customers to calculate how much can they earn from their offered 
rooms. Contract for the API endpoint can be found [here.](contract/openapi-spec.yml)
---
To run tests use:

`./gradlew test`

---
Possible things to improve:
- Room guests is an interface that is not implemented and application cannot be run. (Only tests work where this
  interface is mocked). With the business requirements this can be changed into required solution (i.e.
  database call or external api call)
- Strategy configuration, with business knowledge such strategy could be extracted into separate endpoint, parameter
  in api call or anything else that is required by clients.
- Add support for different currencies. As of now the API is bound to one currency , but if needed it can be adjusted so
  that clients can use different currencies in their calls.
- Implement open-api code generator so that the documentation drives the development.
