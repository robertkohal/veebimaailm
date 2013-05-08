var timeout = null;
$(document).ready(function() {
	"use strict";  
	$(".js-include").each(function(){
        var inc=$(this);
        if (typeof $("#header")[0]!=="undefined") {
			return;
        }
        $.get(inc.attr("title"), function(data){
            inc.replaceWith(data);
            var headerLink = document.getElementsByClassName("headerlink");
			for (var i=0;i<headerLink.length;i++) {
				$(headerLink[i]).bind("click",linkClicked);
                var link_local_part = $(headerLink[i]).attr("href").split("?");
                var link_page_name = link_local_part[1].split("=")[1];
				
                var current_local_part = location.href.split("?");
				var current_page_name = current_local_part[1].split("=")[1];
				
				var page_name_with_removed_params = current_page_name.split("&");
                if (link_page_name===page_name_with_removed_params[0]) {
                    $(headerLink[i]).trigger("click",location.href);
                }
			}
			generateloginbox();
        });
    });
	
	window.addEventListener('popstate', function(event) {
		console.log('popstate fired!');
		var filename = event.target.location.href;
		var current_local_part = "";
		if (filename!=="") {
			current_local_part = filename.split("?")[1];
			filename = getFileNameByPageParam(current_local_part.split(/=|&/)[1]);
		}
		updateContent(event.state, filename, current_local_part); 
		clearTimeout(timeout);
		timeout = setTimeout(function() {}, 50);
	});
});


function linkClicked(event,params) {
	"use strict";
	event.preventDefault();
	if (typeof params!== 'undefined') {
		event.target.href = params;
	}
	navigation(event);
	$(".activetab").removeClass("activetab");
	$(event.target).addClass("activetab");
	return false;
}
/* Login boxi genereerimine */
function generateloginbox() {
	"use strict";  
	if (window.location.protocol!=='https:') {
		generateNotLogedIn();
		return;
	}
	$.getJSON('/server/private/VerifyLogin', function(data) {
		$("#login").empty();
		if (data.result==="success") {
			var html = '<form action="/server/private/VerifyLogin" id="loginform" method="post" accept-charset="utf-8">' +
				'<div id="namefield"> Nimi: <b>'+data.username+'</b></div>' +
				'<input type="submit" name="Submit" value="Logi Välja"><br/>' + 
				'</form>';
			$("#login").append(html);
			$("#loginform").submit(function(event) {
				event.preventDefault();
				$.post("/server/private/VerifyLogin", { "logout": 5433 }, function() {
					if (window.location.protocol!=='http:') {
						window.location.href = window.location.href.replace("https","http");
						return;
					}
				});
			});
		} else {
			generateNotLogedIn();
		}
	});
}

function generateNotLogedIn() {
	"use strict";
	$("#login").empty();
	var html = "<p>Logi sisse ID-kaardiga</p>" +
				"<a href='#' id='loginlink'>" +
				"<img src='images/idkaart.gif' title='Sisse logimiseks tuleb vajutada vähemalt 2 korda ID-kaardi pildil. ID-kaart peab olema juba lugejas. Esimest korda suunatakse https-lehele ning alles teisel korral alles toimub reaalne sisselogimine.' /></a>";
	$("#login").append(html);
	$("#loginlink").bind("click",login);
 
}
function login() {
	"use strict";
	if (window.location.protocol!=='https:') {
		window.location.href = window.location.href.replace("http","https");
		return;
	}
	var request = { "login": true };	
	$.post("/server/private/VerifyLogin", request, function(data) {
		if (data.result==="success") {
			window.location.reload();
		} else {
			alert(data.error_code);
		}
	});
	event.preventDefault();
	return;
}