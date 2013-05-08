$.getJSON("/server/GetVotes",{"all":45},function (data) {
	localStorage.setItem("all_votes",JSON.stringify(data));
	var count_regions = JSON.parse(localStorage.regions).regions.length;
	var count_partys = JSON.parse(localStorage.partys).partys.length;
	var candidates = [];
	var i,j,k;
	for (i=1;i<=count_partys;i++) {
		candidates = [];
		for (j=0;j<data.candidates.length;j++) {
			if (parseInt(data.candidates[j].party_name,10)===i) {
				candidates.push({ "person_name":data.candidates[j].person_name,"vote":data.candidates[j].vote});
			} 
		}
		localStorage.setItem("party_"+i,JSON.stringify({"candidates":candidates}));
	}
	for (i=1;i<=count_partys;i++) {
		for (j=1;j<=count_regions;j++) {
			candidates = [];
			for (k=0;k<data.candidates.length;k++) {
				if (parseInt(data.candidates[k].party_name,10)===i && parseInt(data.candidates[k].region_name,10)===j) {
					candidates.push({ "person_name":data.candidates[k].person_name,"vote":data.candidates[k].vote});
				}
			}
			localStorage.setItem("party_"+i+"_region_"+j,JSON.stringify({"candidates":candidates}));
		}	
	}
	var partys = [];
	var partys_name = JSON.parse(localStorage.partys).partys;
	var votecount = 0;
	var party = "";
	for (i=1;i<=count_partys;i++) {
		votecount = 0;
		for (j=0;j<data.candidates.length;j++) {
			if (parseInt(data.candidates[j].party_name,10)===0) {
				votecount+=data.candidates[j].vote;
			} 
		}
		party = { "name" : partys_name[i-1].name, "votes": votecount};
		partys.push(party);
	}
	
	localStorage.setItem("country_statistics",JSON.stringify({"partys":partys}));
	
	for (i=1;i<=count_regions;i++) {
		partys = [];
		for (j=1;j<=count_partys;j++) {
			votecount = 0;
			for (k=0;k<data.candidates.length;k++) {
				if (parseInt(data.candidates[k].party_name,10)===j && parseInt(data.candidates[k].region_name,10)===i) {
					votecount+=data.candidates[k].vote;
				}
			}
			party = { "name" : partys_name[j-1].name, "votes": votecount};
			partys.push(party);
		}
		localStorage.setItem("region_"+i,JSON.stringify({"partys":partys}));
	}
});