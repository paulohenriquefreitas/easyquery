$(document).ready( function() {
	  var form = $('#query');
	  var resultado = $('.resultadoBusca');
	  
	  resultado.empty();
	  form.submit( function(event) {
		  resultado.children().each(function() { $(this).remove(); });
		  $('#loading-indicator').show();
		  $('#submit').prop( "disabled", true);
	   var request=  $.ajax( {
	      type: "GET",
	      url: form.attr( 'action' ),
	      data: form.serialize(),
	      success: function( response ) {
	    	resultado.html(response);
	    	$('#loading-indicator').hide();
	    	 $('#submit').prop( "disabled", false);
	      }
	    } );
	    event.preventDefault();
	  } );

	} );
    
function enabledFunction() {
    var div = document.getElementById('numPartnerDiv');
    div.style.display = 'block';
    var div = document.getElementById('stockItemDiv');
    div.style.display = 'block';
    document.getElementById('kitDiv');
    kitFalse.checked = true;
    
    
    
    
}
function disabledFunction() {
	var div = document.getElementById('numPartnerDiv');
    div.style.display = 'none';
    var div = document.getElementById('stockItemDiv');
    div.style.display = 'block';
}
function disableMarketPlace() {
	 document.getElementById('mktDiv');
	 b2w.checked = true;
	 var div = document.getElementById('numPartnerDiv');
	 div.style.display = 'none';
}

function enableReembalados(){
	var div = document.getElementById('rewrappedDiv');
    div.style.display = 'block';
    document.getElementById('rewrappedDiv');
    newSoub.checked = true;
}

function disableReembalados(){
	var div = document.getElementById('rewrappedDiv');
    div.style.display = 'none';
    document.getElementById('rewrappedDiv');
    newSoub.checked = false;
    rewrapped.checked = false;
}




function validate(evt) {
	  var theEvent = evt || window.event;
	  var key = theEvent.keyCode || theEvent.which;
	  key = String.fromCharCode( key );
	  var regex = /[0-9 ]|\./;
	  if( !regex.test(key) ) {
	    theEvent.returnValue = false;
	    if(theEvent.preventDefault) theEvent.preventDefault();
	  }
}

function hideCanvasLoader(){
	var div = document.getElementById('loading-indicator');
    div.style.display = 'none';
    document.getElementById("submit").disabled = false;
}