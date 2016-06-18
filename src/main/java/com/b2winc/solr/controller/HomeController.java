package com.b2winc.solr.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.b2w.catalogbackendcommons.index.IndexedItem;
import com.b2w.catalogbackendcommons.index.IndexedMarketPlaceItem;
import com.b2winc.solr.model.BrandSolr;
import com.b2winc.solr.model.QueryForm;
import com.b2winc.solr.model.QueryFormPartner;
import com.b2winc.solr.repository.ItemSolrDao;
import com.b2winc.solr.repository.MarketPlaceSolrDao;
import com.b2winc.solr.util.Properties;


@Controller
@Scope("request")
public	 class HomeController {
	private static final Log log = LogFactory.getLog(HomeController.class);
	private BrandSolr brandSolr;
	private Integer aux;
	private QueryForm queryForm = new QueryForm();
	private static final String 	SEPARATOR = "\\s\\^\\s";
	private static Integer QUANTITY = 5; 
	private Map<String,List<IndexedItem>> kitGroup ;
	private static final String FASHIONDEP = Properties.getFashionErpDepId();
	private String solrUrl;
	

	@RequestMapping(value="/")
	public ModelAndView test() throws IOException{
			ModelAndView mav = new ModelAndView("home","query",new QueryForm());			
		return mav;
	}

	@RequestMapping(value="/busca", method=RequestMethod.GET)
	public ModelAndView navegue(@ModelAttribute("query") QueryForm queryForm, QueryFormPartner queryFormPartner, Model model , BindingResult result) throws IOException, SolrServerException, JAXBException, ClassNotFoundException{
		setUp(queryForm);
		List<IndexedItem> listIndexedItem = new ArrayList<IndexedItem>();
		ModelAndView mv =  new ModelAndView("resultado");
		solrUrl = Properties.getBrandSolrUrl(queryForm) ;
		if(solrUrl == null){
			model.addAttribute("msg","Ops! Não foi possível encontrar o ip do Solr da marca "+ queryForm.getBrand()+" na configuração.");
			return mv;
		}
		
		long initExecutionTime = System.currentTimeMillis();
		try{
			ItemSolrDao itemSolrDao = ItemSolrDao.getItemSolrDao(solrUrl);
			listIndexedItem = getItem(itemSolrDao,solrUrl,queryForm,queryFormPartner);		
			if(listIndexedItem.isEmpty()){
				model.addAttribute("msg","Nenhum item foi encontrado !");
			}else{
				
					model.addAttribute("idList",listIndexedItem);
					model.addAttribute("itemList",getItensById(itemSolrDao, getIdList(listIndexedItem),listIndexedItem.size()));
					model.addAttribute("link",getLink(queryForm.getBrand()));
					model.addAttribute("linkWebStore",getLinkWebstore(queryForm.getBrand()));
					model.addAttribute("size",listIndexedItem.size());
					model.addAttribute("solrLink",solrUrl+"/idxItem/select?q=itemId%3A");
					model.addAttribute("solrLinkPartner",solrUrl+"/idxMarketPlace/select?q=itemId%3A");
					model.addAttribute("kitGroups", getKitGroup());
					model.addAttribute("partnersMap", getParnerList(listIndexedItem));
		
			}
		}catch(Exception e){
			model.addAttribute("msg","Ocorreu uma exceção ! " + e.getMessage());
			log.error("Ocorreu uma exceção ! " + e.getMessage());
		}
		log.info("Tempo total: "+ (initExecutionTime - System.currentTimeMillis())/-1000 + " segundos.");
		return mv;			
	}
	

	private void setUp(QueryForm queryForm) {
		aux=null;
	    this.queryForm = queryForm;
		this.queryForm.setNumSkus(queryForm.getNumSkus());	
		if(StringUtils.isNotEmpty(queryForm.getRows())){
	    	QUANTITY = Integer.valueOf(queryForm.getRows());
	    }else{
	    	QUANTITY = 5;
	    }
	}


