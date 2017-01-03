$('.carousel.carousel-slider').carousel({
	full_width : true
});

$('select').material_select('destroy');


$(".ExistingDataForm").on("submit", function(e) {

	// var data = JSON.stringify( $('#ExistingDataForm').serializeArray() );
	var data = $('#ExistingDataForm').serialize();

	e.preventDefault();
	$.ajax({
		url : 'api/upload/process/',
		contentType : 'multipart/form-data',
		type : 'POST',
		data : data,
		success : function(data) {

			alert(data);

		},
		fail : function(e) {

			// showPleaseWait(false);
			alert("Failed" + e);

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

			// showPleaseWait(false);
			// showPleaseWait(false);
			alert("Failed" + errorThrown);

		}
	});

});

function showDetails(id){
	
	alert(id);
	//id = id.split('_');
	window.location.replace("details.html?id="+id);										
	//appstore/api/processes/p4LppspxJS7yF61q
										
}

$(document)
		.ready(
				function() {

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

					$
							.ajax({
								url : 'api/processes',
								async : false,
								success : function(data) {

									$("#available_processes").html("");

									var htmlDataActive = "";

									// Header Definition
									htmlDataActive = "<div class='col s12 m10'><h5>Available Processes</h5><p><span>"
											+ data.length
											+ "</span> Processes</p></div>";

									// Header Definition
									htmlDataInActive = "<div class='col s12 m10'><h5>Upcoming Processes</h5><p><span>0</span> Processes</p></div>";

									for ( var items in data) {

										var refId = data[items]['refId'];
										var isActive = data[items]['isActive'];
										var id = data[items]['id'];
										var name = data[items]['name'];
										var description = data[items]['description'];
										var iconStyle = data[items]['iconStyle'];
										var backgroundColor = data[items]['backgroundColor'];
										var processIcon = data[items]['processIcon'];
										var category = data[items]['category'];
										var status = data[items]['status'];
										var classDeactivate = "";

	
										if (isActive == "1") {

											htmlDataActive += "<div  id='"
													+ refId
													+ "' class='processItem col s12 m3 "
													+ classDeactivate + "'>";
											htmlDataActive += "<a href='details.html?id="+ refId +"'><div style='background:"+ backgroundColor +"' class='card cards_listing'>";
											htmlDataActive += "<h2 class='center light-blue-text'>"
											htmlDataActive += "<i class='"
													+ iconStyle + "'></i>";
											htmlDataActive += "</h2>"
											htmlDataActive += "<div class='card-content'>"
											htmlDataActive += "<h5 class='center'>"
													+ name + "</h5></a>"
											htmlDataActive += "</div>";
											htmlDataActive += "<div class='card-action'>";
											htmlDataActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
											htmlDataActive += "</div></div></div>";

											// classDeactivate = "deactivate";

										} else if (isActive == "0") {

											htmlDataInActive += "<div  id='"
													+ refId
													+ "' class='col s12 m3 "
													+ classDeactivate + "'>";
											htmlDataInActive += "<a href='details.html?id="+ refId +"'><div style='background:"+ backgroundColor +"' class='card cards_listing'>";
											htmlDataInActive += "<h2 class='center light-blue-text'>"
											htmlDataInActive += "<i class='"
													+ iconStyle + "'></i>";
											htmlDataInActive += "</h2>"
											htmlDataInActive += "<div class='card-content'>"
											htmlDataInActive += "<h5 class='center'>"
													+ name + "</h5></a>"
											htmlDataInActive += "</div>";
											htmlDataInActive += "<div class='card-action'>";
											htmlDataInActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
											htmlDataInActive += "</div></div></div>";

										}
									}

									$("#available_processes").html("");
									$("#upcoming_processes").html("");
									$("#available_processes").html(
											htmlDataActive);
									$("#upcoming_processes").html(
											htmlDataInActive);

								},
								fail : function() {
									alert("f");
								},
								complete : function(data) {
									
									
	
								},
								error : function(XMLHttpRequest, textStatus,
										errorThrown) {

								}
							});

				});
