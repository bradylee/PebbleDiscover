#!/usr/bin/env python

from flask import Flask, request, render_template, url_for
from User import User
from copy import deepcopy
from Requestor import Requestor

app = Flask(__name__)

@app.route('/')
def home():
  """Home page of the application"""
  return render_template('index.html')

@app.route('/register', methods=['POST'])
def register():
  """Register a new user"""
  
  # Make sure that the id does not exist and data is in a valid format
  R = {k: v for (k, v) in request.form.iteritems()}
  if R.has_key('_id'): return 'Error: Cannot set user id.\n'
  if not R.has_key('username'): return 'Error: Username required.\n'
  
  # Setup the user
  user = User()
  user.username = R['username']
  
  # Parse through the preferences and validate
  if R.has_key('preferenecs'):
    for k, v in R['preferences'].iteritems():
      if type(R['preferences'][k]) != list: return 'Error: Invalid input data\n'
      else:
        for i in range(len(R['preferences'][k])): 
          if type(R['preferences'][k][i]) != str: return 'Error: Invalid input data\n'
    
    # Create the user if doesn't exist
    user.preferences = R['preferences']
  try:
    user.create()
    user.store_preferences()
  except Exception, e:
    user.dbcursor.close()
    return 'Error: Could not create user.'
  user.dbcursor.close()
  
  return 'Success\n'

@app.route('/discover')
def discover():
  """Return all relevant queries from Google Maps"""
  username = request.args.get('username')
  gps_latitude = request.args.get('latitude')
  gps_longitude = request.args.get('longitude')
  if username != None and gps_latitude != None and gps_longitude != None:
    # Parse data
    user = User(username=username,location=gps_latitude+','+gps_longitude)
    if user.exists():
      # Perform search
      
      # LOAD USER PREFERENCES FROM MONGODB
      # user.load_preferences()
      req = Requestor(user)
      results = req.process()
      user.dbcursor.close()
      if results is not None: return str(results)
      else: return 'Error: Requests returned no data'
    else:
      user.dbcursor.close()
      return 'Error: Invalid user.\n'
  else:
    return 'Error: Invalid or not enough data. Exptecting id, latitude, and longitude.\n'
  return 'Success\n'

if __name__ == '__main__':
  app.run(debug=True)