	private Map<String, List<IndexedMarketPlaceItem>> getParnerList(List<IndexedItem> listIndexedItem) throws SolrServerException, IOException {
		MarketPlaceSolrDao marketPlaceSolrDao = MarketPlaceSolrDao.getMarketPlaceItemSolrDao(solrUrl);
		Map<String , List<IndexedMarketPlaceItem>> partnerMap = new HashMap<String, List<IndexedMarketPlaceItem>>() ;
		if(!listIndexedItem.isEmpty()){
			for (IndexedItem indexedItem : listIndexedItem) {
				if(indexedItem.getPartnerList() !=null && indexedItem.getPartnerList().size() > 0){
					List<IndexedMarketPlaceItem> partners = new ArrayList<IndexedMarketPlaceItem>();
					StringBuffer queryString= new StringBuffer();
					queryString.append("itemId:"+indexedItem.getId());
					partners = marketPlaceSolrDao.query(queryString.toString());
						partnerMap.put(indexedItem.getId(), partners);
				}
			}
		}
		return partnerMap;
	}

	private StringBuffer getIdList(List<IndexedItem> listIndexedItem) {
		StringBuffer idList = new StringBuffer();
		idList.append("itemId:(");
		for (IndexedItem indexedItem : listIndexedItem) {
			idList.append(indexedItem.getId()+" ");
		}
		idList.append(")");
		return idList;
	}	
	
	private List<IndexedItem> getItem(ItemSolrDao itemSolrDao,String solrUrl, QueryForm queryForm, QueryFormPartner queryFormPartner) throws Exception{
		StringBuffer queryString= new StringBuffer();
		if(StringUtils.isNotEmpty(System.getProperty("query")))
			queryString.append(System.getProperty("query"));
		String id = queryForm.getId();
		String sku = queryForm.getSku();
		String type = queryForm.getType();
		if(StringUtils.isNotEmpty(id) ) {
			queryString.append("itemId:("+id+")");
			return getItensById(itemSolrDao, queryString,100);			
		}else if(StringUtils.isNotEmpty(sku) ) {
			queryString.append("skuList:("+sku+")");
			return getItensById(itemSolrDao, queryString,100);	
		}else if(queryForm.getFashion().equalsIgnoreCase("true")){
			queryString.append("itemStock:"+queryForm.getStock());	
			queryString.append(" AND erpDepartamentId:("+ FASHIONDEP+")");
			queryString.append(" AND "+getQueryType(queryForm.getType()));
			queryString.append(" AND "+"isKit:"+queryForm.getKit());
			if(this.queryForm.getWrapped() != null ){
				queryString.append(getWrappedStatus());
			}
			return getFashionList(itemSolrDao, queryString,queryForm.getNumSkus(),type);
		}else if(queryForm.getKit().equalsIgnoreCase("true")){
			String stock = queryForm.getStock();						
			queryString.append("itemStock:"+stock);	
			queryString.append(" AND isKit:true");
			if(this.queryForm.getWrapped() != null ){
				queryString.append(getWrappedStatus());
			}
			return getKits(itemSolrDao, queryString,queryForm.getNumSkus());
		}else if (type.equals("b2w")){			
			queryString.append(getQueryType(type));
			queryString.append(" AND itemStock:"+queryForm.getStock());
			queryString.append(" AND -erpDepartamentId:("+ FASHIONDEP+")");
			queryString.append(" AND isKit:"+queryForm.getKit());
			if(this.queryForm.getWrapped() != null ){
				queryString.append(getWrappedStatus());
			}		
			return getItens(itemSolrDao, queryString,QUANTITY);
			
		}else {
			queryString.append(getQueryType(type));	
			queryString.append(" AND -erpDepartamentId:("+ FASHIONDEP+")");			
			queryString.append(" AND partnerList:[1 TO *]"); 
			List<IndexedItem> listIndexedItems = new ArrayList<IndexedItem>();
			String fields = "itemId,isMarketPlace,isExclusiveMarketPlace,skuStock,partnerList,skuList";
			if(this.queryForm.getWrapped() != null ){
				queryString.append(getWrappedStatus());
			}
			int random = 0;
			int row = 500;
			String stock = queryForm.getStock();
			@SuppressWarnings("unused")
			List<IndexedItem> indexedItemListCounter = getSimpleItens(itemSolrDao, queryString, 1,"1","itemId");
			int totalResult = (int) itemSolrDao.getTotalResults();
			random = Integer.valueOf(getRandom(totalResult));
			log.info("TotalResults "+ totalResult);
	    	while(listIndexedItems.size() < QUANTITY){
	    		String increment = getIncrement(random);
	    		log.info("Start "+ increment);
	    		if(totalResult < 500 && Integer.valueOf(increment) < 500){
	    			row=totalResult-Integer.valueOf(increment);
	    		}
				List<IndexedItem> indexedItemList = getSimpleItens(itemSolrDao, queryString, row,increment,fields);
					if(indexedItemList != null &&  indexedItemList.size() > 0 && Integer.valueOf(increment) < totalResult){
						for(IndexedItem indexedItem : indexedItemList){
							if(StringUtils.isEmpty(queryForm.getNumSkus()) && StringUtils.isEmpty(queryForm.getNumPartner()) && indexedItem.getSkuList() != null && indexedItem.getPartnerList() != null){
								listIndexedItems.add(indexedItem);
							}else if(!StringUtils.isEmpty(queryForm.getNumSkus()) && StringUtils.isEmpty(queryForm.getNumPartner()) && 
									indexedItem.getSkuList().size() >= Integer.valueOf(this.queryForm.getNumSkus()) && getSkuStock(indexedItem, stock) ==  Integer.valueOf(this.queryForm.getNumSkus())){
								listIndexedItems.add(indexedItem);
							}else if(StringUtils.isEmpty(queryForm.getNumSkus()) && !StringUtils.isEmpty(queryForm.getNumPartner()) && 
									indexedItem.getPartnerList() != null && indexedItem.getPartnerList().size() == Integer.valueOf(queryForm.getNumPartner())){
								listIndexedItems.add(indexedItem);
							}else if(!StringUtils.isEmpty(queryForm.getNumSkus()) && !StringUtils.isEmpty(queryForm.getNumPartner()) && indexedItem.getSkuList().size() >= Integer.valueOf(this.queryForm.getNumSkus()) && indexedItem.getPartnerList().size() == Integer.valueOf(queryForm.getNumPartner()) &&
									getSkuStock(indexedItem,stock) == Integer.valueOf(queryForm.getNumSkus()) && indexedItem.getPartnerList() != null ){
								listIndexedItems.add(indexedItem);
							}
							
							
								if(listIndexedItems.size() >= QUANTITY)
									return listIndexedItems;
										
							
						}
					}else{
						if(StringUtils.isEmpty(queryForm.getStart()) && totalResult > 1){
							totalResult=random;
							random = Integer.valueOf(getRandom(totalResult));
							aux=(totalResult > 500 ? random-500 : (-500+random));
							log.info("TotalResults "+ totalResult);
						}else{
							return listIndexedItems;
						}
					}
			}
			
			return listIndexedItems;
		}
		
	}
	
