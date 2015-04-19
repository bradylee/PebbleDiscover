#!/usr/bin/env python

from flask import Flask, request, render_template, url_for
from User import User
from copy import deepcopy

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
  del R['username']
  
  # Parse through the preferences and validate
  for k, v in R.iteritems():
    if type(R[k]) != list: return 'Error: Invalid input data\n'
    else:
      for i in range(len(R[k])):
        if i != len(R[k])-1: 
          if type(R[k][i]) != str: return 'Error: Invalid input data\n'
        elif type(R[k][i]) != str and type(R[k][i]) != list:
          return 'Error: Invalid input data\n'
  
  # Create the user if doesn't exist
  user.preferences = R
  try:
    user.create()
  except Exception, e:
    return 'Error: Could not create user.'
  
  return 'Success\n'

@app.route('/discover')
def discover():
  """Return all relevant queries from Google Maps"""
  user_id = request.args.get('id')
  gps_latitude = request.args.get('latitude')
  gps_longitude = request.args.get('longitude')
  if user_id != None and gps_latitude != None and gps_longitude != None:
    # Parse data
    user_id = int(user_id)
    gps_latitude = float(gps_latitude)
    gps_longitude = float(gps_longitude)
    user = User(user_id=user_id)
    if user.load_preferences() == None:
      # Perform search
      pass
    else:
      return 'Error: Invalid user.\n'
  else:
    return 'Error: Invalid or not enough data. Exptecting id, latitude, and longitude.\n'
  return 'Success\n'

if __name__ == '__main__':
  app.run(debug=True)
