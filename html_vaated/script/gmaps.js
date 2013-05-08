function initialize() {
	"use strict";

	var myCenter = new google.maps.LatLng(58.7682,25.266723);
	var mapProp = {
		center:myCenter,
		zoom:8,
		mapTypeId:google.maps.MapTypeId.ROADMAP
	};
	var map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
	
	jQuery.ajaxSetup({async:false});
	var partys = getPartys();
	var regions = "";
	$.getJSON("/server/GetRegions",function (data) {
		regions = data.regions;
	});
	jQuery.ajaxSetup({async:true});
	
	addMarkers(map, partys, regions);
	createLegend(map,partys);
}	

function addMarkers(map,partys, regions) {
	"use strict";
	var result = {};
	regions.forEach(function (region,indeks) {
		indeks = indeks+1;
		var leader = [{"votes":-1}];
		var total_votes = 0;
		jQuery.ajaxSetup({async:false});
		$.getJSON("/server/GetVotes",{"region_id":indeks},function (data) {
			leader = data.partys[0];
			data.partys.forEach(function(party) {
				total_votes+=party.votes;
			});
		});
		jQuery.ajaxSetup({async:true});
		var lat = region.latitude;
		var lng = region.longitude;
		console.log("Lat: "+lat);
		console.log("Lng: "+lng);
		var geolocation = new google.maps.LatLng(lat,lng);
		var markerImage = getMarkerImageByPartyName(leader.name,partys);
		var marker=new google.maps.Marker({
						position: geolocation,
						map: map,
						icon: markerImage,
		});
		if (total_votes==0)
			total_votes=1;
		var labelText = "<div class='infowindow'>Juhtiv erakond: "+leader.name+" Hääli protsentuaalselt: "+ ((leader.votes*100)/total_votes).toFixed(1)+"%</div>";
		addInfoWindow(map, labelText,geolocation);					
	});
}
function getPartys() {
	"use strict";
	var partys;
	$.getJSON("/server/GetPartys",function (data) {
		partys = data.partys;
	});
	partys.forEach(function(party) {
		var color = getColorByPartyName(party.name,partys);
		party.markerimage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + color,
							new google.maps.Size(21, 34),
							new google.maps.Point(0,0),
							new google.maps.Point(10, 34));
	});
	return partys;
}

function getColorByPartyName(name, partys) {
	"use strict";
	var color = "FFFFFF";
	partys.forEach(function(party) {
		if (party.name==name) {
			color = party.color;
			return;
		}
	});
	return color;
}
function getMarkerImageByPartyName(party_name,partys) {
	var markerImage= "";
	partys.forEach(function(party) {
		if (party.name===party_name) 
			markerImage=party.markerimage;
	});
	return markerImage;
}
function addInfoWindow(map, labelText, geolocation) {
	"use strict";
	var myOptions = {
		content: labelText,
		boxStyle: {
			border: "1px solid black",
			textAlign: "center",
			fontSize: "8pt",
			width: "100px",
			background: "#fff"
		},
		disableAutoPan: true,
		pixelOffset: new google.maps.Size(-25, 0),
		position: geolocation,
		closeBoxURL: "",
		isHidden: false,
		pane: "mapPane",
		enableEventPropagation: true
	};
	var ibLabel = new InfoBox(myOptions);
    ibLabel.open(map);
		
}
function createLegend(map,partys) {
	"use strict";
	var legend = document.getElementById('legend');
	partys.forEach(function(party) {
		$("#legend").append('<div><img src="' + party.markerimage.url + '"> ' + party.name + '</div>');
	});
	map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);
}

//google.maps.event.addDomListener(window, 'load', initialize);