	private String getWrappedStatus() {
		String [] wrapped = StringUtils.split(this.queryForm.getWrapped(),",");
		if(wrapped.length == 2){
			return " AND itemStockQuantityNew:[1 TO *] AND itemStockQuantityRewrapped:[1 TO *]";
		}else if (wrapped[0].equalsIgnoreCase("newSoub")){
			return " AND itemStockQuantityNew:[1 TO *] AND itemStockQuantityRewrapped:[0 TO 0]";
		}else if (wrapped[0].equalsIgnoreCase("rewrapped")){
			return " AND itemStockQuantityNew:[0 TO 0] AND itemStockQuantityRewrapped:[1 TO *]";
		}
		return "";
	}

	private List<IndexedItem> getItens(ItemSolrDao itemSolrDao,	StringBuffer queryString,Integer quantity) throws NumberFormatException, Exception {
		List<IndexedItem> listIndexedItems = new ArrayList<IndexedItem>();
		if(StringUtils.isNotEmpty(this.queryForm.getNumSkus())){
			listIndexedItems = getItensB2wBySku(itemSolrDao,queryString,Integer.valueOf(this.queryForm.getNumSkus()));
		}else{
			listIndexedItems = getItensB2wBySku(itemSolrDao,queryString,1);			
			
		}
		return listIndexedItems;
	}
	
