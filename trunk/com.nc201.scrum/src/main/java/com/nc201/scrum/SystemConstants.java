package com.nc201.scrum;

import java.util.HashMap;
import java.util.Map;

import com.nc201.common.util.ObjectUtil;

public class SystemConstants {
	public static String WEB_ROOT;
	public static String PROJECT_ROOT;
	
	public static String ok(){
		return result("ok");
	}
	public static String result(String result){
		Map<String,String> map=new HashMap<String,String>();
		map.put("result",result);
		return ObjectUtil.toJson(map);
	}
	
	public static String packStrArray(String[] args,String key){
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			if(!"".equals(sb.toString())){
				sb.append(",");
			}
			sb.append("{\""+key+"\":\""+args[i]+"\"}");
		}
		return "["+sb.toString()+"]";
	}
}
