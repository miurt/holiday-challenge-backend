# backend-challenge Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

# .CSV files (hotels.csv and offers.csv)
hotels.csv and offers.csv are not committed, they should be added to the resources folder (\src\main\resources). 

# Starting the application
./mvnw quarkus:dev

GET requests can be tested through
http://localhost:8080/q/swagger-ui/#/Challenge

# Limitations:
The application works for huge offers files, but is slow (currently about 10 mins for a 13,1 GB sample file)

Possible solutions for a faster implementation: reading information by bytes instead of line by line, multi-threading (split file into small parts, that can be processed parallel)