# Reseau_Projet_2022

Pour lancer rapidement le serveur à partir d'un script il y a deux solutions

Dans le projet git :

Windows 
>Serveur : ./bindist/bin/Reseau_Projet_2022.bat

>Application Client : ./Application_Reseau/bindist/bin/Application_Reseau.bat

Mac / Linux
>Serveur : ./bindist/bin/Reseau_Projet_2022

>Application Client : ./Application_Reseau/bindist/bin/Application_Reseau

Dans le rendu :

Windows
>Serveur : Serveur STOMP/bindist/bin/Reseau_Projet_2022.bat

>Application Client : Application Client/bindist/bin/Application_Reseau.bat

Mac / Linux
>Serveur : Serveur STOMP/bindist/bin/Reseau_Projet_2022

>Application Client : Application Client/bindist/bin/Application_Reseau

## Serveur STOMP
IP Serveur : 127.0.0.1

Port : 9999

Endpoint : /

Le protocole STOMP permet à plusieurs acteurs de communiquer en utilisant le
paradigme publish/subscribe. Un client publie une information sur un topic, tous les
clients abonnés à ce topic reçoivent l’information.

## Application Client
Application cliente de dialogues (chat) anonyme éphémère.

Permet à un utilisateur de dialoguer avec d'autres clients connectés au même serveur STOMP que lui.
Les messages et les topics sont perdus à la fermeture de l'application. Cela permet d'effacer toute trace d'une discussion avec une personne.
