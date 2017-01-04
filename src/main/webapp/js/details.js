$('.carousel.carousel-slider').carousel({
	full_width : true
});

$(document)
		.ready(
				function() {

					// Get Catgory
					var categoryid = getUrlParameter('id');
					// Load All Categories
					// Check if Category ID is provided					
					var nothing = loadCategories(categoryid);
					
					$.ajax({
						url : 'api/categories/all/processes/'+categoryid,
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
