$('.carousel.carousel-slider').carousel({
	full_width : true
});

$('select').material_select('destroy');

/*
 * $(".ExistingDataForm").on("submit", function(e) { // var data =
 * JSON.stringify( $('#ExistingDataForm').serializeArray() ); var data =
 * $('#ExistingDataForm').serialize();
 * 
 * e.preventDefault(); $.ajax({ url : 'api/upload/process/', contentType :
 * 'multipart/form-data', type : 'POST', data : data, success : function(data) {
 * 
 * alert(data); }, fail : function(e) { // showPleaseWait(false); alert("Failed" +
 * e); }, error : function(XMLHttpRequest, textStatus, errorThrown) { //
 * showPleaseWait(false); // showPleaseWait(false); alert("Failed" +
 * errorThrown); } });
 * 
 * });
 */

$(document)
		.ready(
				function() {

					$(function() {
						$('.submit').click(function() {

							// alert("MMNS");
							$('#existingDataForm').submit();
							// parent.refreshIframe();
							// return false;

						});
					});

					$('select').material_select();

					/*
					 * var jsonProcesses = '{"refId":"Z3KVjUt31OVrmcJc", +
					 * '"name":"Payment Requests",' + '"description":"<p> This
					 * proces ...[2000]<p>"}' + '"iconstyle":"icon-cogs",' +
					 * '"processIcon" :
					 * "http://wira.appstore.co.ke/downloads/Z3KVjUt31OVrmcJc/icon.png",' +
					 * '"backgroundcolor":"red",' + '"status":[0,1],' +
					 * '"category":"Finance",' +
					 * '"downloadurl":"http://wira.appstore.co.ke/downloads/Z3KVjUt31OVrmcJc/Payment_Requests.zip",' +
					 * '"images":[/downloads/Z3KVjUt31OVrmcJc/image1.png,' +
					 * '/downloads/Z3KVjUt31OVrmcJc/image2.png,' +
					 * '/downloads/Z3KVjUt31OVrmcJc/image3.png]}';
					 * 
					 * var obj = $.parseJSON(jsonProcesses); for ( var i in obj) {
					 * //alert(obj[i]); }
					 * 
					 */

					
					// Get Catgory
					var categoryid = getUrlParameter('categoryid');
					// Load All Categories
					// Check if Category ID is provided

					var nothing = loadProcesses(categoryid);
					var nothing = loadCategories(categoryid);
					var nothing = loadPopularItems(categoryid);

					// categoryid = IvY5bw5VZzGK75F5

				});
