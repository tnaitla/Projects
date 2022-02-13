import threading
import socket
from _thread import * 
import sys
import re

host = 'localhost' 

help = "/away[message] : Signale son absence quand on nous envoie un message en prive &(en reponse un message peut etre envoyer) & Une nouvelle commande/away reactive l’utilisateur. /help : Affiche la liste des commandes_disponibles. /invite <nick> : Invite un utilisateur sur le canal ou on se trouve. /join <canal> [clé] : Permet de rejoindre un canal (protege eventuellement par une cle). & Le canal est cree s’il n’existe pas. /list: Affiche la liste des canaux sur IRC, /msg [canal|nick] message  : Pour envoyer un message a un utilisateur ou sur un canal (ou on est present ou pas).& Les arguments canal ou nicksont optionnels. /names [channel] : Affiche les utilisateurs connectes a un canal.& Si le canal n’est pas specifie affiche tous les utilisateurs de tous les canaux. /quit <canal> : quitte un canal en informant tt les utilisateurs connecter "

def concat_string (tab , sep = " ") : 
    res= "" 
    for i in range (0,len(tab)) :
        if i == 0 : 
            res =res + tab[i] 
        else : 
            res = res + sep + tab[i] 
    return res 


def read(n) :
    c = serveur.client[n][0]
    while True:
        m = c.recv(255)
        message = m.decode('utf8')
        print(message) 
        
        #######################################
        #######   Help     ###################
        #####################################
        if message.startswith("/help") : 
            c.send(help.encode('utf8'))

        #######################################
        #######   away     ###################
        #####################################
        
        if message.startswith("/away") :
            serveur.client[n][1] = not serveur.client[n][1]
            

        #######################################
        #######   join     ###################
        #####################################
        if message.startswith("/join") : 
            tab_message = message.split(" ")
            print(tab_message)
            if len (tab_message)  != 2 :
                message_to_send = "this command is not correct use: /join #namechannel"
                c.send(message_to_send.encode('utf8'))
            elif tab_message[1]!='':
                channel_to_join  = message.split(" ")[1]
                print(channel_to_join)
                if channel_to_join[0] == "#" and (len (channel_to_join)> 1)  :
                    if channel_to_join in serveur.canaux.keys() : 
                        serveur.canaux[channel_to_join].append(n)
                    else : 
                        serveur.canaux[channel_to_join] = [n]
                else:
                    message_to_send = "this command is not correct use: /join #namechannel"
                    c.send(message_to_send.encode('utf8'))
            else:
                message_to_send = "this command is not correct use: /join #namechannel"
                c.send(message_to_send.encode('utf8'))




        
        
        #######################################
        #######  quit      ###################
        #####################################
        if message.startswith("/quit") : 
          #  channel_to_quit  = message.split(" ")[1]
            if len (message.split(' ') )  == 1 :
                for channel__ in serveur.canaux.keys() : 
                    if n in serveur.canaux[channel__] : 
                        serveur.canaux[channel__].remove(n) 
                        message_to_send = "You left the channel : " + channel__
                        c.send(message_to_send.encode('utf8'))
                        client_to_send  =  serveur.canaux[channel__] 
                        message_to_send= "user " + n + " left this channel : " + channel__
                        for name_c in client_to_send : 
                               c_s = serveur.client[name_c]
                               if c_s[1] == True  and name_c != n : 
                                     c_s[0].send(message_to_send.encode('utf8'))


            
           
            elif not str(message.split(" ")[1]) in list(serveur.canaux.keys()) : 
                message_to_send = "this channel : " + message.split(" ")[1]  + " don't exist" 
                c.send(message_to_send.encode('utf8'))            
            elif  n in serveur.canaux[message.split(" ")[1]] : 
                
                serveur.canaux[message.split(" ")[1]].remove(n)
                message_to_send = "You left the channel : " + message.split(" ")[1]
                c.send(message_to_send.encode('utf8'))
                client_to_send  =  serveur.canaux[message.split(" ")[1]] 
                message_to_send= "user " + n + " left this channel : " + message.split(" ")[1]
                for name_c in client_to_send : 
                    c_s = serveur.client[name_c]
                    if c_s[1] == True  and name_c != n : 
                        c_s[0].send(message_to_send.encode('utf8'))
            else : 
                message_to_send = "You are not in this channel : " + message.split(" ")[1]
                c.send(message_to_send.encode('utf8'))
        

        #######################################
        #######  msg       ###################
        #####################################        
        if message.startswith('/msg') : 
            tab_message = message.split(" ") 
            channel_or_nick = tab_message[1]

            if channel_or_nick[0] == "#" :  # on est dans le cas ou l'argument est channel 
                if not str(channel_or_nick) in list(serveur.canaux.keys()) :
                    message_to_send = "this channel : " + channel_or_nick  + " don't exist" 
                    c.send(message_to_send.encode('utf8'))   
                else : 
                    client_to_send  =  serveur.canaux[channel_or_nick] 
                    for name_c in client_to_send : 
                        c_s = serveur.client[name_c]
                        if c_s[1] == True  and name_c != n : 
                            c_s[0].send((n + " : "+ concat_string(tab_message[2:])).encode('utf8'))
            else : 

                if channel_or_nick in serveur.client.keys()  :  # si l'argument est un autre client 
                    
                    c_s = serveur.client[channel_or_nick]
                    if c_s[1] == True : 
                        c_s[0].send((n + " : " + concat_string(tab_message[2:])).encode('utf8'))
                    else : 
                        message_to_send = channel_or_nick + ' is away'
                        c.send(message_to_send.encode('utf8'))
                else :  # dans le cas ou il y a pas d'arguments 
                    for name_c in serveur.client.keys() :
                        c_s = serveur.client[name_c]
                        if c_s[1] == True  and name_c != n :
                            c_s[0].send((n + " : " + concat_string(tab_message[1:])).encode('utf8'))

        #######################################
        #######  list      ###################
        #####################################
        if message.startswith('/list') : 
            message_to_send = "[" + concat_string(list(serveur.canaux.keys()) , sep  = " - ") + "]"
            c.send(message_to_send.encode('utf8'))

        #######################################
        #######  name      ###################
        #####################################
        if message.startswith('/names') : 
            tab_message = message.split(" ")
            if len(tab_message) == 1 :
                message_to_send = "[" + concat_string(list(serveur.client.keys()) , sep  = " - ") + "]"
                c.send(message_to_send.encode('utf8')) 
            else : 
                #if tab_message[1][0] == "#" : 
                if tab_message[1] in serveur.canaux.keys():
                    message_to_send  = "[" + concat_string(serveur.canaux[tab_message[1]] , sep  = " - " ) + "]" 
                    c.send(message_to_send.encode('utf8'))
                else:
                    message_to_send = "this channel : " + tab_message[1]  + " don't exist" 
                    c.send(message_to_send.encode('utf8')) 



        #######################################
        #######  invite    ###################
        #####################################
        if message.startswith('/invite') : 
            tab_message= message.split(" ")
            if len(tab_message)  != 3 : 
                message_to_send = "Your command is incorrect"
                c.send(message_to_send.encode('utf8'))
            else : 
                if not tab_message[1]  in (serveur.canaux.keys()) :
                    message_to_send = "the channel " + tab_message[1] + " is not exist "
                    c.send(message_to_send.encode('utf8'))
                else : 
                    if not n in serveur.canaux[tab_message[1]] : 
                        message_to_send ="you are not in this channel" + tab_message[1] + ", you can't invite other users "
                        c.send(message_to_send.encode('utf8'))
                    else : 
                        if not tab_message[2] in serveur.canaux[tab_message[1]] :
                            serveur.canaux[tab_message[1]].append(tab_message[2])
                            message_to_send = n + " added you in this channel " + tab_message[1]
                            serveur.client[tab_message[2]][0].send(message_to_send.encode('utf8'))
                        else: 
                            message_to_send = tab_message[2] +" is already in the " + tab_message[1] + " channel . "
                            c.send(message_to_send.encode('utf8'))
            print(serveur.canaux)









                

                
            
            






class serveur_irc :
    def __init__ (self , server_name , list_serveur = [] )  :
        self.server_name = int(server_name) 
        self.list_serveur = list_serveur 
        self.canaux = {"#default" : [] }
        self.client = {}
        self.s_serveur = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s_serveur.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
        self.s_serveur.bind((host,self.server_name))
        self.thread_read = threading.Thread(target=read,args=(self.s_serveur,))
    


serveur = serveur_irc (sys.argv[1])
#serveur.s_serveur.bind((host,serveur.server_name))
serveur.s_serveur.listen(20)

while True:
    c,a = serveur.s_serveur.accept()
    name_client = c.recv(255).decode('utf8')
    print('Test ok ' , name_client)
    serveur.canaux['#default'].append(name_client)
    serveur.client[name_client] = [c, True]
    print(serveur.canaux)
    print(serveur.client)
    start_new_thread(read , (name_client,)) 

   # t= threading.Thread(target=read,args=(c,))
   # t.start()
serveur.s_serveur.close() 