	private List<IndexedItem> getItensB2wBySku(ItemSolrDao itemSolrDao, StringBuffer queryString,Integer skuQuantity) throws Exception {
		String fields = "itemId,isKit,isMarketPlace,itemStock,skuList,skuStock,itemStockQuantityNew,itemStockQuantityRewrapped";
		List<IndexedItem> listIndexedItemsBysku = new ArrayList<IndexedItem>();
		String stock = queryForm.getStock();
		int random = 0;
		int row = 500;
		@SuppressWarnings("unused")
		List<IndexedItem> indexedItemListCounter = getSimpleItens(itemSolrDao, queryString, 1,"1","itemId");
		int totalResult = (int) itemSolrDao.getTotalResults();
		random = Integer.valueOf(getRandom(totalResult));
		log.info("TotalResults "+ totalResult);	
		while(listIndexedItemsBysku.size() < QUANTITY){
			String increment = getIncrement(random);
    		log.info("Start "+ increment);
    		if(totalResult < 500 && Integer.valueOf(increment) < 500){
    			row=totalResult-Integer.valueOf(increment);
    		}
    		
			List<IndexedItem> indexedItemList = getSimpleItens(itemSolrDao, queryString, row,increment,fields);
			try{
				if(indexedItemList != null &&  indexedItemList.size() > 0 && Integer.valueOf(increment) < totalResult){				
					for(IndexedItem indexedItem : indexedItemList){
						if(indexedItem.getSkuList().size() >= skuQuantity && getSkuStock(indexedItem,stock) == skuQuantity){
							listIndexedItemsBysku.add(indexedItem);
							if(listIndexedItemsBysku.size() == QUANTITY){
								return listIndexedItemsBysku;
							}
						}
						
					}
				}else{	
					if(StringUtils.isEmpty(queryForm.getStart()) && totalResult > 1 ){
						totalResult=random;
						random = Integer.valueOf(getRandom(totalResult));
						aux=(totalResult > 500 ? random-500 : (-500+random));
						log.info("TotalResults "+ totalResult);
					}else{
						return listIndexedItemsBysku;
					}
				}
			}catch(Exception e){
				log.error("Ocorreu uma exceção. " + e.getMessage());
				return listIndexedItemsBysku;
			}
		}
		return listIndexedItemsBysku;
	}


	private Integer getSkuStock(IndexedItem indexedItem,String stock) throws SolrServerException, IOException {
		long initExecutionTime = System.currentTimeMillis();
		List<String> skuList = new ArrayList<String>();
		if(indexedItem.getSkuList() != null && indexedItem.getSkuList().size() > 0){
			for(String sku : indexedItem.getSkuList()){
				if(isSkuSoldByB2w(indexedItem,stock,sku)){
					skuList.add(sku);					
				}				
			}
			if(indexedItem.getSkuList().size() != skuList.size()){
				getPartnerSkuList(indexedItem, stock, skuList);
				System.out.println("Tempo " +( initExecutionTime - System.currentTimeMillis())+" milisegundos.");
			}
		}
		
		return skuList.size();
	}
	private boolean isSkuSoldByB2w(IndexedItem indexedItem, String stock,String skuId) {
		for(String sku : indexedItem.getSkuStock()){
			String skuSplitted [] = sku.split(SEPARATOR);
			if(skuSplitted[0].equals(skuId) && skuSplitted[1].equalsIgnoreCase(stock)){
				return true;
			}
		}
		return false;
	}

	private void getPartnerSkuList(IndexedItem indexedItem, String stock,List<String> skuList) throws SolrServerException, IOException {
		List<IndexedMarketPlaceItem> partners = getPartners(indexedItem.getId());
		if(partners != null && partners.size() > 0){
			for (IndexedMarketPlaceItem partner : partners) {
				if(partner.getPartnerStatus().equals(Boolean.valueOf(stock))){
					for(String sku : partner.getSkuList()){
						if(isSkuSoldByPartner(partner,sku,stock) && !skuList.contains(sku)){
							skuList.add(sku);
						}
					}
				}
			}
		}
	}


	private boolean isSkuSoldByPartner(IndexedMarketPlaceItem partner,String skuId, String stock) throws MalformedURLException {		
		if(skuStatus(partner,skuId,stock) && skuStock(partner,skuId,stock)){
			return true;
		}	
		return false;
	}

	private boolean skuStock(IndexedMarketPlaceItem partner, String skuId, String stock) {
		for(String sku : partner.getSkuStock()){
			String skuSplitted [] = sku.split(SEPARATOR);
			if(skuSplitted[0].equalsIgnoreCase(skuId) && skuSplitted[1].equalsIgnoreCase(stock) ){
				return true;
			}
		}
		return false;
	}

