Initialisation du projet BookHub

Ce projet est composé de deux parties :

* un **backend** (Spring Boot)
* un **frontend** (Angular)
* une base de données **Microsoft SQL Server**

1. Configuration de la base de données

Le projet utilise **Microsoft SQL Server** avec une base nommée :


BOOK_HUB


 Étapes :

1. Installer **Microsoft SQL Server** ainsi que **SQL Server Management Studio (SSMS)**

2. Créer la base de données :


CREATE DATABASE BOOK_HUB;
GO


3. Importer la base :

* soit via un fichier .sql fourni (exécuter le script dans la base BOOK_HUB)
* soit via un fichier .bak (Restore Database dans SSMS)

2. Configuration du backend (Spring Boot)

Modifier le fichier application.properties :

properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BOOK_HUB
spring.datasource.username=VOTRE_USERNAME
spring.datasource.password=VOTRE_PASSWORD

spring.jpa.hibernate.ddl-auto=none


3. Lancer le backend

Dans le dossier du projet backend :


./mvnw spring-boot:run

Le backend sera accessible sur :


http://localhost:8080


4. Lancer le frontend (Angular)

Dans le dossier frontend :

ng serve


Le frontend sera accessible sur :


http://localhost:4200


5. Communication entre frontend et backend

Le frontend Angular communique avec le backend via :


http://localhost:8080

Assurez-vous que le backend est lancé avant le frontend.



Résumé des URLs

* Frontend : http://localhost:4200
* Backend : http://localhost:8080
* Base de données : localhost:1433
* Nom de la BDD : BOOK_HUB


Prérequis

* Angular CLI
* Java 17+
* Gradle
* Microsoft SQL Server + SSMS


Notes

* Vérifiez que le port 1433 est bien actif pour SQL Server
* Ne pas oublier de configurer vos identifiants SQL Server
* Le backend doit être lancé avant le frontend

