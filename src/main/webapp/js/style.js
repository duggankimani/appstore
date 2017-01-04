$('.carousel.carousel-slider').carousel({
	full_width : true
});

$('select').material_select('destroy');

$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$("#newDataForm").on("submit", function(e) {
//	var data = $('#newDataForm').serialize();
	e.preventDefault();
	var nameField = $(this).find('input[name=\'name\']');
	if($(nameField).val()==''){
		alert('Name is mandatory');
		return;
	}
	
	var iconStyle = $(this).find('input[name=\'iconStyle\']');
	if($(iconStyle).val()==''){
		$(iconStyle).val('icon-pencil');
	}
	
	var formData = JSON.stringify($( this ).serializeObject());
		
	
	$.ajax({
		url : 'api/categories/all/processes',
		contentType : 'application/json',
		type : 'POST',
		data : formData,
		success : function(data) {
			//var refId = $(this).find('input[name=\'refId\']');
			//var d = $.parseJSON(data);
//			alert(JSON.parse(data));
		},
		fail : function(e) {
			// showPleaseWait(false); alert("Failed"e);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// showPleaseWait(false);
			alert("Failed" + errorThrown);
		}
	});

});

$(document).ready(function() {

	$(function() {
		$('.submit').click(function() {
			//alert("MMNS");
			$(this).closest('form').submit();
		});
	});
	
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

	// Get Catgory
	var categoryid = getUrlParameter('categoryid');
	// Load All Categories
	// Check if Category ID is provided

	var nothing = loadProcesses(categoryid);
	var nothing = loadCategories(categoryid);
	var nothing = loadPopularItems(categoryid);

	// categoryid = IvY5bw5VZzGK75F5

});
