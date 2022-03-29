## Auth server
[![Build](https://github.com/Sepehr79/Auth-Server/actions/workflows/build.yml/badge.svg)](https://github.com/Sepehr79/Auth-Server/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sepehr79_Auth-Server&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Sepehr79_Auth-Server)

Simple authentication server implementation that can be used in microservices architecture.

The main purpose is to authenticate users based on usernames, passwords, roles and authorities, 
which is done with the help of [JWT](https://jwt.io/).
In this architecture, the username is actually the user's email, which is used to send the validation code via email.

#### Data storage
Two separate databases are used to manage the data
1. [Redis](https://redis.io/)
   * saving temporary data
2. [Mongodb](https://www.mongodb.com/)
   * saving permanent data

For a scenario, consider that the user's request is first stored in cache and a message containing the activation code is sent to his email.
After re-sending the activation code by the user, we store his information in permanent memory.

#### How to use
After running the application you can access the API documentation
from path `/auth/<version>/swagger-ui.html` witch you can change it from the `application.properties`.

Also, you need to enable _Less secure app access_ on your gmail service see [here](https://stackoverflow.com/a/64023055/13804680)







