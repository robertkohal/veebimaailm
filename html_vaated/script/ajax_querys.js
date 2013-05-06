var statisticsSortBy = "name";
var statisticsSortByDirection = "down";

function querydb(event) {
	var url_server;
	if (event.data.url=="vote") {
		url_server = "/server/GetCandidates";
	} else {
		url_server = "/server/GetVotes";
	}

	var politics_party = parseInt($("#politics-party").val());
	var districts = parseInt($("#districts").val());
	var params;
	var selection;
	var uriparams = "";
	if (politics_party>=1 && districts>=1) {
		selection = 3;
		params = {"party_id":politics_party,"region_id":districts};
		uriparams = "&party_id="+politics_party+"&region_id="+districts;
	} else if (politics_party>=1) {
		selection = 2;
		params = {"party_id":politics_party};
		uriparams = "&party_id="+politics_party
	} else if (districts>=1) {
		selection = 1;
		params = {"region_id":districts};
		uriparams = "&region_id="+districts;
	} else {
		return;
	}
    $("#loading").show();
	if (navigator.onLine==true) {
		$.getJSON(url_server,params,function (data) {
			updateTableContent(data,event, selection,uriparams);
		});
	} else {
		switch (selection) {
			case 1:
				var data = JSON.parse(localStorage['region_'+districts]);
				updateTableContent(data,event, selection, uriparams);
				break;
			case 2:
				var data = JSON.parse(localStorage['party_'+politics_party]);
				updateTableContent(data,event, selection, uriparams);
				break;
			case 3:
				var data = JSON.parse(localStorage['party_'+politics_party+"_region_"+districts]);
				updateTableContent(data,event, selection, uriparams);
				break;
		}
	}
}
function updateTableContent(data, event, selection, uriparams) {
	if (event.data.url=="vote") {
			updateTable(data,selection);
	} else {
		updateStatisticsTable(data,selection);
	}
	var newdata = $("#content").html();
	var urilocalpart = location.href.split("&");
	history.pushState(newdata, event.target.textContent,urilocalpart[0]+uriparams);
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
		data.candidates.forEach(function(item) {
			html +='<tr class="tbody_tr"><td class="name">' + item.person_name 
			+ '</td><td class="party">' + item.party_name 
			+ '</td><td class="party">' + item.region_name
			+ '</td><td class="vote"><p class="vote_text" id="vote_'+item.id+'">Hääleta</p>'
			+ '</td><td class="candidate_id">' + item.id
			+ '</td></tr>';
		});
	} else {
		for (var i=0;i<data.candidates.length;i++) {
			html+='<tr class="tbody_tr"><td class="name">' + data.candidates[i].person_name 
				+ ((selection==3)? '' : '</td><td class="party">' 
				+ ((selection==1)? data.candidates[i].party_name : data.candidates[i].region_name))
					+ '</td><td class="vote"><p class="vote_text" id="vote_'+data.candidates[i].id+'">Hääleta</p>'
				+ '</td><td class="candidate_id">' + data.candidates[i].id
				+ '</td></tr>'; 
		}
	}
	$("#voting-table-body").append(html);
	for (var i=0;i<data.candidates.length;i++) {
		var id_p = data.candidates[i].id;
		$("#vote_"+data.candidates[i].id).click({"id":id_p},function(event) {
			event.preventDefault();
			$.post("/server/private/Vote", JSON.stringify({"action":"vote","candidate_id":event.data.id,"sura":"sfa"}), function(data) {
				postToServer();
				window.location.reload();
			});
		});
	}
    $("#loading").hide();
}
function updateStatisticsTable(data,selection) {
	$(".voting-table tr").remove();
	var thead = generateStatisticsTHead(selection);
	$(".voting-table thead").empty();
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
	switch (selection) {
		case 4:
			data.candidates.forEach(function(item) {
				html +='<tr class="tbody_tr"><td class="name">' + item.person_name 
				+ '</td><td class="vote"><p class="vote_text">'+item.vote+'</p>'
				+ '</td></tr>';
			});
			break;
			
		case 3:
			for (var i=0;i<data.candidates.length;i++) {
				html+='<tr class="tbody_tr"><td class="name">' + data.candidates[i].person_name 
					+ '</td><td class="vote">'+data.candidates[i].vote
					+ '</td></tr>'; 
				}
			break;
		case 2:
			for (var i=0;i<data.candidates.length;i++) {
			html+='<tr class="tbody_tr"><td class="name">' + data.candidates[i].person_name 
				+ '</td><td class="vote">'+data.candidates[i].vote
				+ '</td></tr>'; 
			}
			break;
		case 1:
			for (var i=0;i<data.partys.length;i++) {
			html+='<tr class="tbody_tr"><td class="name">' + data.partys[i].name 
				+ '</td><td class="vote">'+data.partys[i].votes
				+ '</td></tr>'; 
			}
			break;
		case 0:
			for (var i=0;i<data.partys.length;i++) {
				html+='<tr class="tbody_tr"><td class="name">' + data.partys[i].name 
					+ '</td><td class="vote">'+data.partys[i].votes
					+ '</td></tr>';
			}
			break;
		}
	$("#voting-table-body").append(html);
	$($(".name")[0]).bind("click",sortButtonClick);
		
    $($(".vote")[0]).bind("click",sortButtonClick);
    sortStatistics($(".name")[0]);
	$("#loading").hide();
}
function generateTHead(selection) {
	var thead = "";
switch (selection) {
	
	case 1:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="vote">Hääleta</th>'
			+ '<th class="candidate_id"></th></tr>';
		break;
	case 2:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">Hääleta</th>'
			+ '<th class="candidate_id"></th></tr>';
		break;
	case 3:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="vote">Hääleta</th>'
			+ '<th class="candidate_id"></th></tr>';
		break;
	case 4:
		thead = '<tr><th class="name">Nimi</th>'
			+ '<th class="party">Erakond</th>'
			+ '<th class="party">Maakond</th>'
			+ '<th class="vote">Hääleta</th>'
			+ '<th class="candidate_id"></th></tr>';
		break;
	}
	return thead;
}
function generateStatisticsTHead(selection) {
	var thead = "";
	if (selection==0) {
		selection=1;
	}
switch (selection) {
	
	case 1:
		thead ='<tr><th class="name">Erakond&#x25B2</th>'
			+ '<th class="vote">Hääled</th>';
		break;
	case 2:
		thead = '<tr><th class="name">Nimi&#x25B2</th>'
			+ '<th class="vote">Hääled</th>';
		break;
	case 3:
		thead = '<tr><th class="name">Nimi&#x25B2</th>'
			+ '<th class="vote">Hääled</th>';
		break;
	case 4:
		thead = '<tr><th class="name">Nimi&#x25B2</th>'
			+ '<th class="vote">Hääled</th>';
		break;
	}
	
	return thead;
}
function navigation(element) {
	var params = element.target.href.split("?");
	var current_page_name = params[1].split(/=|&/)[1];
	var filename = getFileNameByPageParam(current_page_name);
    $("#loading").show();
	$.get(filename,function(data,status) {
		var newdata = updateContent(data,filename,params[1]);
		history.pushState(newdata, element.target.textContent, element.target.href);
	});
}
function getFileNameByPageParam(page) {
	var filename = "";
	switch (page) {
		case "index":
			filename = "index.html";
			break;
		case "vote":
			filename = "h22letamine.html";
			break;
		case "nominate":
			filename = "kandideerimine.html";
			break;
		case "list":
			filename = "nimekiri.html";
			break;
		case "statistics":
			filename = "statistika.html";
			break;
	}
	return filename;
}
function updateContent(data, filename, params) {
	
	if (data == null) {
		return;
		
	}
	var letters = "";
	var region_id = "";
	var party_id = "";
	
	var params_array = params.split("&");
	if (params_array.length>1) {
		for (var i=0;i<params_array.length;i++) {
			var index = params_array[i].indexOf("=");
			if (params_array[i].indexOf("party_id")!=-1) {
				party_id = params_array[i].substr(index+1);
			} else if (params_array[i].indexOf("region_id")!=-1) {
				region_id = params_array[i].substr(index+1);
			} else if (params_array[i].indexOf("letters")!=-1) {
				letters = params_array[i].substr(index+1);
			}
		}
	}
	$("#content").empty();
	console.log("failinimi "+ filename);
    if (filename=="index.html") {
        $("#content").append("<div id='googleMap'></div><div id='legend'><h3>Legend</h3></div>");
        $("#loading").hide();
        return;
    } else if (filename=="h22letamine.html" || filename=="kandideerimine.html") {
		
		var loggedin = false;
		jQuery.ajaxSetup({async:false});
		$.getJSON("/server/private/VerifyLogin",function(data) {
			if (data.result=="success") {
				loggedin = true;
			} else {
				loggedin = false;
			}
		});
		jQuery.ajaxSetup({async:true});
		if (loggedin==false) {
			$("#content").append("<b> Ainult autentinud kasutajatele</b>");
			$("#loading").hide();
			return;
		}
	}
	$("#content").append(data);
	if (filename=="h22letamine.html" || filename=="kandideerimine.html" || filename=="statistika.html") {
		if (navigator.onLine==true) {
			$.ajaxSetup({async: false});
			$.getJSON("/server/GetRegions",function(data) {
				localStorage.setItem("regions",JSON.stringify(data));
				setRegions(data);
			});
			$.getJSON("/server/GetPartys",function(data) {
				localStorage.setItem("partys",JSON.stringify(data));
				setPartys(data);
			});
			$.ajaxSetup({async: true});
		} else {
			var regions = JSON.parse(localStorage['regions']);
			var partys = JSON.parse(localStorage['partys']);
			setRegions(regions);
			setPartys(partys);
		}
	}
	if (filename=="h22letamine.html") {
		/* Kandidaatide otsing hääletamise vaates */
		$('#search-candidate').keypress(function (e) {
			if (e.which == 13) {
				e.preventDefault();
			}
			return;
		});

		$('#search-candidate').keyup(function (e) {
			clearTimeout($.data(this, 'timer'));
			if (e.keyCode == 13) {
				search_h(true);
			} else {
				$(this).data('timer', setTimeout(search_h, 500));
			}
		});
		
		function search_h(force) {
			var existingString = $("#search-candidate").val();
			if (!force && existingString.length < 2) {
				return;
			}
			var selection=4;
			$.getJSON('/server/GetCandidates',{"letters":existingString}, function(data) {
				updateTable(data,selection);
			});
		}
        $("#districts").bind("change",{url:"vote"},querydb);
        $("#politics-party").bind("change",{url:"vote"},querydb);
		
		/* Hääletamine - on hääletatud/ei ole hääletatud */
		$.getJSON('/server/private/Vote', function(data) {
			$("#is_voted_text").empty();
			if (data.result=="alreadyVoted") {
				var date = new Date(data.timestamp);
				var day = date.getDate();
				var month = ("0"+(date.getMonth()+1)).slice(-2);
				var year = date.getFullYear();
				var vote_for_date = "["+day+"."+month+"."+year+"]";
				$("#is_voted_text").html("Te olete juba hääletanud."+vote_for_date+"<br/><br/><a href='h22letamine.html' id='cancel_vote'>Tühista hääl</a>");
				$(".is_voted_for").css({"background":"#006600"});
				$("#cancel_vote").click(function(event) {
					event.preventDefault();
					$.post("/server/private/Vote", JSON.stringify({"action":"cancel"}), function(data) {
						postToServer();
						window.location.reload();
					});
				});
			} else {
				$(".is_voted_for").css({"background":"#ff7b2b"});
				$("#is_voted_text").html("Te ei ole veel hääletanud.");
			}
		});
        
	} else if (filename=="statistika.html") {
		/* Kandidaadi otsimine statistika vaates */
		$('#search-candidate').keypress(function (event) {
			if (event.which == 13) {
				event.preventDefault();
			}
			return;
		});

		$('#search-candidate').keyup(function (event) {
			clearTimeout($.data(this, 'timer'));
			if (event.keyCode == 13) {
				search(true,event);
			} else {
				$(this).data('timer', setTimeout(function () {
					search(false,event);
				}, 500));
			}
		});
		function search(force,event) {
			var existingString = $("#search-candidate").val();
			if (!force && existingString.length < 2) {
				return;
			}
			var selection=4;
			/* online / offline */
			if (navigator.onLine==true) {
				$.getJSON('/server/GetVotes',{"letters":existingString}, function(data) {
					console.log(event.target.textContent);
					updateLetterSearch(data,selection,existingString,event);
				});
			} else {
				var data = JSON.parse(localStorage['all_votes']);
				var candidates = [];
				var re = new RegExp("^"+existingString,'i');
				data.candidates.forEach(function(item) { 
					var name_array =  item['person_name'].split(" ");
					name_array.unshift(name_array.pop());
					var shifted_name = name_array.join(" ");
					if (re.test(shifted_name)) {
						candidates.push({ "person_name":item['person_name'],"vote":item['vote']});
					} 
				});
				updateLetterSearch({"candidates":candidates},selection,existingString,event);
			}
		}//
		$("#districts").bind("change",{url:"random"},querydb);
        $("#politics-party").bind("change",{url:"random"},querydb);
		if (party_id=="" && region_id=="" && letters=="") {
			$.getJSON('/server/GetVotes', function(data) {
			selection = 0;
			updateStatisticsTable(data,selection);
		});
		}
		if (window.location.protocol!=='https:') {
			var ws = new WebSocket("ws://veebimaailm.dyndns.info/server/VotesUpdate",'echo-protocol');
			ws.onopen = function(){};
			ws.onmessage = function(message){
				$("#politics-party").change();
			};
		}
		
		getAllVotes();
	/* kandideerimise fail */	
    } else if (filename=="kandideerimine.html") {
        $("#districts").bind("change",hideSelectionError);
        $("#politics-party").bind("change",hideSelectionError);
        $("#candidate_questionary").submit(valitadeQuestionary);
		
		/* Kandideerimine - on kandideeritud / ei ole */
		$.getJSON('/server/private/Nominate', function(data) {
			$("#is_voted_text").empty();
			if (data.result=="alreadyNominated") {
				$("#is_voted_text").html("Te olete kandideerinud.<br/><br/><a href='kandideerimine.html' id='cancel_nominate'>Tühista kandideerimine</a>");
				$(".is_applyed_for").css({"background":"#006600","margin-right":"50%"});
				$("#cancel_nominate").click(function(event) {
					event.preventDefault();
					$.post("/server/private/Nominate", JSON.stringify({"action":"cancel"}), function(data) {
						postToServer();
						window.location.reload();
					});
				});
			} else {
				$(".is_voted_for").css({"background":"#ff7b2b"});
				$("#is_voted_text").html("Te ei ole veel kandideerinud.");
			}
		});
        
	} else if (filename=="nimekiri.html") {
        var x = "";
        var str = "ABCDEFGHIJKLMNOPRSŠZŽTUVÕÄÖÜ";
        for(var i=0; i<str.length; i++)  {
            x = x +"<a>" + str.charAt(i) +"</a> ";	
        }
        document.getElementById("alphabet").innerHTML=x;
	}
	if (party_id!="") {
		$("#politics-party").val(party_id);
	}	
	if (region_id!="") {
		$("#districts").val(region_id);
	}
	if (letters!="") {
		$("#search-candidate").val(letters);
	}
	
	if (party_id!="") {
		$("#politics-party").change();
	}	
	 else if (region_id!="") {
		$("#districts").change();
	}
	 else if (letters!="") {
		$("#search-candidate").keyup();
	}
	
    $("#loading").hide();
	return $("#content").html();
}
function updateLetterSearch(data,selection,existingString,event) {
	updateStatisticsTable(data,selection);
	var newdata = $("#content").html();
	var urilocalpart = location.href.split("&");
	uriparams = "&letters="+existingString;
	history.pushState(newdata, event.target.textContent,urilocalpart[0]+uriparams);
}
function setRegions(data) {
	$("#districts").empty();
	$('#districts').append(new Option('Vali piirkond',0));
	data.regions.forEach(function(item) {
		$('#districts').append(new Option(item['name'],item['id_region']));
	});
}
function setPartys(data) {
	$("#politics-party").empty();
	$('#politics-party').append(new Option('Vali erakond',0));
	data.partys.forEach(function(item) {
		$('#politics-party').append(new Option(item['name'],item['id_party']));
	});
}
function getAllVotes() {
	$.getJSON("/server/GetVotes",{"all":45},function (data) {
		localStorage.setItem("all_votes",JSON.stringify(data));
		var count_regions = JSON.parse(localStorage['regions']).regions.length;
		var count_partys = JSON.parse(localStorage['partys']).partys.length;
		for (var i=1;i<=count_partys;i++) {
			var candidates = [];
			data.candidates.forEach(function(item) { 
				if (parseInt(item['party_name'])==i) {
					candidates.push({ "person_name":item['person_name'],"vote":item['vote']});
				} 
			});
			localStorage.setItem("party_"+i,JSON.stringify({"candidates":candidates}));
		}
		for (var i=1;i<=count_partys;i++) {
			for (var j=1;j<=count_regions;j++) {
				var candidates = [];
				data.candidates.forEach(function(item) { 
					if (parseInt(item['party_name'])==i && parseInt(item['region_name'])==j) {
						candidates.push({ "person_name":item['person_name'],"vote":item['vote']});
					}
				});
				localStorage.setItem("party_"+i+"_region_"+j,JSON.stringify({"candidates":candidates}));
			}	
		}
		var partys = []
		var partys_name = JSON.parse(localStorage['partys']).partys;
		for (var i=1;i<=count_partys;i++) {
			var votecount = 0;
			data.candidates.forEach(function(item) { 
				if (parseInt(item['party_name'])==i) {
					votecount+=item['vote'];
				} 
			});
			var party = { "name" : partys_name[i-1].name, "votes": votecount};
			partys.push(party);
		}
		
		localStorage.setItem("country_statistics",JSON.stringify({"partys":partys}));
		
		for (var i=1;i<=count_regions;i++) {
			var partys = []
			for (var j=1;j<=count_partys;j++) {
				var votecount = 0;
				data.candidates.forEach(function(item) { 
					if (parseInt(item['party_name'])==j && parseInt(item['region_name'])==i) {
						votecount+=item['vote'];
					}
				});
				var party = { "name" : partys_name[j-1].name, "votes": votecount};
				partys.push(party);
			}
			localStorage.setItem("region_"+i,JSON.stringify({"partys":partys}));
		}
	});
}

