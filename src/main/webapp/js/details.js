$('.carousel.carousel-slider').carousel({
	full_width : true
});
$(document).ready(function(){
    $('.carousel').carousel(
//    		indicators:true,
//    		full-width:true
    );
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
								// alert(getUrlParameter("id"));
								
								$(".detail_name").html(name);
								$(".detail_description").html(description);
								$(".detail_category").html(category);
								$("#aEditProcess").prop("href","addprocess.html?id="+refId);

								var attachments = data['attachments'];
								
								var carouselItems = "";
								for ( var idx in attachments) {
									var attachment = attachments[idx];
									var path = "api/files/"+refId+"/"+attachment['path'];
									if(path.includes("/screenshots/")){
										var attName = attachment['name'];
										carouselItems += "<a class=\"carousel-item white-text\" " +
												"style='background-color:red' "+
												"href='#slider"+idx+"!'>"+
												"<img src='"+path+"'></img>"+
												"</a>";
									}
									
								}
								
								$(".carousel-slider").html(carouselItems);
								$('.carousel').carousel();
								$('.carousel').carousel('set', 1);

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
