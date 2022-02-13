import threading
import socket
import sys 
from tkinter import *
import random 

host = 'localhost'
affiche_line = 2 

colors = ["dodgerblue" , 'orange' , "#ff286e"]

def read(c):
    global affiche_line
    while True:
        m = c.recv(255)
        print(m.decode('utf'))
        to_print = " \n"
        to_print = to_print +  m.decode('utf8') 
        Text_out.insert(END, to_print)
        Text_out.tag_add(to_print, str(affiche_line) +".0", str(affiche_line) +".1000000")
        Text_out.tag_config(to_print, background="gainsboro", foreground="black")
        affiche_line = affiche_line + 1

class client_irc : 
    def __init__ (self , nickname ,server_name )  : 
        self.nickname =  nickname 
        self.server_name = server_name   # c'est le port
        self.s_client =  socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s_client.connect((host,int(server_name)))
        self.s_client.send(self.nickname.encode('utf8'))
        self.thread_read = threading.Thread(target=read,args=(self.s_client,))



def command_to ( ) : 
    global affiche_line
    message = input_message.get()
    
    client.s_client.send(message.encode("utf8"))
    message =  "\n"  + message 
    input_message.delete(0,END)
    Text_out.insert(END , message ) 
    Text_out.tag_add(message, str(affiche_line) +".0", str(affiche_line)+".1000000")
    Text_out.tag_config(message, background="blue", foreground="white")
    affiche_line = affiche_line + 1

client = client_irc(sys.argv[1] , sys.argv[2])
client.thread_read.start()




random_indice = random.randrange(0,3)
accent_color = colors[random_indice]
fen = Tk()   ## creation de la la fenetre graphique 
title = "IRC User : " +  client.nickname 
fen.title(title)  # donner un titre à la fenetre 
fen.geometry("360x400")  # la taille de la fenetre 
fen.config(background = "black")

frame_write =  Frame(fen, width =  360, height = 50  , relief = RAISED , bd  =6 ) 
frame_write.config(background =  accent_color)
frame_write.place(x = 0, y = 365 )

input_message = Entry(frame_write , width = 30 ,relief = RAISED , bd = 2 , font = "ARIAL 12")
input_message.place(x = 0, y = 0 )

button_send = Button(frame_write , width = 1 ,text = "send" ,relief = RAISED, bd = 2 ,fg = 'white' , bg = "green" , command = command_to)
button_send.place(x = 310, y = 0)

frame_out =  Frame(fen, width =  350, height = 350  , relief = RAISED  ,bd  =6 , bg =  accent_color) 
frame_out.place(x =5, y = 5 )


Text_out = Text(frame_out, width = 41 , height = 19,spacing1 = 1, font = ("Times", '12' , 'bold'))
Text_out.place(x = 0 , y = 0 )






#while True:
    #m = input('>')
    #if m == 'end':
        #client.s_client.close()
#button_send.invoke() 
    #client.s_client.send(m.encode('utf8'))



fen.mainloop()



    
