# IRC_With_Python

Mini-projet : Serveur de discussion
Le but de ce mini-projet est de réaliser un service de discussion en ligne (Internet Relay Chatou IRC) en Python. 
Il s’agit d’un système client/serveur permettant à des utilisateurs de discuter en direct en s'envoyant des messages.
Les  utilisateurs  peuvent  discuter  en  groupe  à  travers  des canaux de  discussion,  mais  également  deux-à-deux  de maniére privée.
Le principe est assez simple : des utilisateurs se connectent à un serveur IRC en utilisant un programme client, tape des commandes et le serveur exécute ces commandes. 
Le réseau IRC est constitué de serveurs connectés entre eux, sans topologie particuliére. Chaque client se connecte à un des serveurs et les commandes (ou messages) qu’il tape sont communiquées par son serveur de rattachement aux autres serveurs, jusqu’aux clients destinataires. Les commandes  acceptées  par  un  serveur  sont  assez  nombreuses  et  tout  est  d ́ecrit  dans  un  protocole  d ́ecrit  dans  laRFC1459 : https://datatracker.ietf.org/doc/html/rfc1459.
Le guide suivant décrit de maniére succinte la grande majorité des commandes disponibles :https://fr.wikipedia.org/wiki/Aide:IRC/commandesComme

