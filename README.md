
## Important Note
I developed the database, the classes, and the logging according to my understanding as the assessment document was too brief. Also I didn't expand it much as it was mentioned. (Simple Sales System)

## Documentation
I placed comments mainly in places where there is some logic and it needs to be clarified.
Code that is straight forward and is understood only by its naming is left out uncommented.

## Maintainability
Alongside documentation, I followed a good naming convention in both ends to keep the code maintainable.

## Front-end Part

### State Management
I used Ngrx Store for state-management, and together with the pwa, the app is efficiently fetching and storing the users.

### Caching/ Pwa
To be able to run caching, the app must be built and then run by http-server -p port-number (ex: http-server -p 8081) in the build folder (dist)

---

## Back-end Part

### Architecture
I implemented a microservices architecture, were I split the APIs from each other, each having a controller, DAO, and a DAO Implementation.

### Error Handling
I created a global controller advice to handle http requests errors, and a Custom Exception class to have a unified Error response structure for all error responses.

### Models and Records
Alongside the custom exception class, I created the database tables as classes in a model package, and 3 records in a record in package as a DTO (Data Transfer Object), to have a good and maintainable structure for the API endpoints parameters and responses.

## Database

### MySQL and JDBC
I used MySQL for my database and jdbc for its connection with spring boot.

### Structure
I created 5 tables in total, and their code can be found in the SQL Queries file.

## Class Diagram

### I considered that the clients can exist even without having a purchase/sale, as they would have signed up, but didn't do a sale/purchase yet. 
### I considered that a transaction once created shouldn't have a transaction log because the transaction didn't get an update yet.
### I considered that a product can exist without a transaction as it can be present, but not purchased by anyone yet.