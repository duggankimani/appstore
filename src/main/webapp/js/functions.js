var resetForm = function resetForm(form){
	$(form).find('input[name=\'refId\']').val('');
	$("newUploadsForm").find('input[name=\'refId\']').val('');
	$(form)[0].reset();
};

var isValid = function isValid(el){
	if($(el).val()=='' && $(el).hasClass('validate')){
		$(el).addClass('invalid');
		return false;
	}
	
	return true;
};

var showDetails = function showDetails(id) {

	alert(id);
	// id = id.split('_');
	window.location.replace("details.html?id=" + id);
	// appstore/api/processes/p4LppspxJS7yF61q

};

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


var loadPopularItems = function loadPopularItems(categoryid) {
	
	$.ajax({
		url : "api/categories/all/favprocesses",
		async : false,
		success : function(data) {

			$("#favourite_listings").html("");

			var htmlDataActive = "";// "<ul
			var htmlDataInActive = "";
			// id='slide-out'
			// class='side-nav'>";
			// Header Definition

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

				if (status == "AVAILABLE") {

					htmlDataActive += "<a id='" + refId
							+ "' href='details.html?id="
							+ refId + "'><div style='background:"
							+ backgroundColor
							+ "' class='card cards_listing'>";
					htmlDataActive += "<h2 class='center light-blue-text'>"
					htmlDataActive += "<i class='" + iconStyle
							+ "'></i>";
					htmlDataActive += "</h2>"
					htmlDataActive += "<div class='card-content'>"
					htmlDataActive += "<h5 class='center'>" + name
							+ "</h5></a>"
					htmlDataActive += "</div>";
					htmlDataActive += "<div class='card-action'>";
					htmlDataActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
					htmlDataActive += "</div></div>";

					// classDeactivate =
					// "deactivate";

				} else if (status == "UPCOMING") {


					htmlDataInActive += "<a id='" + refId
							+ "' href='details.html?id="
							+ refId + "'><div style='background:"
							+ backgroundColor
							+ "' class='card cards_listing'>";
					htmlDataInActive += "<h2 class='center light-blue-text'>"
					htmlDataInActive += "<i class='" + iconStyle
							+ "'></i>";
					htmlDataInActive += "</h2>"
					htmlDataInActive += "<div class='card-content'>"
					htmlDataInActive += "<h5 class='center'>" + name
							+ "</h5></a>"
					htmlDataInActive += "</div>";
					htmlDataInActive += "<div class='card-action'>";
					htmlDataInActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
					htmlDataInActive += "</div></div>";

				}
			}


			$("#favourite_listings").html(htmlDataActive);

		},
		fail : function() {
			alert("f");
		},
		complete : function(data) {

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

		}
	});
	
}

var loadCategories = function loadCategories(categoryid) {

	var url = "";
	var allUrl = "";

	if (categoryid == "" || categoryid == "undefined"
			|| categoryid == undefined) {


	} else {

		allUrl = "<li><a href='index.html'>All</a></li>";
		;

	}
	
	$.ajax({
		url : "api/categories",
		async : false,
		success : function(data) {

			$("#category_listings").html("");

			var htmlCategory = "";// "<ul
			// id='slide-out'
			// class='side-nav'>";
			// Header Definition
			
			var options = "";

			for ( var items in data) {

				var refId = data[items]['refId'];
				var isActive = data[items]['isActive'];
				var id = data[items]['id'];
				var name = data[items]['name'];

				// alert("Name");

				if (isActive == "1") {

					htmlCategory += "<li><a href='index.html?categoryid="
							+ refId + "'>" + name + "</a></li>";
					// classDeactivate =
					// "deactivate";

				} else if (isActive == "0") {

					htmlCategory += "<li><a href='index.html?categoryid="
							+ refId + "'>" + name + "</a></li>";
					// classDeactivate =
					// "deactivate";

				}
				
				var key = name;
				options +="<option value='"+key+"'>"+name+"</option>";
			}

			// htmlCategory += "</ul>";
			// alert(htmlCategory);
			htmlCategory += allUrl;
			$("#category_listings").html(htmlCategory);
			if($("#category").length){
				$("#category").html(options);
			}

		},
		fail : function() {
			alert("f");
		},
		complete : function(data) {

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

		}
	});

};

