#!/usr/bin/python
import smtplib
fromaddr='romer@infrno.net'
toaddrs='stats@infrno.net'
msg='oh boy!'

server = smtplib.SMTP('localhost')
server.set_debuglevel(1)
server.sendmail(fromaddr, toaddrs, msg)
server.quit()

