<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Easy Query</title>
<link type="text/css" href="/resources/css/bootstrap.css" rel="stylesheet"/>
<link type="text/css" href="/resources/css/hover.css" rel="stylesheet"/>
<link type="text/css" href="/resources/css/toggle-switch.css" rel="stylesheet"/>
<link type="text/css" href="/resources/css/select2.css" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" href="/resources/css/jquery.tokenize.css" />
<link rel="shortcut icon" href="/resources/image/Java_Log2o.png"/>

<script src="http://code.jquery.com/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<script src="/resources/js/bootstrap-select.js"></script>
<script src="/resources/js/select2.js"></script>
<script type="text/javascript" src="/resources/js/jquery.tokenize.js"></script>
<script type="text/javascript" src="/resources/js/innerjs.js"></script>
<script src="/resources/js/heartcode-canvasloader.js"></script>

<link rel="stylesheet" href="/resources/css/style.css">
<link rel="stylesheet" href="/resources/css/prism.css">
<link rel="stylesheet" href="/resources/css/chosen.css">

<style type="text/css">

.searchclear {

    right:-17px;
    top:0;
  
    font-size:12px;
    cursor:pointer;
    color:#EEE;
}
.circle { 
    right:-17px;
    top:0;
  width: 19px; 
  height: 19px; 
  background: #007EB8; 
  -moz-border-radius: 50px; 
  -webkit-border-radius: 50px; 
  border-radius: 50px; 
  padding:0px 5px 1px 5px;
}

</style>

<script type="text/javascript">
/* $(document).ready(function(){
    $(".searchinputsku").keyup(function(){
        $(".searchclear").toggle(Boolean($(this).val()));
    });
    $(".searchclear").toggle(Boolean($(".searchinputsku").val()));
    $(".searchclear").click(function(){
        $(".searchinputsku").val('').focus();
        $(this).hide();
    });
}); */

$(document).ready(function(){
    $(".searchinput").keyup(function(){
    	$(this).parents('.controls').find('.searchclear').toggle(Boolean($(this).val()));
    });
    
    $(".searchclear").each(function() {
   		$(this).toggle(Boolean($(this).parents('.controls').find(".searchinput").val()));
    });
    
    $(".searchclear").click(function(){
    	$(this).parents('.controls').find(".searchinput").val('').focus();
        $(this).hide();
    });
});

</script>
</head>
<body >

		
<div class="container">
	<div class="row clearfix">
		<div class="col-md-12 column">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<img alt="40x40" src="/resources/image/Java_Log2o.png" class="img-rounded" />
						<span>Easy Query</span>
					</h3>
				</div>
			</div>
		</div>
	</div>
</div>
					
