#!/usr/bin/env python

from flask import Flask
from flask import request
app = Flask(__name__)

@app.route('/')
def home():
  return 'Home'

@app.route('/discover')
def discover():
  user_id = request.args.get('user_id')
  gps_latitude = request.args.get('gps_latitude')
  gps_longitude = request.args.get('gps_longitude')
  if user_id != None and gps_latitude != None and gps_longitude != None:
    user_id = int(user_id)
    gps_latitude = float(gps_latitude)
    gps_longitude = float(gps_longitude)
    return '[{ "user_id": %d, "gps_latitude": %f, "gps_longitude": %f}]' % (user_id, gps_latitude, gps_longitude)
  else:
    return 'Invalid data'

if __name__ == '__main__':
  app.run(debug=True)
