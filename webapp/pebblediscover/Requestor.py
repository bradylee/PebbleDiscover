#!/usr/bin/env python

import requests
import os
import collections
import json
from multiprocessing import Pool

class GoogleMapSearch(collections.MutableMapping):
  
  GOOGLE_MAPS_URL = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?'
  
  def __init__(self, *args, **kwargs):
    self.parameters = {}
    
    # Required parameters
    self.parameters['key'] = kwargs['key'] if kwargs.has_key('key') else os.environ['GOOGLE_MAPS_KEY'] if os.environ.has_key('GOOGLE_MAPS_KEY') else None
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

"""Parallel search"""
def parallel_search(gmap_search): return gmap_search.search()

class Requestor():
  
  def __init__(self, user=None):
    self.user = user
  
  def process(self):
    """Asynchronously query google maps"""
    if self.user is not None:
      async_queries = []
      
      # DEBUGGING
      # prefs = []
      
      # Load in preferences
      for k, v in self.user.preferences.iteritems():
        for l in self.user.preferences[k]:
          gmap_search = GoogleMapSearch()
          gmap_search.parameters['location'] = '40,-88'
          gmap_search.parameters['radius'] = '10000'
          gmap_search.parameters['keyword'] = l
          gmap_search.parameters['language'] = 'en'
          # gmap_search.parameters['minprice'] = None
          # gmap_search.parameters['maxprice'] = None
          # gmap_search.parameters['name'] = None
          # gmap_search.parameters['opennow'] = None
          # gmap_search.parameters['rankby'] = None
          if k != 'keywords': gmap_search.parameters['types'] = k
          # gmap_search.parameters['pagetoken'] = None
          
          # DEBUGGING
          # prefs.append(gmap_search.parameters)
          
          async_queries.append(gmap_search)
       
      # Send all requests
      async_pool = Pool(processes=8)
      response = async_pool.map(parallel_search, async_queries)
      filtered_responses = ['']*len(response)
      for i, r in enumerate(response):
        filtered_response = {k:v for (k, v) in r.iteritems()}
        filtered_response['category'] = {'name': None, 'type': None}
        if async_queries[i].parameters.has_key('type') and async_queries[i].parameters['types'] != '' and async_queries[i].parameters['types'] is not None: filtered_response['category']['name'] = async_queries[i].parameters['types']
        else: filtered_response['category']['name'] = 'keyword'
        filtered_response['category']['type'] = async_queries[i].parameters['keyword']
        filtered_responses[i] = filtered_response
      return filtered_responses
    else: return None
