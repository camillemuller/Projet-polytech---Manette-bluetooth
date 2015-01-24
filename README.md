# Projet-polytech---Manette-bluetooth

Description : 

Dans le cadre d'un projet Polytech nous avons développé une manette bluetooth avec un joystick, il vous permettra de commander un robot à partir des données Bluetooth envoyées.
Cette application fonctionne avec le module bluetooth HC-06.

#####

Commande envoyée sur la liaison Bluetooth :

• [CMD,X,Y,V] : Lors d'un mouvement du joystick CMD = Commande joystick, X = Valeur égale au variant de -100 à 100 sur l'axe X
,Y variant de -100 à 100 sur l'axe Y, égale à 1 en vitesse rapide, 0 en vitesse lent.

• [B] = Commande du BUZZER

• [BAT,NB]  = Envoyer depuis la plateforme arduino, NB = 0 à 100 fait varier l'icone de batterie sur l'appliation.

Toute les secondes , l'application envoie "[HB]\n", ceci permet de vérifier que la connexion est bien encore active sur la plateforme Arduino. 



Joystick : https://code.google.com/p/mobile-anarchy-widgets/ 
