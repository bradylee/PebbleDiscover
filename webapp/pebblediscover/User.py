#!/usr/bin/env python

import pymongo
from pymongo import MongoClient
import json
from copy import deepcopy

class User():
  
  DEFAULT_PREFERENCES = { \
    'restaurants': [  \
        'Type1',  \
        'Type1',  \
        'Type1'], \
    'music': [ \
        '',  \
        '',  \
        '' ],
    'music': [ \
        '',  \
        '',  \
        '' ],
    'music': [ \
        '',  \
        '',  \
        '' ],
    'music': [ \
        '',  \
        '',  \
        '' ],
    'music': [ \
        '',  \
        '',  \
        '' ],
    'music': [ \
        '',  \
        '',  \
        '' ]}
  
  DEFAULT_DBNAME = 'pebblediscover'
  DEFAULT_CNAME = 'preferences'
  
  def __init__(self, user_id=None, username=None, dbname=DEFAULT_DBNAME, collection_name=DEFAULT_CNAME, preferences=DEFAULT_PREFERENCES, host='localhost', port='27017'):
    self.user_id = user_id
    self.username = username
    
    self.preferences = dict()
    
    self.dbname = dbname
    self.collection_name = collection_name
    
    # Open a database connection
    self.dbcursor = None
    self.db = None
    self.collection = None
    try:
      self.dbcursor = MongoClient('mongodb://%s:%s' % (str(host), str(port)))
      self.db = self.dbcursor[self.dbname]
      self.collection = self.db[collection_name]
    except Exception, e:
      print 'Error opening mongodb cursor and connecting to database collection'
  
  def delete(self):
    try:
      self.collection.remove({'_id': self.user_id})
    except Exception, e:
      print 'Error: Could not delete user from mongodb, %s' % str(e)
  
  def create(self):
    # Check if user exists, if not create else update
    try:
      u_data = self.collection.find_one({'username': self.username})
      if u_data == None or len(u_data) == 0:
        # User does not exist
        try:
          fip = deepcopy(self.preferences)
          fip['username'] = self.username
          self.collection.insert(fip)
          u_data = self.collection.find_one({'username': self.username})
          self.user_id = u_data['_id']
        except Exception, e:
          print 'Error: Could not create user in mongodb, %s' % str(e)
      else:
        # Update the user
        try:
          self.user_id = u_data['_id']
          self.store_preferences()
        except Exception, e:
          print 'Error: could not update existing user in mongodb, %s' % str(e)
    except Exception, e:
      print 'Error: Could not create user in mongodb, %e' % str(e)
  
  def load_preferences(self):
    try:
      u_data = self.collection.find_one({'_id': self.user_id})
      u_data['_id'] = int(u_data['_id'])
      if u_data is not None:
        self.user_id = u_data['_id']
        del u_data['_id']
        del u_data['username']
        self.preferences = u_data
    except Exception, e:
      print 'Could not load data from mongodb, %s' % str(e)
  
  def store_preferences(self):
    try:
      id_filter = {'_id': self.user_id}
      updater = { '$set': { k: v for (k, v) in self.preferences.iteritems() if k != '_id' } }
      self.collection.update(id_filter, updater)
    except Exception, e:
      print 'Error: Could not store data into mongodb, %s' % str(e)
  
  def json(self):
    return json.dumps(self.preferences)