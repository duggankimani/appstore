$('.carousel.carousel-slider').carousel({
	full_width : true
});

$(document).ready(function(){
$('.carousel').carousel(
//    		indicators:true,
//    		full-width:true
);
});

$( ".next" ).click(function() {
	$("li.active").next().click();
});

$( ".previous" ).click(function() {
	$("li.active").prev().click();
});


$("li.start").next().css({"color": "red", "border": "2px solid red"});

$(document)
		.ready(
				
				function() {
					loadPopularItems();
					
					// Get Catgory
					var processref = getUrlParameter('id');
					$("#demo-link").attr("href","demo.html?id="+processref);
					// Load All Categories
					// Check if Category ID is provided
					var nothing = loadCategories(processref);
					
					$.ajax({
						url : 'api/categories/all/processes/'+processref,
						async : true,
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
									path = path.replace("\\", "/"); //windows file paths
									if(path.includes("/screenshots/")){
										var attName = attachment['name'];
										carouselItems += "<a class=\"carousel-item white-text\" " +
												"href='#slider"+idx+"!'>"+
												"<img src='"+path+"'></img>"+
												"</a>";
										
									}else if(path.endsWith(".zip")){
										$(".download_process").removeClass("hide");
										$(".download_process").attr("href",path);
									}
									
								}
								
								if(carouselItems==""){
									if(backgroundColor!=null){
										carouselItems="<div class=\"carousel-item white-text\" " +
												"style='background-color:"+backgroundColor+"'" +
										"href=\"#one!\"> " +
										"<h2>No Images</h2> " +
										"<p class=\"white-text\">" +
										"No images uploaded for this process.</p></div>";
							
									}else{
										carouselItems="<div class=\"carousel-item green white-text\" " +
										"href=\"#one!\"> " +
										"<h2>No Images</h2> " +
										"<p class=\"white-text\">" +
										"No images uploaded for this process.</p></div>";
							
									}
									
								}
								
								if(attachments.length<2){
									$(".next").addClass("hide");
									$(".previous").addClass("hide");
								}
								
								$(".carousel").html(carouselItems);
								$(".carousel").removeClass('initialized');
								$('.carousel.carousel-slider').carousel({full_width : true});

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
