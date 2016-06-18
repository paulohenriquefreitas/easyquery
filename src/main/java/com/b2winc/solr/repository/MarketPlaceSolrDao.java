package com.b2winc.solr.repository;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.b2w.catalogbackendcommons.index.IndexedMarketPlaceItem;

import br.com.ideais.metasolr.dao.AbstractSolrDao;
import br.com.ideais.metasolr.template.CommonSolrTemplate;

public class MarketPlaceSolrDao extends AbstractSolrDao<IndexedMarketPlaceItem> {
	
	public static MarketPlaceSolrDao getMarketPlaceItemSolrDao(String solrUrl) throws SolrServerException, IOException{
		CommonSolrTemplate solrTemplate =  new CommonSolrTemplate();
		HttpSolrServer solrServer = new HttpSolrServer(solrUrl+"/idxMarketPlace");
		solrServer.setConnectionTimeout(10000);
		solrServer.ping();
		solrTemplate.setServer(solrServer);
		MarketPlaceSolrDao itemSolrDao = new MarketPlaceSolrDao();
		itemSolrDao.setTemplate(solrTemplate);
		
		return itemSolrDao;
	}	

}
