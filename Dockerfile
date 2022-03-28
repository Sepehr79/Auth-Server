FROM maven:3.8.4-openjdk-11
WORKDIR ./app
COPY . .
CMD ["mvn", "spring-boot:run"]