<form:form class="form-horizontal"  action="/busca" method="get" modelAttribute="query" name="formQuery" >
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
			     <div id=mode-group class="btn-group btn-group-justified font-group " data-toggle="buttons">
			      <label class="btn btn-primary active" onclick="disableReembalados()"><form:radiobutton path="brand" value="submarino"  checked="checked"/>SUBMARINO</label>			        
			      <label class="btn btn-danger" onclick="disableReembalados()"><form:radiobutton path="brand" value="americanas" />AMERICANAS</label>			        
			      <label class="btn btn-shop" onclick="disableReembalados()"><form:radiobutton path="brand" value="shoptime"/>SHOPTIME</label>
			      <label class="btn btn-soub" onclick="enableReembalados()" ><form:radiobutton path="brand" value="soubarato" />SOUBARATO</label>
			    </div>
			</div>
		</div>
	</div>
	
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-4 col">
			    <div class="panel-group" id="accordion">
			    	<div class="panel panel-default">
			    		 <div class="panel-heading">
			                <h5 class="panel-title link">
			                    <a data-toggle="collapse"  href="#collapseOne">Busca de Itens</a>
			                </h5>
			            </div>
			            <div id="collapseOne" class="panel-collapse collapse ">
                			<div class="panel-body">
								<label class="control-label">Item Id</label>
								<div class="controls ">
										<input pattern="[0-9. ]*" title="Entre com números, por favor." type="text" name="id" id="id" class="form-control searchinput " onkeypress='validate(event)' placeholder="Pesquisa por Id">
										<span id="searchclear" class="searchclear circle ">x</span>
								</div>								
								<label class="control-label">Sku Id</label>
								<div class="controls ">
									<input pattern="[0-9. ]*" title="Entre com números, por favor." type="text" name="sku" id="sku" class="form-control searchinput" onkeypress='validate(event)' placeholder="Pesquisa por Sku"  />
									<span id="searchclear" class="searchclear circle ">x</span>
								</div>				
							</div>
						</div>							
					</div>
					<div id="mktDiv" class="panel panel-default">	
						<div class="panel-heading">
			                <h5 class="panel-title">
			                    <a data-toggle="collapse"  href="#collapseTwo">Itens Marketplace</a>
			                </h5>
			            </div>
			            <div id="collapseTwo" class="panel-collapse collapse in">
               				 <div class="panel-body">
								<label class="control-label" >Tipo:</label>
								<div class="switch-toggle switch-3 well">
								<input id="b2w" type="radio"  name="type" value="b2w" checked /><label for="b2w" onclick="disabledFunction()">B2W</label>
								<input id="misto" type="radio"  name="type" value="misto"/><label for="misto" onclick="enabledFunction()">MISTO</label>
								<input id="100" type="radio"  name="type" value="100"/><label for="100" onclick="enabledFunction()">100%</label>
								<a class="btn btn-primary"></a>					
								</div>
									
	               				 <div id="numPartnerDiv" class="numPartnerDiv">
	               				 	<label class="control-label">Quantidade de Parceiros</label>
									<div class="controls ">
										<form:input pattern="[0-9.]*" title="Entre com números, por favor." id="numPartner" class="form-control" onkeypress='validate(event)' placeholder="Quantidade de parceiros" path="numPartner"/>
									</div>           
								</div>	  
								<div id="stockItemDiv" class="stockItemDiv">  				 	
									<label class="control-label" >Estoque do Item:</label>				
									<div class="switch-toggle well">
									  <input id="true" type="radio" name="stock" value="true" checked> <label for="true" onclick="">TRUE</label>
									  <input id="false" type="radio" name="stock" value="false" > <label for="false" onclick="">FALSE</label>
									  <a class="btn btn-primary"></a>
									</div> 
								</div>
							</div>
						</div>		
					</div>
					<div class="panel panel-default">
						<div class="panel-heading">
			                <h5 class="panel-title">
			                    <a data-toggle="collapse"  href="#collapseThree">Itens de Moda</a>
			                </h5>
			            </div>
	            		<div id="collapseThree" class="panel-collapse collapse">
	               			 <div class="panel-body">											
								<label class="control-label" >Moda:   </label>
								<div class="switch-toggle well">
								  <input id="fashionTrue" type="radio" name="fashion" value="true" > <label for="fashionTrue" onclick="">TRUE</label>
								  <input id="fashionFalse" type="radio" name="fashion" value="false" checked> <label for="fashionFalse" onclick="">FALSE</label>
								  <a class="btn btn-primary"></a>
								</div> 
								<br/>
							</div>
						</div>		
					</div>					
					<div id="kitDiv" class="panel panel-default">
						<div class="panel-heading">
			                <h5 class="panel-title">
			                    <a data-toggle="collapse"  href="#collapseFour">Kit</a>
			                </h5>
			            </div>
	            		<div id="collapseFour" class="panel-collapse collapse">
	               			 <div class="panel-body">											
								<label class="control-label" >Kit:   </label>
								<div class="switch-toggle well">
								  <input id="kitTrue" type="radio" name="kit" value="true" > <label for="kitTrue" onclick="disableMarketPlace()">TRUE</label>
								  <input id="kitFalse" type="radio" name="kit" value="false" checked> <label for="kitFalse" >FALSE</label>
								  <a class="btn btn-primary"></a>
								</div> 
								<br/>
							</div>
						</div>		
					</div>				
					
					<div id="rewrappedDiv" class="panel panel-default" style="display:none">
						<div class="panel-heading">
			                <h5 class="panel-title">
			                    <a data-toggle="collapse"  href="#collapseFive">Reembalados</a>
			                </h5>
			            </div>
	            		<div id="collapseFive" class="panel-collapse collapse in">
	               			 <div class="panel-body">											
								<div >
								    <input type="checkbox" id="newSoub"  name="wrapped" value="newSoub" class="checkbox"  />
								    <label for="newSoub">NOVO</label>								
								    <input type="checkbox" id="rewrapped"  name="wrapped" value="rewrapped" class="checkbox"  />
								    <label for="rewrapped">REEMBALADO</label>
								</div> 
							</div>
						</div>		
					</div>					

					<hr/>
					<div class="panel-body ">
						<form:input pattern="[0-9.]*" title="Entre com números, por favor." id="numSku" class="smallfield searchinput" placeholder="Nº Skus" onkeypress='validate(event)' path="numSkus"/>
						<span id="searchclear" class="searchclear circle ">x</span>
						<form:input pattern="[0-9.]*" title="Entre com números, por favor." class="smallfield searchinput" placeholder="Start"  onkeypress='validate(event)' path="start"/>
						<span id="searchclear" class="searchclear circle ">x</span>
						<form:input pattern="[0-9.]*" title="Entre com números, por favor." class="smallfield searchinput" placeholder="Rows" onkeypress='validate(event)' path="rows"/>
						<span id="searchclear" class="searchclear circle ">x</span>
					</div>  	
					
					<div class="form-actions"><br/>
						<button id="submit" type="submit" class="btn btn-success" >Submit</button>
						<button type="button" class="btn" onclick="hideCanvasLoader()">Cancel</button>
					</div>						
				</div>	
			</div>
			
			<div class="resultadoBusca col-md-4 col2">		    
			    <div class="panel-group " id="accordion">
				  <div class="panel panel-default itemList">
				    <div class="panel-heading">
				      <h4 class="panel-title">
				       
				        <i class="pull-right glyphicon glyphicon-chevron-down accordion-toggle" data-toggle="collapse"  href="#collapseOne">
				        </i>
				      </h4>
				    </div>
				    
				  </div>	
				</div>
			    <span class="link"></span>
				
			</div>
			<div class="container" id="loading-indicator">
				<!-- Create a div which will be the canvasloader wrapper -->	
				<div id="canvasloader-container" class="wrapper"></div>
			</div>
			 <script type="text/javascript">
						var cl = new CanvasLoader('canvasloader-container');
						cl.setColor('#819e0b'); // default is '#000000'
						cl.setShape('spiral'); // default is 'oval'
						cl.setDiameter(137); // default is 40
						cl.setDensity(32); // default is 40
						cl.setRange(0.9); // default is 1.3
						cl.setFPS(15); // default is 24
						cl.show(); // Hidden by default
							
							// This bit is only for positioning - not necessary
							  var loaderObj = document.getElementById("canvasLoader");
					  		loaderObj.style.position = "absolute";
					  		loaderObj.style["top"] = cl.getDiameter() * -0.5 + "px";
					  		loaderObj.style["left"] = cl.getDiameter() * -0.5 + "px";
			
			
		   </script>
		</div>
	</div>			
</form:form>
</body>
</html>