var loadProcesses = function loadProcesses(categoryid) {

	var url = "";
	var allUrl = "";

	if (categoryid == "" || categoryid == "undefined"
			|| categoryid == undefined) {

		url = "api/categories/all/processes";

	} else {

		url = "api/categories/" + categoryid + "/processes";
		allUrl = "<li><a href='index.html'>All</a></li>";
		;

	}
	// Load All Processes
	$
			.ajax({
				url : url,
				async : false,
				success : function(data) {

					$("#available_processes").html("");

					var htmlDataActive = "";
					// Header Definition
					var htmlDataInActive = "";
					
					var selCategory = "";
					
					var inactive = 0;

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
						if(categoryid!=null && categoryid!=''){
							selCategory = category;
						}
						var status = data[items]['status'];
						var classDeactivate = "";

						if (status == "AVAILABLE") {

							htmlDataActive += "<div  id='" + refId
									+ "' class='processItem col s12 m3 "
									+ classDeactivate + "'>";
							htmlDataActive += "<a href='details.html?id="
									+ refId + "'><div style='background:"
									+ backgroundColor
									+ "' class='card cards_listing'>";
							htmlDataActive += "<h2 class='center light-blue-text'>"
							htmlDataActive += "<i class='" + iconStyle
									+ "'></i>";
							htmlDataActive += "</h2>"
							htmlDataActive += "<div class='card-content'>"
							htmlDataActive += "<h5 class='center'>" + name
									+ "</h5></a>"
							htmlDataActive += "</div>";
							htmlDataActive += "<div class='card-action'>";
							htmlDataActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
							htmlDataActive += "</div></div></div>";

							// classDeactivate =
							// "deactivate";

						} else if (status == "UPCOMING") {
							++inactive;
							htmlDataInActive += "<div  id='" + refId
									+ "' class='col s12 m3 " + classDeactivate
									+ "'>";
							htmlDataInActive += "<a href='details.html?id="
									+ refId + "'><div style='background:"
									+ backgroundColor
									+ "' class='card cards_listing'>";
							htmlDataInActive += "<h2 class='center light-blue-text'>"
							htmlDataInActive += "<i class='" + iconStyle
									+ "'></i>";
							htmlDataInActive += "</h2>"
							htmlDataInActive += "<div class='card-content'>"
							htmlDataInActive += "<h5 class='center'>" + name
									+ "</h5></a>"
							htmlDataInActive += "</div>";
							htmlDataInActive += "<div class='card-action'>";
							htmlDataInActive += "<a href='details.html'>VIEW</a><span class='free' href='#'>FREE</span>";
							htmlDataInActive += "</div></div></div>";

						}
					}
					
					if(selCategory==''){
						activeHeading = "<div class='col s12 m10'><h5>Available Processes</h5><p><span>"
							+ data.length + "</span> Processes</p></div>";
					}else{
						activeHeading = "<div class='col s12 m10'><h5>"+category+" Processes</h5><p><span>"
							+ data.length + "</span> Processes</p></div>";
					}
					
					if(inactive==0){
						inactiveHeading = "<div class='col s12 m10 hide'><h5>Upcoming Processes</h5><p><span>" +
						"" +inactive+
						"</span> Processes</p></div>";
					}else{
						inactiveHeading = "<div class='col s12 m10'><h5>Upcoming Processes</h5><p><span>" +
						"" +inactive+
						"</span> Processes</p></div>";
					}
					
				

					$("#available_processes").html("");
					$("#upcoming_processes").html("");
					$("#available_processes").html(activeHeading+htmlDataActive);
					$("#upcoming_processes").html(inactiveHeading+htmlDataInActive);

				},
				fail : function() {
					alert("f");
				},
				complete : function(data) {

				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {

				}
			});

};