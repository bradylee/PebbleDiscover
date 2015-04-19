#!/usr/bin/env python

import requests
import os
import collections
from multiprocessing import Pool

class GoogleMapSearch(collections.MutableMapping):
  
  GOOGLE_MAPS_URL = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?'
  
  def __init__(self, *args, **kwargs):
    self.parameters = {}
    
    # Required parameters
    self.parameters['key'] = kwargs['key'] if kwargs.has_key('key') else os.environ['MAPS_KEY'] if os.environ.has_key('MAPS_KEY') else None
    self.parameters['location'] = None
    self.parameters['radius'] = None
    
    # Optional parameters
    self.parameters['keyword'] = None
    self.parameters['language'] = None
    self.parameters['minprice'] = None
    self.parameters['maxprice'] = None
    self.parameters['name'] = None
    self.parameters['opennow'] = None
    self.parameters['rankby'] = None
    self.parameters['types'] = None
    self.parameters['pagetoken'] = None
    self.parameters['zagatselected'] = None
    self.update(dict(*args, **kwargs))
  
  def __getitem__(self, key):
    return self.parameters[self.__keytransform__(key)]
  
  def __setitem__(self, key, value):
    self.parameters[self.__keytransform__(key)] = value
  
  def __delitem__(self, key):
    del self.parameters[self.__keytransform__(key)]
  
  def __iter__(self):
    return iter(self.parameters)
  
  def __len__(self):
    return len(self.parameters)
  
  def __keytransform__(self, key):
    return key
  
  def search(self):
    parameters = {p: v for (p, v) in self.parameters.iteritems() if p is not None}
    response = requests.get(GoogleMapSearch.GOOGLE_MAPS_URL, params=parameters)
    return response.json() if response.ok else None

class Requestor():
  
  def __init__(self):
    self.user = None
  
  def process(self):
    """Asynchronously query google maps"""
    if self.user is not None:
      async_queries = []
      
      # Load in preferences
      for k, v in self.user.preferences.iteritems():
        for l in self.user.preferences[k]:
          gmap_search = GoogleMapSearch()
          gmap_search.parameters['keyword'] = k+' '+l
          gmap_search.parameters['language'] = 'eng'
          gmap_search.parameters['minprice'] = None
          gmap_search.parameters['maxprice'] = None
          gmap_search.parameters['name'] = None
          gmap_search.parameters['opennow'] = None
          gmap_search.parameters['rankby'] = None
          gmap_search.parameters['types'] = None
          gmap_search.parameters['pagetoken'] = None
          gmap_search.parameters['zagatselected'] = None
          
          async_queries.append(gmap_search)
       
      # Send all requests
      async_pool = Pool(processes=8)
      async_pool.map(lambda x: x.search(), async_queries)
