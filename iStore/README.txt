# iStore - Application de gestion d'inventaire

##  Description
iStore est une application de gestion d'inventaire développée en Java avec une interface utilisateur Swing et une base de données MySQL. Elle permet de gérer les utilisateurs, les magasins, et les articles tout en offrant des rôles d'administration et des fonctionnalités avancées de gestion.

---

##  Fonctionnalités principales

- **Gestion des utilisateurs :**
  - Ajouter, modifier, et supprimer des utilisateurs.
  - Rôles supportés : `admin` et `employee`.
  - Inscription uniquement pour les emails autorisés via la liste blanche.

- **Gestion des magasins :**
  - Création et suppression des magasins (réservée aux administrateurs).

- **Gestion de l'inventaire :**
  - Ajouter, supprimer ou mettre à jour des articles dans l'inventaire.
  - Gérer les stocks : vendre ou recevoir des articles.
  - Chaque magasin possède un inventaire distinct.

---

##   Prérequis

1. **Logiciels requis :**
   - [Java JDK 8+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
   - [MySQL Server](https://dev.mysql.com/downloads/mysql/) (ou WAMP si utilisé sur Windows).
   - [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (facultatif pour la gestion graphique).
   - [WAMP](https://www.wampserver.com/) pour tester sur Windows.

---

##  Installation et configuration


### Étape 1 : Configurer la base de données

1. Ouvrez MySQL Workbench ou phpMyAdmin.
2. Importez le fichier SQL `istore.sql` fourni dans le dossier du projet : SOURCE path/to/istore.sql;
   (Remplacez path/to/istore.sql par le chemin du fichier SQL.)

   Vérifiez que les tables suivantes ont été créées :
   utilisateurs
   boutiques
   inventaire
   articles
   whitelisted_emails

---

### Étape 2 : Configurer le projet Java

   Importez le projet dans votre IDE préféré (IntelliJ IDEA, Eclipse, etc.).
   Assurezvous que le fichier DatabaseHelper est configuré correctement avec vos identifiants MySQL :

   private static final String DB_URL = "jdbc:mysql://localhost:3306/istore";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "votre_mot_de_passe";

   Ajoutez les fichiers JAR nécessaires (JUnit et BCrypt) dans le dossier lib ou via votre gestionnaire de dépendances.

     Utilisation

   1. Lancer l'application

   Exécutez la classe principale (Main.java) dans votre IDE ou via la ligne de commande :

   java -cp .;lib/* Main

   2. Accès administrateur

   Email : admin@istore.com
   Mot de passe : Password123

   3. Ajouter un nouvel utilisateur

   Assurez-vous que l'email est présent dans la table whitelisted_emails.

   Structure du projet

   lib/ : Bibliothèques nécessaires.
   src/
     database/ : Connexion et gestion MySQL.
        istore.sql : Fichier de base de données.

     view/ : Interfaces utilisateur (Swing).
     model/ : Modèles d'entités (ex. User, Article).


--

     Contributeurs

   Nom : CHAKIR ANICE

--

     Support

   Pour toute question ou problème, veuillez contacter anice.chakir@supinfo.com

--