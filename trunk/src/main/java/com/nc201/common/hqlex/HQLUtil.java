package com.nc201.common.hqlex;

import java.util.HashMap;

import org.hibernate.hql.QuerySplitter;
import org.hibernate.hql.ast.QueryTranslatorImpl;

/**
 * HQL工具类
 * @author zhuzhf
 *
 */
public class HQLUtil {
	/**
	 * 将hql转换为sql语句
	 * @param hql
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String trans2Sql(String hql){
		String[] filterSql=QuerySplitter.concreteQueries(hql, DetachedSessionFactoryImpl.getInstance());		
		QueryTranslatorImpl translatorImpl=new QueryTranslatorImpl(filterSql[0],filterSql[0],new HashMap(),DetachedSessionFactoryImpl.getInstance());
		translatorImpl.compile(new HashMap(), false);
		return translatorImpl.getSQLString();
	}
}
