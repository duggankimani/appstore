$('.carousel.carousel-slider').carousel({
	full_width : true
});

$(document)
		.ready(
				function() {

					var getUrlParameter = function getUrlParameter(sParam) {
						var sPageURL = decodeURIComponent(window.location.search
								.substring(1)), sURLVariables = sPageURL
								.split('&'), sParameterName, i;

						for (i = 0; i < sURLVariables.length; i++) {
							sParameterName = sURLVariables[i].split('=');

							if (sParameterName[0] === sParam) {
								return sParameterName[1] === undefined ? true
										: sParameterName[1];
							}
						}

					};
					
					refId = getUrlParameter('id');
					
					$
					.ajax({
						url : 'api/processes/'+refId,
						async : false,
						success : function(data) {

							

								var refId = data['refId'];
								var isActive = data['isActive'];
								var id = data['id'];
								var name = data['name'];
								var description = data['description'];
								var iconStyle = data['iconStyle'];
								var backgroundColor = data['backgroundColor'];
								var processIcon = data['processIcon'];
								var category = data['category'];
								var status = data['status'];
								var classDeactivate = "";
								
								//alert(getUrlParameter("id"));
								
								$(".detail_name").html(name);
								$(".detail_description").html(description);
								$(".detail_category").html(category);


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
