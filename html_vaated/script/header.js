$(document).ready(function() {
    $(document).ajaxSend(function (event, request, settings) {
        request.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    });

	$(".js-include").each(function(){
        var inc=$(this);
        if (typeof $("#header")[0]!="undefined") {
        	return;
        }
        $.get(inc.attr("title"), function(data){
            inc.replaceWith(data);
            
            var headerLink = document.getElementsByClassName("headerlink");
        	for (var i=0;i<headerLink.length;i++) {
        		$(headerLink[i]).bind("click",linkClicked);
                var index = $(headerLink[i]).attr("href").lastIndexOf("/") + 1;
                var filenameLink = $(headerLink[i]).attr("href").substr(index);
                index = location.href.lastIndexOf("/") + 1;
                filenameLocation = location.href.substr(index);
                if (filenameLocation=="index.html" || filenameLocation=="" ) {
                    if (!$("#map").length) {
                        $("#content").append("<img id=\"map\" src=\"images/kontuurkaart.jpg\" alt=\"kaart\" />");
                    }
                    continue;
                }
                if (filenameLink==filenameLocation) {
                    $(headerLink[i]).trigger("click");
                }
        	}
			generateloginbox();
        });
    });
	window.addEventListener('popstate', function(event) {
	console.log('popstate fired!');
    var filename = event.target.location.href;
    if (filename!="") {
         var index = filename.lastIndexOf("/") + 1;
         filename = filename.substr(index);
    }
	updateContent(event.state,filename); 
	});
});
function linkClicked(event) {
	navigation(event);
	event.preventDefault();
	return false;
}
function generateloginbox() {
	$("#login").hover(function() {
		$("#loginform").toggle();
	});
	$.getJSON('/server/VerifyLogin', function(data) {
		$("#logform").empty();
		if (data.result=="success") {
			var form = '<form action="/server/VerifyLogin" id="loginform" method="post" accept-charset="utf-8">'
				 + '<div id="namefield" style="text-transform:capitalize;"> Nimi: <b>'+data.username+'</b></div>'
				 + '<input type="submit" name="Submit" value="Logi Välja"><br/>' 
				 + '</form>';
			$("#logform").append(form);
			$("#loginform").submit(function(event) {
				event.preventDefault();
				$.post("/server/VerifyLogin", { "logout": 5433 }, function(data) {
					window.location.reload();
				});
			});
		} else {
			var form = '<form action="/server/VerifyLogin" id="loginform" method="post" accept-charset="utf-8">'
					 + '<label for="username">Kasutajanimi: </label>'
					 + '<input type="text" name="username" id="username"/><br/>'
					 + '<label for="password">Parool: </label>'  
					 + '<input type="password" name="userpass" id="userpass"/><br/>'
					 + '<label for="key">Registreerimisvõti: </label>'
					 + '<input type="text" name="key" id="key"/><br/>'
					 + '<input type="submit" name="Submit" value="Meldi"><br/>' 
					 + '</form>';
			$("#logform").append(form);
			$("#loginform").submit(function(event) {
				event.preventDefault();
				//
				var username = $("#username").val();
				var password = $("#userpass").val();
				var registry_key = $("#key").val();
				if (registry_key.length==0) {
					var request = { "username": username, "password": password };		
				} else {
					var request = { "username": username, "password": password,"key":registry_key };
				}
				$.post("/server/VerifyLogin", request, function(data) {
					if (data.result=="success") {
						window.location.reload();
					} else {
						alert(data.error_code);
					}
				});
			});
		}
	});
}
