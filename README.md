# backend-challenge Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

# .CSV files (hotels.csv and offers.csv)
hotels.csv and offers.csv are not committed, they should be added to the resources folder (\src\main\resources). 

## Splitting for faster performance
New version: offers.csv is splitted in several csv files according to days in an offer e.g. \splitted_files\offers_7days.csv  for 7-days-vacations. Splitting is normally done during the first request (if haven't done before) or can be potentially done automatically on the application's start (now can be done manually through http://localhost:8080/api/search/split or from swagger-ui).

Pros: after splitting it is possible to get big lists of offers within seconds

Cons: splitting of huge files requires a lot of time (between 1-2 hours for a 13,1 GB sample file). This should be taken into consideration during deployment (acceptable if needed to be executed only once or once in several days, but useless if needed to update often).

# Starting the application
./mvnw quarkus:dev 

GET requests can be tested through
http://localhost:8080/q/swagger-ui/#/Challenge

# Limitations:
Old version: ~~The application works for huge offers files, but is slow (currently about 10 mins for a 13,1 GB sample file)~~
Now: Slow splitting

Possible solutions for a faster implementation: reading information by bytes instead of line by line, multi-threading (split file into small parts, that can be processed parallel)

Possble steps for development: adding data from csv to a database
