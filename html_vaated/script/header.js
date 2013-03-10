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
                    $("#content").append("<img id=\"map\" src=\"images/kontuurkaart.jpg\" alt=\"kaart\" />");
                    continue;
                }
                if (filenameLink==filenameLocation) {
                    $(headerLink[i]).trigger("click");
                }
        	}
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
