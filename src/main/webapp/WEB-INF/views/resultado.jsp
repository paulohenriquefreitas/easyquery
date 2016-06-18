<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">

.cornerLeft {
   float:left;
   margin-left: 15px;
}		
.cornerRight {
   float:right;
   margin-top: -22px;
}

.spt-tag-novo-esq {
  background-image: url(/resources/image/auto-sprite.png);
  background-position: -301px -70px;
  width: 85px;
  height: 19px;
}	

.spt-tag-reembalado-esq {
  background-image: url(/resources/image/auto-sprite.png);
  background-position: -1px -98px;
  width: 85px;
  height: 19px;
}	
</style>	
</head>
<body>
	 <c:if test="${fn:length(itemList) gt 0}">
		<div class="col-md-12 ">
			<div class="panel panel-default">
			   <div class="panel-heading">
				<c:forEach var="item" items="${idList}">
				  <a href="${link}${item.id}" target="_blank">${item.id}</a>
				</c:forEach>
				</div>
			</div>	
				<div class="panel-group" id="accordion"> 
			        <c:forEach var="item" items="${itemList}"> 
				         <div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a href="${link}${item.id}" target="_blank">${item.id}</a> - 
				                    <a data-toggle="collapse" href="#${item.id}">${item.itemName}</a>
				                </h4>
				            </div>
				            <div id="${item.id}" class="panel-collapse collapse">
				            	  <div class="panel-group"> 
					                <div class="panel-body">
						                <ul class="list-unstyled">
						                  <li><span class="text-danger">ItemId :</span> ${item.itemId}</li>				                  
										  <li><span class="text-danger">ItemStock :</span> ${item.inStock}</li>
										  <li><span class="text-danger">ItemStockQuantity :</span> ${item.itemStockQuantity}</li>
										  <li><span class="text-danger">ItemStockQuantityNew :</span> ${item.itemStockQuantityNew}</li>
										  <li><span class="text-danger">ItemStockQuantityRewrapped :</span> ${item.itemStockQuantityRewrapped}</li>
										  <c:if test="${item.defaultPrice ne null}">
										  <li><span class="text-danger">DefaultPrice :</span> ${item.defaultPrice}</li>
										  </c:if>
										  <li><span class="text-danger">SalesPrice :</span> ${item.salesPrice}</li>
										  <li><span class="text-danger">AdminTagList :</span> ${item.adminTagList}</li>
										  <li><span class="text-danger">SiteStructure :</span> ${item.siteStructure}</li>
										  <c:if test="${item.isMarketPlace}">
										  <li><span class="text-danger">PartnerList :</span> ${item.partnerList}</li>
										  </c:if>
										  <li><span class="text-danger">FlatGroupId :</span> ${item.flatGroupsIds}</li>
										  <li><span class="text-danger">SkuStock :</span> ${item.skuStock}</li>
										  <li><span class="text-danger">SkuStockQuantity :</span> ${item.skuStockQuantity}</li>
										  
										  <li><span class="text-danger">SkuList :</span> ${item.skuList}</li>
										  <li><span class="text-danger">SkuDiffs :</span> ${item.skuDiffs}</li>
										  <li><span class="text-danger">ImageSkuUrlList :</span> ${item.imageSkuUrlList}</li>
										  <c:if test="${item.isKit}">
										  <li><span class="text-danger">IsKit :</span> ${item.isKit	}</li>
										  <li><span class="text-danger">SkuKitList :</span> ${item.skuKitList}</li>
										  <li><span class="text-danger">Filhos do Kit :</span> ${item.kitItemList}</li>
										  </c:if>
										  <li><span class="text-danger">IsMarketPlace :</span> ${item.isMarketPlace}</li>
										  <li><span class="text-danger">IsExclusiveMarketPlace :</span> ${item.isExclusiveMarketPlace}</li>
										   <c:if test="${item.numReviews gt 0}">
										  <li><span class="text-danger">AverageOverallRating :</span> ${item.averageOverallRating}</li>
										  <li><span class="text-danger">NumReviews :</span> ${item.numReviews}</li>
										  </c:if>									       	
										</ul>	
										<div class="cornerRight">
						                	<span class="text-primary"> Informações do Item em  
							                    <a href="${solrLink}${item.id}&wt=json&indent=true" target="_blank">json</a> ou em	
												<a href="${solrLink}${item.id}&wt=xml&indent=true" target="_blank"> xml</a>
											</span>	
										</div>								
										<c:if test="${item.isKit}">
											<hr>
											<div class="cornerLeft">
												<span class="text-danger">Filhos:</span>
													<table class="table table-bordered table-hover">
													    <thead>
													        <tr>
													            <th data-field="link">link</th>
													            <th data-field="xml">json</th>
													            <th data-field="json">xml</th>
													        </tr>
													    </thead>
														<tbody>
															<c:forEach items="${kitGroups}" var="itemKitGroup">	
																<c:if test="${item.id eq itemKitGroup.key}">
																	<c:forEach items="${itemKitGroup.value}" var="itemKit">	
																		<tr>
																		<c:choose>
																			 <c:when test="${itemKit.soldSeparatelly}">
																					<td><a href="${link}${itemKit.id}" target="_blank">${itemKit.id}</a></td>
																			 </c:when>
																			 <c:otherwise>
																			 	<td>${itemKit.id}</td>	
																			 </c:otherwise>
																			</c:choose>
																			<td><a href="${solrLink}${itemKit.id}&wt=json&indent=true" target="_blank">${itemKit.id}</a></td>
																			<td><a href="${solrLink}${itemKit.id}&wt=xml&indent=true" target="_blank">${itemKit.id}</a></td>
																		</tr>
																	</c:forEach>	
																</c:if>
															</c:forEach>
														</tbody>				                	
													</table>	
											</div>
										</c:if>										 
										<c:if test="${fn:length(partnersMap) gt 0}">
											 <hr/>
					               			 <div>
						                		 <ul class="list-unstyled">
							                		<c:forEach items="${partnersMap}" var="partners">
								                	 <c:forEach items="${partners.value}" var="partner">
								                	 	<c:if test="${item.id eq partner.itemId}">
								                	 		<li><span class="text-primary">Id : </span>${partner.id}</li> 
								                	 		<li><span class="text-primary">PartnerId:</span>${partner.partnerId}</li> 				
								                			<li><span class="text-primary">Name : </span ><a class="text-danger" href="${linkWebStore}${partner.partnerName}/${partner.id}" target="_blank">${partner.partnerName}</a></li> 	
								                			<li><span class="text-primary">PartnerStatus : </span>${partner.partnerStatus}</li> 					                			
								                			<li><span class="text-primary">SkuStock : </span>${partner.skuStock}</li> 
								                			<li><span class="text-primary">SkuStatus : </span>${partner.skuStatus}</li> 
								                			<li><span class="text-primary">SkuDefaultPrice : </span>${partner.skuDefaultPrice}</li>
								                			<li><span class="text-primary">SkuSalesPrice : </span>${partner.skuSalesPrice}</li>
								                			<li><span class="text-primary">SkuStockQuantity : </span>${partner.skuStockQuantity}</li>
															<br/>	
								                		</c:if>	
								                	</c:forEach>	 
								                </c:forEach>
								              </ul>
						             		</div>
						         	   <div class="cornerRight">
						                	<span class="text-primary"> Informações dos parceiros em  
							                    <a href="${solrLinkPartner}${item.id}&wt=json&indent=true" target="_blank">json</a> ou em	
												<a href="${solrLinkPartner}${item.id}&wt=xml&indent=true" target="_blank"> xml</a>
											</span>	
										</div>    
						         		</c:if>  
					                </div>
				                </div>
				              </div>
				        </div> 
			       </c:forEach> 
			    </div>			
		</div>
	 </c:if>
	 
	
	 <c:if test="${fn:length(itemList) eq 0}">	 
	 	<label class="control-label" >${msg}</label><br/>
	 </c:if>
</body>
</html>