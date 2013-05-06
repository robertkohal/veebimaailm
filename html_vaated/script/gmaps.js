function initialize() {
	
	var addresses = new Array("Roosikrantsi 12, Tallinn","Leigri väljak 5, Kärdla",
						  "Keskväljak 1, Jõhvi","Suur 3, Jõgeva",
						  "Rüütli 25, Paide","Lahe 8, Haapsalu","Kreutzwaldi 5, Rakvere",
						  "Kesk 20, Põlva","Akadeemia 2, Pärnu","Tallinna mnt 14, Rapla",
						  "Lossi 1,Kuressaare","Riia 15, Tartu","Kesk 12, Valga","Vabaduse plats 2, Viljandi",
						  "Jüri 12,Võru");
	
	var myCenter=new google.maps.LatLng(58.7682,25.266723);
	var mapProp = {
		center:myCenter,
		zoom:8,
		mapTypeId:google.maps.MapTypeId.ROADMAP
	};
	var map=new google.maps.Map(document.getElementById("googleMap")
			,mapProp);
	addMarkers(map, addresses);
	createLegend(map);
}	

function addMarkers(map, addresses) {
	var geocoder = new google.maps.Geocoder();
	result = {};
	addresses.forEach(function (address,region) {
		region = region+1;
		function addMarker() {
			geocoder.geocode({'address': address}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					var lat = results[0].geometry.location.lat();
					var lng = results[0].geometry.location.lng();
					var geolocation = new google.maps.LatLng(lat,lng);
					var marker=new google.maps.Marker({
						position: geolocation,
						map: map,
						icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
					});
					addInfoWindow(map, marker, region, geolocation);
					
				} else {
					
					result = "Unable to find address: " + status;
					console.log(result);
					setTimeout(addMarker,5000*Math.random());
				}
			});
		}
		return addMarker();
	})
}

function addInfoWindow(map, marker, region, geolocation) {
	$.getJSON('/server/GetVotes',{'region_id':region}, function(data) {
		var totalVotes = 0;
		var leader = "";
		var leaderPoints = 0;
		var votedForLeader = 0;
		$.each(data.partys, function(key, val) {
			if(key==0) {
				leader = val.name;
				leaderPoints = val.votes;
			}
			totalVotes = totalVotes + val.votes;
		});
		if(totalVotes != 0) {
			votedForLeader = (leaderPoints/totalVotes*100).toFixed(1);
		}
		/*var infowindow = new google.maps.InfoWindow({
			content:"<div class='infowindow'>Juhtiv erakond: "+leader+" Hääli protsentuaalselt: "+votedForLeader+"%</div>",
			maxWidth: "0.1"
			
		});*/
		var labelText = "<div class='infowindow'>Juhtiv erakond: "+leader+" Hääli protsentuaalselt: "+votedForLeader+"%</div>";

        var myOptions = {
                 content: labelText
                ,boxStyle: {
                   border: "1px solid black"
                  ,textAlign: "center"
                  ,fontSize: "8pt"
                  ,width: "100px"
				  ,background: "#fff"
                 }
                ,disableAutoPan: true
                ,pixelOffset: new google.maps.Size(-25, 0)
                ,position: geolocation
                ,closeBoxURL: ""
                ,isHidden: false
                ,pane: "mapPane"
                ,enableEventPropagation: true
        };
		
		var iconbase = 'http://maps.google.com/mapfiles/ms/icons/';
		
		if(leader == "Isamaa- ja Respublica Liit") {
			
			var marker=new google.maps.Marker({
					position: geolocation,
					map: map,
					icon: iconbase + 'red-dot.png'
			});
		}
		if(leader == "Eesti Keskerakond") {
			var marker=new google.maps.Marker({
					position: geolocation,
					map: map,
					icon: iconbase + 'pink-dot.png'
			});
		}
		if(leader == "Eesti Reformierakond") {
			//map.removeOverlay(marker);
			var marker=new google.maps.Marker({
					position: geolocation,
					map: map,
					icon: iconbase + 'blue-dot.png'
			});
		}
		if(leader == "Sotsiaaldemokraatlik erakond") {
			var marker=new google.maps.Marker({
					position: geolocation,
					map: map,
					icon: iconbase + 'green-dot.png'
			});
		}
        var ibLabel = new InfoBox(myOptions);
        ibLabel.open(map);	
	});
}
function createLegend(map) {
	var iconBase = 'http://maps.google.com/mapfiles/ms/icons/';
	var icons = {
	  keskera: {
		name: 'Eesti Keskerakond',
		icon: iconBase + 'pink-dot.png'
	  },
	  irl: {
		name: 'Isamaa- ja Respublica Liit',
		icon: iconBase + 'red-dot.png'
	  },
	  ref: {
		name: 'Eesti Reformierakond',
		icon: iconBase + 'blue-dot.png'
	  },
	  sots: {
		name: 'Sotsiaaldemokraatlik erakond',
		icon: iconBase + 'green-dot.png'
	  }
	};
	var legend = document.getElementById('legend');
	$.each(icons, function(key, val) {
	  var type = icons[key];
	  var name = type.name;
	  var icon = type.icon;
	  $("#legend").append('<div><img src="' + icon + '"> ' + name + '</div>');
	  //legend.appendChild(div);
	});

	map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);
}

google.maps.event.addDomListener(window, 'load', initialize);