	private boolean skuStatus(IndexedMarketPlaceItem partner, String skuId, String stock) {
		for(String sku : partner.getSkuStatus()){
			String skuSplitted [] = sku.split(SEPARATOR);
			if(skuSplitted[0].equalsIgnoreCase(skuId) && skuSplitted[1].equalsIgnoreCase(stock) ){
				return true;
			}
		}
		return false;
	}

	private List<IndexedMarketPlaceItem> getPartners(String itemId) throws SolrServerException, IOException {
		MarketPlaceSolrDao marketPlaceSolrDao = MarketPlaceSolrDao.getMarketPlaceItemSolrDao(solrUrl);
		StringBuffer queryString= new StringBuffer();
		String fields = "skuStatus,skuStock,partnerStatus,skuList";
		queryString.append("itemId:"+itemId);
		SolrQuery solrQuery = new SolrQuery(queryString.toString());
		solrQuery.addField(fields);		
		List<IndexedMarketPlaceItem> partners = marketPlaceSolrDao.query(solrQuery);
		return partners;
	}

	private List<IndexedItem> getFashionList(ItemSolrDao itemSolrDao,StringBuffer queryString, String numSkus, String type) throws Exception {
		SolrQuery query = new SolrQuery(queryString.toString());
		query.add("rows",String.valueOf(QUANTITY));
		String fields = "itemId,itemName,itemStock,skuStock,skuList,erpDepartamentId,isMarketPlace,partnerList,kitItemList";
		query.addField(fields);
		List<IndexedItem> listIndexedItemsFashion = new ArrayList<IndexedItem>();
		String stock = queryForm.getStock();
		int random = 0;
		int row = 500;
		@SuppressWarnings("unused")
		List<IndexedItem> indexedItemListCounter = getSimpleItens(itemSolrDao, queryString, 1,"1","itemId");
		int totalResult = (int) itemSolrDao.getTotalResults();
		random = Integer.valueOf(getRandom(totalResult));
		log.info("TotalResults "+ totalResult);	
		kitGroup = new HashMap<String, List<IndexedItem>>();		
		while(listIndexedItemsFashion.size() < QUANTITY){
			String increment = getIncrement(random);
    		log.info("Start "+ increment);
    		if(totalResult < 500 && Integer.valueOf(increment) < 500){
    			row=totalResult-Integer.valueOf(increment);
    		}
			List<IndexedItem> listIndexedItems = getSimpleItens(itemSolrDao, queryString, row,increment,fields);
			try{
				if(listIndexedItems != null &&  listIndexedItems.size() > 0  && Integer.valueOf(increment) < totalResult){	
					for(IndexedItem indexedItem : listIndexedItems){
						if(type.equals("b2w") && StringUtils.isEmpty(numSkus)){
							listIndexedItemsFashion.add(indexedItem);
						}else if(type.equals("b2w") && !StringUtils.isEmpty(numSkus) && indexedItem.getPartnerList().size() == Integer.valueOf(numSkus)){
							listIndexedItemsFashion.add(indexedItem);
						}else if(StringUtils.isEmpty(numSkus) && StringUtils.isEmpty(queryForm.getNumPartner()) && indexedItem.getPartnerList() != null){
							listIndexedItemsFashion.add(indexedItem);
						}else if(!StringUtils.isEmpty(numSkus) && StringUtils.isEmpty(queryForm.getNumPartner()) &&  indexedItem.getSkuList() != null 
								&& indexedItem.getSkuList().size() == Integer.valueOf(numSkus)){
							listIndexedItemsFashion.add(indexedItem);
						}else if(StringUtils.isEmpty(numSkus) && !StringUtils.isEmpty(queryForm.getNumPartner()) && 
								indexedItem.getPartnerList() != null && indexedItem.getPartnerList().size() == Integer.valueOf(queryForm.getNumPartner())){
							listIndexedItemsFashion.add(indexedItem);
						}else if(!StringUtils.isEmpty(numSkus) && indexedItem.getSkuList().size() == Integer.valueOf(numSkus) && 
								!StringUtils.isEmpty(queryForm.getNumPartner()) && indexedItem.getPartnerList() != null && indexedItem.getPartnerList().size() == Integer.valueOf(queryForm.getNumPartner())){
							listIndexedItemsFashion.add(indexedItem);
						}
											
						if(listIndexedItemsFashion.size() == QUANTITY){
							kitGroup = getKitGroupList(itemSolrDao,listIndexedItemsFashion);
							return listIndexedItemsFashion;
						}
					}
				}else{
					if(StringUtils.isEmpty(queryForm.getStart()) && totalResult > 1){
						totalResult=random;
						random = Integer.valueOf(getRandom(totalResult));
						aux=(totalResult > 500 ? random-500 : (-500+random));
						log.info("TotalResults "+ totalResult);
					}else{
						kitGroup = getKitGroupList(itemSolrDao,listIndexedItemsFashion);
						return listIndexedItemsFashion;
					}
					
				}
			}catch(Exception e){
				log.error("Ocorreu uma exceção. " + e.getMessage());
				kitGroup = getKitGroupList(itemSolrDao,listIndexedItemsFashion);
				return listIndexedItemsFashion;
			}
		}
			
		return null;
	}	

