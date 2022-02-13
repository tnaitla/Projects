# IRC_With_Python

Mini-projet : Serveur de discussion
Le but de ce mini-projet est de réaliser un service de discussion en ligne (Internet Relay Chatou IRC) en Python. 

Il s’agit d’un système client/serveur permettant à des utilisateurs de discuter en direct en s'envoyant des messages.
Les  utilisateurs  peuvent  discuter  en  groupe  à  travers  des canaux de  discussion,  mais  également  deux-à-deux  de maniére privée.
Le principe est assez simple : des utilisateurs se connectent à un serveur IRC en utilisant un programme client, tape des commandes et le serveur exécute ces commandes. 

Le réseau IRC est constitué de serveurs connectés entre eux, sans topologie particuliére. Chaque client se connecte à un des serveurs et les commandes (ou messages) qu’il tape sont communiquées par son serveur de rattachement aux autres serveurs, jusqu’aux clients destinataires. Les commandes  acceptées  par  un  serveur  sont  assez  nombreuses  et  tout  est  d ́ecrit  dans  un  protocole  d ́ecrit  dans  laRFC1459 : https://datatracker.ietf.org/doc/html/rfc1459.

Le guide suivant décrit de maniére succinte la grande majorité des commandes disponibles :https://fr.wikipedia.org/wiki/Aide:IRC/commandesComme

Comme  vous  pouvez  le  voir,  les  commandes  ont  la  forme  suivante /commande <arguments>,  ou <arguments> représente une liste d’arguments (cette liste pouvant etre vide). 
  
Dans ce projet, nous allons implémenter un petit sous-ensemble de ces commandes:
  

1.   /away [message] Signale son absence quand on nous envoie un message en priv ́e(en réponse un message peut etre envoyé). Une nouvelle commande/away réactive l’utilisateur
  
2.   /helpAffiche la liste des commandes disponibles
  
3.   /invite <nick>Invite un utilisateur sur le canal ou on se trouve
  
4.   /join <canal> [clé]Permet de rejoindre un canal (protégé  éventuellement par une clé).Le canal est créé s’il n’existe pas
  
5.  /listAffiche la liste des canaux sur IRC
  
6.  /msg [canal|nick] message Pour envoyer un message à un utilisateur ou sur un canal (ou on est présent ou pas). Les arguments canal ou nick sont optionnels
  
7.  /names [channel] Affiche les utilisateurs connectés à un canal. Si le canal n’est pas spécifié,affiche tous les utilisateurs de tous les canaux.
  
![quit](https://user-images.githubusercontent.com/96243303/153772572-9932d43a-71b9-4e2f-83a1-6fbe26cf5c26.png)

