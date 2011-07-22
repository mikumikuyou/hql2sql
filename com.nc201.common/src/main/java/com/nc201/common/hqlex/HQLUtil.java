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
	/**
	 * 加入类 以json形式
	 * json格式如：
	 * 
		{
		   "name":"com.xxxx.yyyy.User",                  //类名(包含包名，如：com.xxxx.yyyy.User)
		   "nodeName":"User",                            //节点名(暂无用)		   
		   "dbName":"Test_User",                         //表名
		   "props":[                                     //类属性
		      {
		         "name":"id",                            //属性名称
		         "dbName":"id",                          //数据库字段名
		         "dataType":"STRING",                    //数据类型
		         "isPrimaryKey":true                     //是否主键
		      },
		      {
		         "name":"name",
		         "dbName":"name",
		         "dataType":"STRING"
		      },
		      {
		         "name":"age",
		         "dbName":"age",
		         "dataType":"int"
		      },
		      {
		         "name":"salary",
		         "dbName":"slary",
		         "dataType":"double"
		      },
		      {
		         "name":"brithday",
		         "dbName":"brithday",
		         "dataType":"STRING"
		      },
		      {                                          //多对一
		         "name":"user",
		         "dbName":"userId",
		         "dataType":"OBJECT",
		         "relaEntityName":"user"
		      }		      
		      {                                          //一对多
		         "name":"books",
		         "dataType":"SET",
		         "relaEntityName":"book",
		         "mappedPropName":"user"                 //关联实体属性
		      },
		      {                                          //多对多
		         "name":"KCS",                           //属性名称
		         "dataType":"SET",                       //数据类型
		         "relaEntityName":"KC",                       //关联实体
		         "mappedPropName":"users",               //关联实体属性
		         "joinTblName":"RELA_USER_KC",           //多对多关联表
		         "joinColName":"kc_id",                  //关联Id（多对多适用）
		         "isManyMany":"true",                    //是否多对多
		         "isManyMaster":"true"                   //是否多对多主控方（暂无用）
		      }
		   ]
		}
 
	 * @param json
	 */	
	public static void registerClass(String json){
		DetachedSessionFactoryImpl.getInstance().registerClass(json);
	}
	/**
	 * 选择数据库方言
	 * 方言类型，参照org.hibernate.dialect.Dialect实现类
	 * @param className 使用全路径名，例如：org.hibernate.dialect.DB2Dialect、org.hibernate.dialect.Oracle9iDialect
	 */
	public static void setDialect(String className){
		DetachedSessionFactoryImpl.getInstance().setDialect(className);
	}
	public static String getDialectName() {
		return DetachedSessionFactoryImpl.getInstance().getDbName();
	}
	
}
