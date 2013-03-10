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
    $("#loading").show();
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
    $("#loading").hide();
}
function generateTHead(selection) {
	var thead = "";
switch (selection) {
	
	case 1:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="vote">Hääleta</th></tr>';
		break;
	case 2:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">Hääleta</th></tr>';
		break;
	case 3:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="vote">Hääleta</th></tr>';
		break;
	case 4:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">Hääleta</th></tr>';
		break;
	}
	return thead;
}
function navigation(element) {
	var index = element.target.href.lastIndexOf("/") + 1;
	var filename = element.target.href.substr(index);
    $("#loading").show();
	$.get(filename,function(data,status) {
		updateContent(data,filename);
		history.pushState(data, element.target.textContent, element.target.href);
	});
}
function updateContent(data, filename) {
	if (data == null) {
        return;
	}
	$("#content").empty();
    if (filename=="index.html") {
        $("#content").append("<img id=\"map\" src=\"images/kontuurkaart.jpg\" alt=\"kaart\" />");
        $("#loading").hide();
        return;
    }
	$("#content").append(data);
	if (filename=="h22letamine.html") {
		$('#search-candidate').keypress(function (e) {
			if (e.which == 13) {
				e.preventDefault();
				var selection=4;
			} else {
				return;
			}
            $("#loading").show();
			$.getJSON("candidate.json",function(data) {
				updateTable(data,selection);
			});
		});
        $("#districts").bind("change",querydb);
        $("#politics-party").bind("change",querydb);
        
	} else if (filename=="statistika.html") {
		$.get("diagramm.html",function(data,status) {
            $("#loading").hide();
			$(".statistics-wrapper").empty();
			$(".statistics-wrapper").append(data);
		});
        $("#val5").bind("click",getDistrictStatistics);
    } else if (filename=="kandideerimine.html") {
        $("#districts").bind("change",hideSelectionError);
        $("#politics-party").bind("change",hideSelectionError);
        $("#candidate_questionary").submit(valitadeQuestionary);
        
	} else if (filename=="nimekiri.html") {
        var x = "";
        var str = "ABCDEFGHIJKLMNOPRSŠZŽTUVÕÄÖÜ";
        for(var i=0; i<str.length; i++)  {
            x = x +"<a>" + str.charAt(i) +"</a> ";	
        }
        document.getElementById("alphabet").innerHTML=x;
	}
    $("#loading").hide();
}

function valitadeQuestionary(event) {
    var politics_party = parseInt($("#politics-party").val());
	var districts = parseInt($("#districts").val());
    
    if (districts==0) {
        $("#nodistrictselectederror").show();
    }
    if (politics_party==0) {
        $("#nopartyselectederror").show();
    }
    
    if (politics_party==0 || districts==0) {
        event.preventDefault();
        return false;
    } else {
        var district = $("#districts").find(":selected").text();
        var politics_party = $("#politics-party").find(":selected").text();
        $("h1").after("<div id=\"dialog-confirm\"></div>");
        $("#dialog-confirm").attr("title","Kandideerimine");
        $("#dialog-confirm").html('Piirkond: '+district+'<br/>Erakond: '+politics_party+'<br/>Kas soovite kandideerida?');
        $( "#dialog-confirm" ).dialog({
                                resizable: false,
                                modal: true,
                                buttons: { 
                                    "Jah": function() {
                                        applyFor();
                                        $( this ).dialog( "close" );
                                    },
                                    "Ei": function() {
                                        $( this ).dialog( "close" );
                                    }
                                }
                            });
        event.preventDefault();
        return false;                    
    }
    return true;
    
}
function applyFor() {
    var date = new Date();
    var day = date.getDate();
    var month = ("0"+(date.getMonth()+1)).slice(-2);
    var year = date.getFullYear();
    var apply_for_date = "["+day+"."+month+"."+year+"]";
    $("#is_voted_text").text("Te olete kandideerinud."+apply_for_date);
    $(".is_applyed_for").css({"background":"#006600","margin-right":"40%"});
}

function hideSelectionError(event) {
    var selectionValue = parseInt($(event.target).find(":selected").val());
    var selectId = $(event.target).attr("id");
    if (selectionValue!=0) {
        if (selectId=="districts") {
            $("#nodistrictselectederror").hide();
        } else {
            $("#nopartyselectederror").hide();
        }  
    }
    return false;
}

function getDistrictStatistics(element) {
    
	var districtId = element.target.id;
	if (districtId=="val5") {
		$.get("statistika_sort.html",function(data,status) {
			$(".statistics-wrapper").empty();
			$(".statistics-wrapper").append(data);
            $($(".name")[0]).bind("click",sortButtonClick);
            $($(".vote")[0]).bind("click",sortButtonClick);
			sortStatistics($(".name")[0]);
		});
	}
}
function sortButtonClick(event) {
    sortStatistics(event.target);
	return event.preventDefault();
}
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
    
    var LastNameA = $(a).children(".name").text().split(" ")[1];
	var LastNameB = $(b).children(".name").text().split(" ")[1];
    if (LastNameA>LastNameB) {
		return 1;
	} else if (LastNameA<LastNameB) {
		return -1;
	} else {
		return 0;
	}
}

function reverseNameColumn(a,b) {
    var LastNameA = $(a).children(".name").text().split(" ")[1];
	var LastNameB = $(b).children(".name").text().split(" ")[1];
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