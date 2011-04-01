package com.nc201.common.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 对象工具类
 * @author zhuzhf
 *
 */
public class ObjectUtil {
	
	/**
	 * json 串转Map
	 * @param s
	 * @return	 
	 */
	@SuppressWarnings("rawtypes")
	public static Map json2Map(String s)  {
		Map<String,Object> map=new HashMap<String,Object>();
		JsonElement jsonElement=new JsonParser().parse(s);

		Set<Entry<String, JsonElement>>  set=jsonElement.getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> entry : set) {
			if(entry.getValue().isJsonPrimitive()){
				Object value=entry.getValue().isJsonNull()?null:entry.getValue().getAsString();
				map.put(entry.getKey(),value);
			}else if(entry.getValue().isJsonArray()){
				JsonArray jsonArray=entry.getValue().getAsJsonArray();
				List<Map> subs=new ArrayList<Map>(); 
				for(Iterator<JsonElement> itor=jsonArray.iterator();itor.hasNext();){
					JsonElement subJson=itor.next();
					subs.add(json2Map(subJson.toString()));				
				}
				map.put(entry.getKey(),subs);
			}else if(entry.getValue().isJsonObject()){
				map.put(entry.getKey(), json2Map(entry.getValue().toString()));
			}else{
				Object value=entry.getValue().isJsonNull()?null:entry.getValue();
				map.put(entry.getKey(),value);
			}
		}
		return map;
	}
	/**
	 * json 数组转为List
	 * @param s
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> json2List(String s)  {
		//Map<String,Object> map=new HashMap<String,Object>();
		JsonElement jsonElement=new JsonParser().parse(s);
		JsonArray jsonArray=jsonElement.getAsJsonArray();
		List<Map> subs=new ArrayList<Map>(); 
		for(Iterator<JsonElement> itor=jsonArray.iterator();itor.hasNext();){
			JsonElement subJson=itor.next();
			subs.add(json2Map(subJson.toString()));				
		}
		return subs;
	}	
	/**
	 * Map 转json串
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String map2Json(Map map){		
		return new Gson().toJson(map);
	}	
	/**
	 * 判断左边json是否等于右边json
	 * @param rightLeftJosn
	 * @param leftJson
	 * @return
	 */
	public static boolean isEquals(String leftJson,String rightJson){
		JsonElement leftElement=new JsonParser().parse(leftJson);
		JsonElement rightElement=new JsonParser().parse(rightJson);
		if(leftElement.isJsonObject()&&rightElement.isJsonObject()){
			return isEquals(json2Map(leftJson),json2Map(rightJson));
		}else if(leftElement.isJsonArray()&&rightElement.isJsonArray()){
			JsonArray leftArray=leftElement.getAsJsonArray();
			JsonArray rightArray=rightElement.getAsJsonArray();
			if(leftArray.size()!=rightArray.size()){
				return false;
			}
			for (int i = 0; i < leftArray.size(); i++) {
				if(!isEquals(json2Map(leftArray.get(i).toString()),json2Map(rightArray.get(i).toString()))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * 判断左边Map是否等于右边Map
	 * @param leftMap
	 * @param rightMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEquals(Map leftMap,Map rightMap) {
		if(leftMap!=null&&rightMap!=null&&rightMap.size()!=leftMap.size()){
			return false;
		}
		
		for(Object m: rightMap.keySet()){
			Object value=rightMap.get(m);
			
			if(!leftMap.containsKey(m)){
				return false;
			}
			
			if(value==null){
				if(leftMap.get(m)!=null){
					return false;
				}
			}else{
				if(value instanceof List){
					Object leftObj=leftMap.get(m);
					if(!(leftObj instanceof List)){
						return false;
					}else{
						List rightList=(List)value;
						List leftList=(List)leftObj;
						if(rightList.size()!=leftList.size()){
							return false;
						}
						for (int i = 0; i < rightList.size(); i++) {
							if(!isEquals((Map)leftList.get(i),(Map)rightList.get(i))){
								return false;
							}
						}
					}
				}else if(value instanceof Map){
					Object leftObj=leftMap.get(m);
					if(!(leftObj instanceof Map)){
						return false;
					}else{
						if(!isEquals((Map)leftObj,(Map)value)){
							return false;
						}
					}
				}else{
					if(leftMap.get(m)==null||!value.toString().equals(leftMap.get(m).toString())){
						return false;
					}
					
				}
				
				
			}

		}  

		return true;
	}
	
	
	/**
	 * 判断左边json是否包含右边json
	 * @param rightLeftJosn
	 * @param leftJson
	 * @return
	 */
	public static boolean isContain(String leftJson,String rightJson){
		return isContain(json2Map(leftJson),json2Map(rightJson));
	}
	
	/**
	 * 判断左边Map是否包含右边Map
	 * @param leftMap
	 * @param rightMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isContain(Map leftMap,Map rightMap) {
		if(leftMap!=null&&rightMap!=null&&rightMap.size()>leftMap.size()){
			return false;
		}
		
		for(Object m: rightMap.keySet()){
			Object value=rightMap.get(m);
			
			if(!leftMap.containsKey(m)){
				return false;
			}
			
			if(value==null){
				if(leftMap.get(m)!=null){
					return false;
				}
			}else{
				if(value instanceof List){
					Object leftObj=leftMap.get(m);
					if(!(leftObj instanceof List)){
						return false;
					}else{
						List rightList=(List)value;
						List leftList=(List)leftObj;
						if(rightList.size()!=leftList.size()){
							return false;
						}
						for (int i = 0; i < rightList.size(); i++) {
							if(!isContain((Map)leftList.get(i),(Map)rightList.get(i))){
								return false;
							}
						}
					}
				}else if(value instanceof Map){
					Object leftObj=leftMap.get(m);
					if(!(leftObj instanceof Map)){
						return false;
					}else{
						if(!isContain((Map)leftObj,(Map)value)){
							return false;
						}
					}
				}else{
					if(leftMap.get(m)==null||!value.toString().equals(leftMap.get(m).toString())){
						return false;
					}
				}
				
				
			}

		}  

		return true;
	}
	
	/**
	 * 将json串转成指定class的实例
	 * @param <T>
	 * @param json
	 * @param aClass
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T json2Obj(String json, Class aClass){
		Gson gson = new Gson();		
		Object obj = gson.fromJson(json, aClass);
		return (T) obj;
	}
	
	/**
	 * obj 2 json串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		return new Gson().toJson(obj);
	}
	/**
	 * 从map中去除指定的key值
	 * @param datas
	 * @param keys
	  
	 */
	@SuppressWarnings("unchecked")
	public static String escapeKey(String jsonDatas,String...keys ) {
		return map2Json(escapeKey(json2Map(jsonDatas),keys));
	}
	
	/**
	 * 从map中去除指定的key值
	 * @param datas
	 * @param keys
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> escapeKey(Map<String,Object> datas,String...keys ){
		
		for(Object m: datas.keySet()){
			Object value=datas.get(m);
			if(value instanceof List){
				List<Map<String,Object>> subDatas=(List<Map<String,Object>>)value;
				for (Map<String, Object> aSub : subDatas) {
					escapeKey(aSub,keys);
				}
			}else if(value instanceof Map){
				escapeKey((Map<String, Object>)value,keys);
			}
		}
		for (int i = 0; i < keys.length; i++) {
			datas.remove(keys[i]);
		}
		return datas;
	}
	/**
	 * 计算arg1在arg2中没有的字符串
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static String[] arrayDiff(String[] arg1 ,String[] arg2){
		List<String> retDatas=new ArrayList<String>();
		for (int i = 0; i < arg1.length; i++) {
			boolean inRight=false;
			for (int j = 0; j < arg2.length; j++) {
				if(arg1[i]!=null&&arg1[i].equals(arg2[j])){
					inRight=true;
					break;
				}
			}
			if(!inRight){
				retDatas.add(arg1[i]);
			}
		}
		return retDatas.toArray(new String[retDatas.size()]);
	}
	
	/**
	 * 数组合并
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static Object[] mergeArray(Object[] obj1,Object... obj2){
		Object[] resultObj=new String[obj1.length+obj2.length];
		System.arraycopy(obj1,0,resultObj,0,obj1.length); 
		System.arraycopy(obj2,0,resultObj,obj1.length,obj2.length); 
		return resultObj;
	}
	
	/**
	 * 判断json数组是否相等
	 * @param json1
	 * @param json2	  
	 */
	public static boolean isEquals(String[] json1,String... json2) {
		if(json1.length!=json2.length){
			return false;
		}
		for (int i = 0; i < json1.length; i++) {
			boolean isEquals=false;
			for (int j = 0; j < json2.length; j++) {
				if(json2[j]!=null&&isEquals(json1[i],json2[j])){
					json2[j]=null;//剔除出数组
					isEquals=true;
					break;
				}
			}
			if(!isEquals){
				return false;
			}
		}
		return true;
	}
	
}