	private String getRandom(int brandStart) {
		if(StringUtils.isEmpty(queryForm.getStart())){
			return String.valueOf(1 + (int)(Math.random() * brandStart));
		}else{
			return queryForm.getStart();
		}
	}
	

	private List<IndexedItem> getKits(ItemSolrDao itemSolrDao,
			StringBuffer queryString,String skuQuantity) throws Exception {
		Integer skuQty = StringUtils.isEmpty(skuQuantity) ? 1 : Integer.valueOf(skuQuantity);
		SolrQuery query = new SolrQuery(queryString.toString());
		query.add("rows",String.valueOf(QUANTITY));
		List<IndexedItem> listIndexedItemsKit = new ArrayList<IndexedItem>();
		String fields = "itemId,itemName,isKit,skuList,kitItemList";
		int random = 0;
		int row = 500;
		@SuppressWarnings("unused")
		List<IndexedItem> indexedItemListCounter = getSimpleItens(itemSolrDao, queryString, 1,"1","itemId");
		int totalResult = (int) itemSolrDao.getTotalResults();
		random = Integer.valueOf(getRandom(totalResult));
		log.info("TotalResults "+ totalResult);	
		while(listIndexedItemsKit.size() < QUANTITY){
			String increment = getIncrement(random);
    		log.info("Start "+ increment);
    		if(totalResult < 500 && Integer.valueOf(increment) < 500){
    			row=totalResult-Integer.valueOf(increment);
    		}
			List<IndexedItem> listIndexedItems = getSimpleItens(itemSolrDao, queryString, row,increment,fields);
			try{
				if(listIndexedItems != null &&  listIndexedItems.size() > 0 && Integer.valueOf(increment) < totalResult){
					for(IndexedItem indexedItem : listIndexedItems){
						if(indexedItem.getSkuList().size() == skuQty){
							listIndexedItemsKit.add(indexedItem);
						}
						if(listIndexedItemsKit.size() == QUANTITY){
							kitGroup = getKitGroupList(itemSolrDao, listIndexedItemsKit);
							return listIndexedItemsKit;
						}	
					}
				}else{
					if(StringUtils.isEmpty(queryForm.getStart()) && totalResult > 1){
						totalResult=random;
						random = Integer.valueOf(getRandom(totalResult));
						aux=(totalResult > 500 ? random-500 : (-500+random));
						log.info("TotalResults "+ totalResult);
					}else{
						return listIndexedItemsKit;
					}
				}
			}catch(Exception e){
				log.error("Ocorreu uma exceção. " + e.getMessage());
				return listIndexedItemsKit;
			}
		}
		return null;
	}

	
	private Map<String, List<IndexedItem>> getKitGroupList(ItemSolrDao itemSolrDao, List<IndexedItem> listIndexedItemsKit) {
		kitGroup = new HashMap<String, List<IndexedItem>>();
		for (IndexedItem indexedItem : listIndexedItemsKit) {
			List<String> memberKits = new ArrayList<String>();
			if(indexedItem.getKitItemList() != null){
				for(String kitItem : indexedItem.getKitItemList()){
					String kitInfos[] = kitItem.split("\\^");
					if(!kitInfos[2].trim().isEmpty() && !memberKits.contains(kitInfos[2].trim())){
						memberKits.add(kitInfos[2].trim());						
					}
				}
				kitGroup.put(indexedItem.getId(), getItensKitChildrenById(itemSolrDao, getKitChildrenList(memberKits), memberKits.size()));	
			}
		}		
		return kitGroup;
	}


