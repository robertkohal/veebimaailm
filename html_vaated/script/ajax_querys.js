$(document).ready(function() {
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
		//alert($('#search-candidate').val());
	});
});
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
