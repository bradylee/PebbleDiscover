var UI = require('ui');
var ajax = require('ajax');

var loading = new UI.Card({
  title: "Loading..."
});
loading.show();

var nothing = new UI.Card({
  title: "Sorry...",
  subtitle: "Nothing nearby"
});

var username = "Brady";
var gps = {
  latitude: 40,
  longitude: -88
};

function MenuItem(title, keywords) {
  this.title = title;
  this.keywords = keywords;
  this.list = [];
  
  this.contains = function(key) {
    if (this.keywords.indexOf(key) > -1)
      return true;
    else
      return false;
  };
}

function Place(name, type, price, open, id, rating) {
  this.title = name;
  this.subtitle = type;
  this.price = price;
  this.open = open;
  this.id = id;
  this.rating = rating;
}

var main_menu_items = [
  new MenuItem("Food", ["bakery", "cafe", "meal_delivery", "meal_takeaway", "food", "restaurant"]), 
  new MenuItem("Stores", ["department_store", "clothing_store", "store", "electronics_store", "convenience_store"]), 
  new MenuItem("Venues", ["zoo", "amusement_park", "casino", "shopping_mall", "movie_theater"]), 
  new MenuItem("Public Transport", ["subway_station", "taxi_stand", "train_station"])
];

var main_menu = new UI.Menu({
  sections: [{
    title: "Main Menu",
    items: main_menu_items
  }]
});

main_menu.on('select', function(e) {
  var category = main_menu_items[e.itemIndex];
  
  if (category.list.length > 0) {
    var new_menu = new UI.Menu({
      sections:[{
        title: category.title,
        items: category.list
      }]
    });
    new_menu.show();
    
    new_menu.on('select', function(e) {
      var place = category.list[e.itemIndex];
      var open;
      
      if (place.open) {
        open = "Open now";
      }
      else {
        open = "Closed now";
      }
      
      var price = "";
      if (place.price > 0)
        price = Array(place.price+1).join("$");
      else if (price === 0)
        price = "Free";
        
      var rating = "";
      if (place.rating > 0)
        rating = place.rating.toFixed(2);
      
      var text = price + "\n" + rating;
      
      var placeCard = new UI.Card({
        title: place.title,
        subtitle: open,
        body: text
      });
      
      placeCard.show();
    });
    
  }
  else
    nothing.show();
});

main_menu.show();
loading.hide();

function submit() {
  var URL = "http://104.236.213.197/register";
  ajax(
  {
    url: URL,
    method: 'post',
    type: 'form',
    data: {
      "username" : "Brady"
    },
    crossDomain: true
  },
    function(data) {
      // Success!
      console.log('Success POST!');
    },
    function(error) {
      // Failure!
      console.log('Failed POST: ' + error);
    }
  );
}
submit();

function getGPS() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(setPosition);
  }
  function setPosition(position) {
    gps.latitude = position.coords.latitude;
    gps.longitude = position.coords.longitude;
  }
}
getGPS();

function download(link) {
  var URL = link;
  ajax(
  {
    url: URL,
    method: 'get'
  },
    function(data) {
      // Success!
      console.log('Success GET!');
      parse(data);
    },
    function(error) {
      // Failure!
      console.log('Failed GET: ' + error);
    }
  );
}
download("http://104.236.213.197/discover?username=" + username + "&latitude=" + gps.latitude + "&longitude=" + gps.longitude);
//parse(main_request);

function parse(data) {
  if (data === null)
    return;
  console.log("Parsing...");
  var res = JSON.parse(data);
  for (var i in res) {
    var status = res[i].status;
    //var category = res[i].category;
    var results = res[i].results;
    if (status === "OK") {
            
      for (var r in results) {
        var name = results[r].name;
        var type = results[r].types[0].trim();
        var price = results[r].price_level;
        var open = results[r].opening_hours;
        if (open !== undefined)
          open = open.open_now;
        var id = results[r].place_id;
        var rating = results[r].rating;
        
      for (var m in main_menu_items) {
        var item = main_menu_items[m];
        if (item.contains(type))
          item.list.push(new Place(name, type, price, open, id, rating));
        else
          console.log("Invalid place type: " + type);
      }
      }
    }
  }
  for (var n in main_menu_items) {
    var items = main_menu_items[n];
    console.log(items.list);
  }
}