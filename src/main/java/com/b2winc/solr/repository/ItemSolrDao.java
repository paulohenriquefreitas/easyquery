package com.b2winc.solr.repository;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import br.com.ideais.metasolr.dao.AbstractSolrDao;
import br.com.ideais.metasolr.template.CommonSolrTemplate;

import com.b2w.catalogbackendcommons.index.IndexedItem;

public class ItemSolrDao extends AbstractSolrDao<IndexedItem> {
	
	public static ItemSolrDao getItemSolrDao(String solrUrl) throws SolrServerException, IOException{
		CommonSolrTemplate solrTemplate =  new CommonSolrTemplate();
		HttpSolrServer solrServer = new HttpSolrServer(solrUrl+"/idxItem");
		solrServer.setConnectionTimeout(10000);
		solrServer.ping();
		solrTemplate.setServer(solrServer);
		ItemSolrDao itemSolrDao = new ItemSolrDao();
		itemSolrDao.setTemplate(solrTemplate);
		
		return itemSolrDao;
	}
}