function postToServer(){
	jQuery.ajaxSetup({async:false});
	$.post("/server/VotesUpdate",{"action":"vote"},function(data) {
		return;
	});
	jQuery.ajaxSetup({async:true});
}
function closeConnect(){
	var ws = new WebSocket("ws://veebimaailm.dyndns.info/server/VotesUpdate",'echo-protocol');
	ws.close();
}

function valitadeQuestionary(event) {
    var party_id = parseInt($("#politics-party").val());
	var region_id = parseInt($("#districts").val());
    
    if (region_id==0) {
        $("#nodistrictselectederror").show();
    }
    if (party_id==0) {
        $("#nopartyselectederror").show();
    }
    
    if (party_id==0 || region_id==0) {
        event.preventDefault();
        return false;
    } else {
        var district = $("#districts").find(":selected").text();
        var politics_party = $("#politics-party").find(":selected").text();
        $("h1").after("<div id=\"dialog-confirm\"></div>");
        $("#dialog-confirm").attr("title","Kandideerimine");
        $("#dialog-confirm").html('Piirkond: '+district+'<br/>Erakond: '+politics_party+'<br/>Kas soovite kandideerida?');
        $("#dialog-confirm" ).dialog({
                                resizable: false,
                                modal: true,
                                buttons: { 
                                    "Jah": function() {
                                        applyFor(party_id,region_id);
                                        $( this ).dialog( "close" );
                                    },
                                    "Ei": function() {
                                        $( this ).dialog( "close" );
                                    }
                                }
                            });
        return false;                    
    }
    return true; 
}
/* Kandideeri hääletamisele */
function applyFor(party_id,region_id) {
	$.post("/server/private/Nominate", JSON.stringify({"action":"nominate","region_id":region_id,"party_id":party_id}), function(data) {
		if (data.result=="success") {
			postToServer();
			window.location.reload();
		} 
	});
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
/* Sorteerimise funktsioonid */
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
	} else if (statisticsSortBy=="name" || statisticsSortBy=="party") {
        
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