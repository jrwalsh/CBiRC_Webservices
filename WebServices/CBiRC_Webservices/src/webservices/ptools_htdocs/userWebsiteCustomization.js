YAHOO.namespace ("ptools");

// Add the orgids for your site's popular databases here:
YAHOO.ptools.popularDatabases = [];

/*
   Use this function to display a temporary message in the current web page.
   It will currently display the message entered in the div
   marked by id "userTemporaryMsg" in file temporary-message.shtml
*/
function insertTemporaryMessage(){
    var tmp = document.getElementById('userTemporaryMsg');

    if (tmp == null || tmp == undefined) return;

    // If there is no real message do not try to display as it takes space
    // on the page even if the message is empty.

    if (tmp.innerHTML.search('[^ \n\t]') >= 0) { 
	tmp.style.width='600px'; tmp.style.display  = 'block'; 
	return; 
    } else {
	tmp.style.width=0; tmp.style.display  = 'none'; 
	return; 
    }
}

function userDefinedAfterPathwayToolsInit() {
	getSlot('COMMON-NAME');
}

function getSlot(slotName) {
	var browser = navigator.appName;
        if (browser == "Microsoft Internet Explorer") {
                var http = createRequestObject();
                var request = 'http://jrwalsh.student.iastate.edu/getFrameSlots.php?org=' + typeObjectPage.orgid + '&frame=' + typeObjectPage.object + '&slot=' + slotName;
                http.open('GET', request, false);
                http.send();
                var response=http.responseText;
                var tmp = document.getElementById('jrwalshSection');
                if (response.length > 0) {
                        tmp.innerHTML=response;
                } else {
                        tmp.innerHTML=request;
                }
        }

}

function createRequestObject() {
	var request_o; //declare the variable to hold the object.
	var browser = navigator.appName; //find the browser name
	if(browser == "Microsoft Internet Explorer"){
		/* Create the object using MSIE's method */
		request_o = new ActiveXObject("Microsoft.XMLHTTP");
	}else{
		/* Create the object using other browser's method */
		request_o = new XMLHttpRequest();
	}
	return request_o; //return the object
}

/* Function called to handle the list that was returned from the internal_request.php file.. */
function handleThoughts(){
	/* Make sure that the transaction has finished. The XMLHttpRequest object 
		has a property called readyState with several states:
		0: Uninitialized
		1: Loading
		2: Loaded
		3: Interactive
		4: Finished */
	try
	{
		if(http.readyState == 4){ //Finished loading the response
			/* We have got the response from the server-side script,
				let's see just what it was. using the responseText property of 
				the XMLHttpRequest object. */
			return http.responseText;
		}
	}
	catch(Exception)
	{
	}

}

function gup( name ) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}

