package com.b2winc.solr.util;

import org.apache.commons.lang3.StringUtils;

import com.ctc.wstx.util.StringUtil;

public final class Validation {
	
	public static boolean isNumber(String id) {
		
		return StringUtils.isNumeric(id);
	}


}
