import MySQLdb

# TODO: run this under https, and inspect credentials passed via POST
def newRoomId(req):
  conn = MySQLdb.connect(host='sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com',user='root',passwd='sk00bysnack',db='sage')
  cursor = conn.cursor()
  cursor.execute('insert into room (created_on) values (now())')
  room_id = conn.insert_id()
  conn.close()
  return room_id

def getLatestImageURL(req):
  conn = MySQLdb.connect(host='sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com',user='root',passwd='sk00bysnack',db='sage')
  cursor = conn.cursor()
  cursor.execute('select path from image order by created_on desc limit 1')
  row = cursor.fetchone()
  path = row[0]
  conn.close()

  url = "http://wowza.infrno.net%s" % row[0]
  return '<html><head><meta http-equiv="refresh" content="60; URL=http://wowza.infrno.net/services/getLatestImageURL"></head><img width="100%%" src="http://wowza.infrno.net%s"></html>' % row[0]
