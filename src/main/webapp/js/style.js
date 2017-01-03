$('.carousel.carousel-slider').carousel({
	full_width : true
});

$('select').material_select('destroy');


$(".ExistingDataForm").on("submit", function (e) {
    
	//var data = JSON.stringify( $('#ExistingDataForm').serializeArray() );
	var data = $('#ExistingDataForm').serialize() ;
	
	e.preventDefault();
	$.ajax({
		url : 'api/upload/process',
		contentType: 'multipart/form-data',
	    type: 'POST',
	    data : data,
		success : function(data) {

			alert(data);

		},
		fail : function() {

			// showPleaseWait(false);

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

			// showPleaseWait(false);

		}
	});

});


$(document).ready(function() {

	$('select').material_select();

	/*
	 * var jsonProcesses = '{"refId":"Z3KVjUt31OVrmcJc", + '"name":"Payment
	 * Requests",' + '"description":"<p> This proces ...[2000]<p>"}' +
	 * '"iconstyle":"icon-cogs",' + '"processIcon" :
	 * "http://wira.appstore.co.ke/downloads/Z3KVjUt31OVrmcJc/icon.png",' +
	 * '"backgroundcolor":"red",' + '"status":[0,1],' + '"category":"Finance",' +
	 * '"downloadurl":"http://wira.appstore.co.ke/downloads/Z3KVjUt31OVrmcJc/Payment_Requests.zip",' +
	 * '"images":[/downloads/Z3KVjUt31OVrmcJc/image1.png,' +
	 * '/downloads/Z3KVjUt31OVrmcJc/image2.png,' +
	 * '/downloads/Z3KVjUt31OVrmcJc/image3.png]}';
	 * 
	 * var obj = $.parseJSON(jsonProcesses); for ( var i in obj) {
	 * //alert(obj[i]); }
	 * 
	 */

	$.ajax({
		url : 'json/process.json',
		async : false,
		success : function(data) {

		},
		fail : function() {
			alert("f");
		},
		complete : function(data) {
			// alert("c");

			/*
			 * var t = data; var data = t.replace(/&quot;/ig,'"'); dataRes =
			 * jQuery.parseJSON(data); var $mainDetailsArr = dataRes[0];
			 * alert($mainDetailsArr);
			 */

			// $("#spotlightList").html($htmlSpotlightYoutube);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

		}
	});

});
