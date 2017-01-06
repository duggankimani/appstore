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
	var form = this;
	var valid = true; 
	if(!isValid($(this).find('input[name=\'name\']'))){
		valid = false;
	}
	
	if(!isValid($(this).find('input[name=\'iconStyle\']'))){
		valid = false;
	}
	
	if(!isValid($(this).find('input[name=\'backgroundColor\']'))){
		valid = false;
	}
	
	if(!valid){
		alert('Please fill in all mandatory fields.');
		return;
	}
	
	var formData = JSON.stringify($( this ).serializeObject());
		
	
	$.ajax({
		url : 'api/categories/all/processes',
		contentType : 'application/json',
		type : 'POST',
		data : formData,
		dataType : 'json',
		success : function(data) {
			$(form).find('input[name=\'refId\']').val(data.refId);
			$('#newUploadsForm').find('input[name=\'refId\']').val(data.refId);
			Materialize.toast('Process Saved!', 4000);
			//resetForm(form);
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


var loadProcess = function loadProcess() {
	// Get Catgory
	var processRef = getUrlParameter('id');
	if(typeof processRef == "undefined"){
		return;
	}
	
	// Load All Categories
	// Check if Category ID is provided					
	console.log('Loading ProcessRef = ' + processRef);
	
	$.ajax({
		url : 'api/categories/all/processes/' + processRef,
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
			var attachments = data['attachments'];
			//alert(getUrlParameter("id"));

			var form = $("#newDataForm");
			$(form).find('input[name=\'refId\']').val(refId);
			$("#newUploadsForm").find('input[name=\'refId\']').val(refId);
			
			$(form).find('input[name=\'name\']').val(name);
			$("#newUploadsForm").find('input[name=\'name\']').val(name);
			
			$(form).find('input[name=\'iconStyle\']').val(iconStyle);
			$(form).find('input[name=\'backgroundColor\']').val(
					backgroundColor);
			$(form).find('textarea[name=\'description\']').val(description);
			$(form).find('select[name=\'category\']').val(category);
			if(status==null){
				status = 'Available';
			}
			$(form).find('select[name=\'status\']').val(status);
			
			
			var tableRows= "";
			for ( var idx in attachments) {
				var attachment = attachments[idx];
				tableRows += "<tr>";
				
				var path = "files/"+refId+"/"+attachment['path'];
				var attName = attachment['name'];
				tableRows += "<td><a href=\"api/"+path+"\" target=\"_blank\" class=\"waves-effect waves-teal btn-flat\">"+attName+"</a></div></td>";
				
				var attModified = attachment['lastModified'];
				tableRows += "<td>"+attModified+"</td>";
				
				tableRows += "<td><div><a href=\""+path+"\" class=\"waves-effect waves-teal btn-flat delete\">Delete</a></div></td>";
				tableRows += "</tr>";
			}
			
			$("#attachments").html(tableRows);
			
			$("#attachments").find("a.delete").click(function(e){
				var href = $(this).attr('href');
				console.log('delete '+href);
				deleteFile(href);
				e.preventDefault();
			});
		},
		fail : function() {
			alert("failed");
		},
		complete : function(data) {
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
};


var deleteFile = function deleteFile(filePath){
	alert("Delete - File Path - "+filePath);
	
	$.ajax({
		url : filePath,
		type : 'DELETE',
		async : false,
		success : function(){
			alert('Success ...');
		},
		fail : function() {
			alert("failed");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error '+errorThrown);
		}
	});
} 
