var statisticsSortBy = "name";
var statisticsSortByDirection = "down";

function querydb() {
	
	var politics_party = parseInt($("#politics-party").val());
	var districts = parseInt($("#districts").val());
	var filename;
	var selection;
	
	if (politics_party>=1 && districts>=1) {
		selection = 3;
		filename = "findCandidatesByPartyAndRegion.json";
	} else if (politics_party>=1) {
		selection = 2;
		filename = "findCandidatesByParty.json";
	} else if (districts>=1) {
		selection = 1;
		filename = "findCandidatesByRegion.json";
	} else {
		return;
	}
	$.getJSON(filename,function(data) {
		updateTable(data,selection);
	});
}

function updateTable(data,selection) {
	$(".voting-table tr").remove();
	var thead = generateTHead(selection);
	$(".voting-table thead").append(thead);
	
	var html = "";
	
	switch (selection) {
	
	case 3:
		$(".name").css({"width":"80%"});
		break;
	case 4:
		$(".name").css({"width":"30%"});
		$(".party").css({"width":"30%"});
		break;
	}
	if (selection==4) {
		html ='<tr><td class="name">' + data.person.name 
		+ '</td><td class="party">' + data.party.name 
		+ '</td><td class="party">' + data.region.name
		+ '</td><td class="vote"></td></tr>'
	} else {
		for (var i=0;i<data.candidates.length;i++) {
			html+='<tr><td class="name">' + data.candidates[i].person.name 
				+ ((selection==3)? '' : '</td><td class="party">' 
				+ ((selection==1)? data.candidates[i].party.name : data.candidates[i].region.name))
				+ '</td><td class="vote"></td></tr>' 
		}
	}
	$("#voting-table-body").append(html);
}
function generateTHead(selection) {
	var thead = "";
switch (selection) {
	
	case 1:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="vote">H‰‰leta</th></tr>';
		break;
	case 2:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">H‰‰leta</th></tr>';
		break;
	case 3:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="vote">H‰‰leta</th></tr>';
		break;
	case 4:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">H‰‰leta</th></tr>';
		break;
	}
	return thead;
}
function navigation(element) {
	var index = element.href.lastIndexOf("/") + 1;
	var filename = element.href.substr(index);
	$.get(filename,function(data,status) {
		$("#content").empty();
		$("#content").append(data);
		if (filename=="h22letamine.html") {
			$('#search-candidate').keypress(function (e) {
				if (e.which == 13) {
					e.preventDefault();
					var selection=4;
				} else {
					return;
				}
				$.getJSON("candidate.json",function(data) {
					updateTable(data,selection);
				});
			});
		} else if (filename=="statistika.html") {
			$.get("diagramm.html",function(data,status) {
				$(".statistics-wrapper").empty();
				$(".statistics-wrapper").append(data);
			});
		}
	});
}
function getDistrictStatistics(element) {
	var districtId = element.id;
	if (districtId=="val5") {
		$.get("statistika_sort.html",function(data,status) {
			$(".statistics-wrapper").empty();
			$(".statistics-wrapper").append(data);
			sortStatistics($(".name")[0]);
		});
	}
}
//proov
function sortStatistics(element) {
	var uparrow = "&#x25B2";
	var downarrow = "&#x25BC";
	
	if (element.className==statisticsSortBy) {
		var columnname = element.innerHTML.slice(0,-1);
			
		if (statisticsSortByDirection=="up") {
			statisticsSortByDirection="down";
			
			element.innerHTML = columnname+downarrow;
		} else {
			statisticsSortByDirection="up";
			element.innerHTML = columnname+uparrow;
		}
	} else {
		var columnname = element.innerHTML;
		var lastSortedColumnelement = $("."+statisticsSortBy)[0].innerHTML.slice(0,-1);
		
		$("."+statisticsSortBy)[0].innerHTML= lastSortedColumnelement;
		element.innerHTML = columnname+uparrow;
		statisticsSortByDirection="up";
	}
	statisticsSortBy = element.className;
	var rows = $("#voting-table-body tr");
	if (statisticsSortBy=="vote") {
		if (statisticsSortByDirection=="down") {
			rows.sort(reverseNumberColumn);
		} else {
			rows.sort(sortNumberColumn);
		}
	} else if (statisticsSortBy=="name") {
		if (statisticsSortByDirection=="down") {
			rows.sort(reverseNameColumn);
		} else {
			rows.sort(sortNameColumn);
		}
	}
	$("#voting-table-body").empty();
	$("#voting-table-body").append(rows);
}
function sortNameColumn(a,b) {
	var LastNameA = $(a).children(".name").text().split(" ").slice(-1)[0];
	var LastNameB = $(b).children(".name").text().split(" ").slice(-1)[0];
	if (LastNameA>LastNameB) {
		return 1;
	} else if (LastNameA<LastNameB) {
		return -1;
	} else {
		return 0;
	}
}

function reverseNameColumn(a,b) {
	var LastNameA = $(a).children(".name").text().split(" ").slice(-1)[0];
	var LastNameB = $(b).children(".name").text().split(" ").slice(-1)[0];
	if (LastNameA<LastNameB) {
		return 1;
	} else if (LastNameA>LastNameB) {
		return -1;
	} else {
		return 0;
	}
}
function sortNumberColumn(a,b) {
	var votesForA = parseInt($(a).children(".vote").text());
	var votesForB = parseInt($(b).children(".vote").text());
	return votesForA-votesForB;
}
function reverseNumberColumn(a,b) {
	var votesForA = parseInt($(a).children(".vote").text());
	var votesForB = parseInt($(b).children(".vote").text());
	return votesForB-votesForA;
}