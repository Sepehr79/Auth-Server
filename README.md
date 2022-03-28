## Auth server
[![Build](https://github.com/Sepehr79/Auth-Server/actions/workflows/build.yml/badge.svg)](https://github.com/Sepehr79/Auth-Server/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sepehr79_Auth-Server&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Sepehr79_Auth-Server)

Simple authentication server implementation that can be used in microservices architecture.

The main purpose is to authenticate users based on usernames, passwords, roles and authorities, 
which is done with the help of [JWT](https://jwt.io/).
In this architecture, the username is actually the user's email, which is used to send the validation code via email.



