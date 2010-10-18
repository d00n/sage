import MySQLdb

# TODO: run this under https, and inspect credentials passed via POST
def newRoomId(req):
  conn = MySQLdb.connect(host='localhost',user='sage_rw',passwd='sk00bysnack99',db='sage')
  cursor = conn.cursor()
  cursor.execute('insert into room (created_on) values (now())')
  room_id = conn.insert_id()
  conn.close()
  return room_id