	private StringBuffer getKitChildrenList(List<String> memberKits) {
		StringBuffer idList = new StringBuffer();
		idList.append("itemId:(");
		for (String itemId : memberKits) {
			idList.append(itemId+" ");
		}
		idList.append(")");
		return idList;
	}

	private List<IndexedItem> getItensById(ItemSolrDao itemSolrDao,
			StringBuffer queryString, int rows) throws Exception {
		SolrQuery query = new SolrQuery(queryString.toString());	
		query.add("rows",String.valueOf(rows));
		List<IndexedItem> listIndexedItems = new ArrayList<IndexedItem>();
		try{
			listIndexedItems = itemSolrDao.query(query);
		}catch (Exception e) {
			log.error("Ocorreu uma exceção. " + e.getMessage());
			throw new Exception("Tente um número menor na busca.");
		}	
		return listIndexedItems;
	}	
	
	private List<IndexedItem> getItensKitChildrenById(ItemSolrDao itemSolrDao,
			StringBuffer queryString, int rows) {
		
		SolrQuery query = new SolrQuery(queryString.toString());
		query.addField("itemId,soldSeparatelly");
		query.add("rows",String.valueOf(rows));
		List<IndexedItem> listIndexedItems = new ArrayList<IndexedItem>();
		try{
			listIndexedItems = itemSolrDao.query(query);
		}catch (Exception e) {
			log.error("Ocorreu uma exceção. " + e.getMessage());
			return listIndexedItems;
		}	
		return listIndexedItems;
	}	
	

	private String getIncrement(Integer start2) {
		if(aux == null){
			aux = start2;
			return String.valueOf(start2);
		}else{
			aux=aux+500;
			
			return String.valueOf(aux);	
		}
	}

	private List<IndexedItem> getSimpleItens(ItemSolrDao itemSolrDao,StringBuffer queryString, int quantity, String start2, String fields) throws Exception {
		SolrQuery query = new SolrQuery(queryString.toString());
		List<IndexedItem> indexedItemList = null;
		query.add("rows",String.valueOf(quantity));
		query.add("start",start2);
		query.addFilterQuery("+(+isFreeBee:false -soldSeparatelly:false -item_property_EXCLUSIVE_B2B:true)");
		query.addField(fields);
		query.setTimeAllowed(5000);
		try{
			indexedItemList = itemSolrDao.query(query);			
		}catch(Exception e){
			log.error("Ocorreu uma exceção. " + e.getMessage());
			throw new Exception(e.getMessage().toString());
		}
		return  indexedItemList;
		
		
	}

	private String getQueryType(String type) {
		if(type!=null){
			if(type.equalsIgnoreCase("100"))
				return("isExclusiveMarketPlace:true");
			else if(type.equalsIgnoreCase("misto") && queryForm.getStock().equals("true"))
				return("isMarketPlace:true AND isExclusiveMarketPlace:false");
			else if(type.equalsIgnoreCase("misto") && queryForm.getStock().equals("false"))
				return("isMarketPlace:true AND isExclusiveMarketPlace:true");
			else
				return("isMarketPlace:false");
		}
		return "";
	}
	
	private String getLink(String marca) {
		return "http://www."+marca+".com.br/produto/";
	}
	
	private String getLinkWebstore(String marca) {
		return "http://www."+marca+".com.br/lojista/";
	}	
	
	public BrandSolr getBrandSolr() {
		return brandSolr;
	}

	public void setBrandSolr(BrandSolr brandSolr) {
		this.brandSolr = brandSolr;
	}
	
	
	public QueryForm getQueryForm() {
		return queryForm;
	}

	public void setQueryForm(QueryForm queryForm) {
		this.queryForm = queryForm;
	}

	public Map<String, List<IndexedItem>> getKitGroup() {
		return kitGroup;
	}

	public void setKitGroup(Map<String, List<IndexedItem>> kitGroup) {
		this.kitGroup = kitGroup;
	}
	
}