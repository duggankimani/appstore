var Google_Client_Status='';
var CLIENT_ID='';

function start() {
	
	gapi.load('auth2', function() {
	    auth2 = gapi.auth2.init({
	      client_id: "631292106887-7bemucklm2qtsbsc2933l3bfin9bp9pb.apps.googleusercontent.com",
	      // Scopes to request in addition to 'profile' and 'email'
	      //scope: 'additional_scope'
	    });
	    
	 // Listen for sign-in state changes.
	    auth2.isSignedIn.listen(signinChanged);

	    // Listen for changes to current user.
	    auth2.currentUser.listen(userChanged);

	    // Sign in the user if they are currently signed in.
	    if (auth2.isSignedIn.get() == true) {
	      auth2.signIn();
	    }

	    // Start with the current live values.
	    refreshValues();
	  });
}

/**
 * Listener method for sign-out live value.
 *
 * @param {boolean} val the updated signed out state.
 */
var signinChanged = function (val) {
 
};

/**
 * Listener method for when the user changes.
 *
 * @param {GoogleUser} user the updated user.
 */
var userChanged = function (user) {
  googleUser = user;
  updateGoogleUser();
};

/**
 * Updates the properties in the Google User table using the current user.
 */
var updateGoogleUser = function () {
  if (googleUser) {
    var id_token=googleUser.getAuthResponse().id_token;
    signInCallback(id_token, 'auth2tokencallback');
  } else {
    //googleUser=null
  }
};

/**
 * Retrieves the current user and signed in states from the GoogleAuth
 * object.
 */
var refreshValues = function() {
  if (auth2){
    googleUser = auth2.currentUser.get();
    updateGoogleUser();
  }
}

function signInCallback(authResult) {
  if (authResult['code']) {

    // Hide the sign-in button now that the user is authorized, for example:
    $('#Google_Login').attr('style', 'display: none');

    // Send the code to the server
    $.ajax({
      type: 'POST',
      url: 'oauth2callback',
      contentType: 'application/octet-stream; charset=utf-8',
      success: function(result) {
        // Handle or verify the server response.
    	  window.location='addprocess.html';
      },
      processData: false,
      data: authResult['code']
    });
  } else {
    // There was an error.
  }
}

function signInCallback(code, targetUrl) {
  if (code) {
	var redirectUrl = window.location.href;
	if(redirectUrl.includes("?redirect")){
		var idx = redirectUrl.indexOf("?");
		redirectUrl = redirectUrl.substring(idx+9+1);
		alert("redirect = "+redirectUrl);
	}
    // Hide the sign-in button now that the user is authorized, for example:
    $('#Google_Login').attr('style', 'display: none');

    // Send the code to the server
    $.ajax({
      type: 'POST',
      url: targetUrl,
      contentType: 'application/octet-stream; charset=utf-8',
      success: function(result) {
        // Handle or verify the server response.
    	  $("body").hide();
    	  window.location=redirectUrl;
      },
      processData: false,
      data: code
    });
  } else {
    // There was an error.
  }
}


$(document).ready(function(){
//		$.ajax({
//			type:"GET",
//			url:"googleservlet",
//			dataType: 'json'
//		})
//		.done(function(data) {
//			Google_Client_Status = $(data)[0].GOOGLE_CLIENT_STATUS;
//			CLIENT_ID = $(data)[0].CLIENT_ID;
//			
//			if(Google_Client_Status!='ACTIVE'){
//				$('#Google_Login').parent().hide();
//			}else{
//				$('#Google_Login').parent().show();
//			}
//			
//		})
//		.fail(function() {
//			$('#Google_Login').parent().hide();
//		})
//		.always(function() {
//			
//		});
});

function loginWithCookie(){
	var str = "loginmethod=VIA_COOKIE"
	$.ajax({
		type:"POST",
		url:"auth",
		data:str
	})
	.done(function() {
		window.location='index.html';
	})
	.fail(function() {
	})
	.always(function() {
	});	
}

function doFormSubmit() {
	var form = $('#status > form');
	// 'this' refers to the current submitted form
	var str = form.serialize();
	
	$('#issues').hide();
	$('#submit').prop("disabled",true);
	
	$.ajax({
		type:"POST",
		url:"auth",
		data:str
	})
	.done(function() {
		$("body").hide();
		window.location='addprocess.html';
	})
	.fail(function() {
		$('#issues').show();
	})
	.always(function() {
		$('#submit').prop("disabled",false);
	});

};