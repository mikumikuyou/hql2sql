package com.nc201.common.hqlex;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.StatelessSession;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.Region; 
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.PersisterFactory;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;

import com.nc201.common.util.ObjectUtil;


/**
 * 实现SessionFactoryImplementor，目的是将hql的解析环境从hibernate上下文环境剥离出来
 * 单例，系统唯一
 * @author zhuzhf
 *
 */
public class DetachedSessionFactoryImpl extends Configuration implements  SessionFactoryImplementor {

	private SessionFactoryImplementor sfi;
	//使用单例
	public static DetachedSessionFactoryImpl factoryImpl=new DetachedSessionFactoryImpl();
	public static DetachedSessionFactoryImpl getInstance(){
		return factoryImpl;
	}
	
	private DetachedSessionFactoryImpl(){	
		//默认Oracle9i数据库
		setDialect("org.hibernate.dialect.Oracle9iDialect");	
	}
	
	public void setDialect(String className){
		try {
			@SuppressWarnings("rawtypes")
			Class aClass=Class.forName(className);
			Object tmpObj=aClass.newInstance();
			if(!(tmpObj instanceof Dialect)){
				throw new HQLExException(className+"不是Dialect的实现类");
			}
				dialect=(Dialect)tmpObj;
				Configuration cfg=new Configuration();
				Properties properties=new Properties();
				properties.put("hibernate.dialect", dialect.getClass().getName());
				cfg.addProperties(properties);
				settings=cfg.buildSettings();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new HQLExException(e.getMessage());
		} 
	}
	public String getDbName() {		
		return dialect.getClass().getSimpleName().substring(0,dialect.getClass().getSimpleName().length()-"Dialect".length());
	}
	
	@Override
	public Dialect getDialect() {		
		return dialect;
	}
	@Override
	public Settings getSettings() {		
		return sfi==null?settings:sfi.getSettings();
	}	
		
	
	private static final long serialVersionUID = 1L;

	private Settings settings;
	
	private Dialect dialect;

	
	/**
	 * 集合实体Map
	 */
	private  Map<String,CollectionPersister> collectionPersisters=new HashMap<String,CollectionPersister> ();
	
	/**
	 * 所有实体EntityPersister Map
	 */
	private  Map<String,EntityPersister> entityPersisterMap=new HashMap<String,EntityPersister> ();
	
	/**
	 * 所有实体PersistentClass Map
	 */
	private  Map<String,PersistentClass> persistentClassMap=new HashMap<String, PersistentClass>();
	


	
/*	*//**
	 * 延迟加载Collection
	 *//*
	private  Map<String,List<Collection>> delayLoadCollection=new HashMap<String,List<Collection>>();
*/

	static class PropertyWrap{
		public enum ASS_TYPE{
			ONE2MANY,MANY2MANY
		}
		//当前属性
		private Property property;
		//关联实体名称
		private String relationEntityName;
		//关联属性名称
		private String mappedPropertyName;
		//关联类型
		private ASS_TYPE relationType;
		
		/**
		 * 关联表名称，relationType=MANY2MANY时有效
		 */
		private String joinTblName;
				
		/**
		 * 指定主体端的外键名称，relationType=MANY2MANY时有效
		 */
		private String joinColName;


		/**
		 * 指定被关联端的外键名称，relationType=MANY2MANY时有效
		 */	
		private String inverseJoinColName;
		
		
		private boolean isManyMaster;

		
		
		public boolean isManyMaster() {
			return isManyMaster;
		}

		public void setManyMaster(boolean isManyMaster) {
			this.isManyMaster = isManyMaster;
		}

		public String getJoinTblName() {
			return joinTblName;
		}

		public void setJoinTblName(String joinTblName) {
			this.joinTblName = joinTblName;
		}

		public String getJoinColName() {
			return joinColName;
		}

		public void setJoinColName(String joinColName) {
			this.joinColName = joinColName;
		}

		public String getInverseJoinColName() {
			return inverseJoinColName;
		}

		public void setInverseJoinColName(String inverseJoinColName) {
			this.inverseJoinColName = inverseJoinColName;
		}

		public Property getProperty() {
			return property;
		}

		public void setProperty(Property property) {
			this.property = property;
		}

		public String getRelationEntityName() {
			return relationEntityName;
		}

		public void setRelationEntityName(String relationEntityName) {
			this.relationEntityName = relationEntityName;
		}

		public String getMappedPropertyName() {
			return mappedPropertyName;
		}

		public void setMappedPropertyName(String mappedPropertyName) {
			this.mappedPropertyName = mappedPropertyName;
		}

		public ASS_TYPE getRelationType() {
			return relationType;
		}

		public void setRelationType(ASS_TYPE relationType) {
			this.relationType = relationType;
		}

		public PropertyWrap(Property property, String relationEntityName,
				String mappedPropertyName, ASS_TYPE relationType) {
			super();
			this.property = property;
			this.relationEntityName = relationEntityName;
			this.mappedPropertyName = mappedPropertyName;
			this.relationType = relationType;
		}
		
		
	}
	//延迟加载属性集合
	private Map<String,List<PropertyWrap>> delayLoadPropertyMap=new HashMap<String,List<PropertyWrap>>();
	
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
 
	 * @param persistentClass
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  void pushClass(String json){
		Map classDataMap=null;
		try {
			classDataMap=ObjectUtil.json2Map(json);
		} catch (Exception e) {
			throw new HQLExException("输入json格式不符合数据规范："+json);
		}
		RootClass  mc=new RootClass();
		org.hibernate.mapping.Table table = new org.hibernate.mapping.Table(classDataMap.get("dbName").toString());
		
		mc.setEntityName(classDataMap.get("name").toString());
		mc.setNodeName(classDataMap.get("name").toString());
		mc.setTable(table);
		
		Object tmpObj=classDataMap.get("props");
		if(tmpObj==null||!(tmpObj instanceof List)){
			throw new HQLExException("json中的props不符合数据规范："+json);
		}
		List<Map> props=(List)tmpObj;
		for (Iterator iterator = props.iterator(); iterator.hasNext();) {
			Map propMap = (Map) iterator.next();
			
			String dataType=propMap.get("dataType").toString();
			
			Property property = new Property();
			property.setName( propMap.get("name").toString());
			property.setNodeName( propMap.get("name").toString() );
			//property.setPropertyAccessorName("field");			
			
			
			SimpleValue propValue=null;
			if("OBJECT".equalsIgnoreCase(dataType)){
				propValue=new ManyToOne(table);
				((ManyToOne)propValue).setReferencedEntityName(propMap.get("relaEntityName").toString());
				propValue.setTypeName(propMap.get("relaEntityName").toString());
			}else if("SET".equalsIgnoreCase(dataType)){
				//complexProps.add(boPropDef);
				PersistentClass relaClass=persistentClassMap.get(propMap.get("relaEntityName").toString());
				PropertyWrap propertyWrap=null;
				//如果多对多
				if(propMap.get("isManyMany")!=null&&"true".equalsIgnoreCase(propMap.get("isManyMany").toString())){
					
					propertyWrap=new PropertyWrap(property, propMap.get("relaEntityName").toString(), propMap.get("mappedPropName").toString(), PropertyWrap.ASS_TYPE.MANY2MANY);
					propertyWrap.setJoinTblName(propMap.get("joinTblName").toString());
					propertyWrap.setJoinColName(propMap.get("joinColName").toString());
					//propertyWrap.setInverseJoinColName(propMap.get("inverseJoinColName").toString());
					
					if(propMap.get("isManyMaster")!=null&&"true".equalsIgnoreCase(propMap.get("isManyMaster").toString())&&relaClass!=null){
						propertyWrap.setManyMaster(true);
						
					}else{
						propertyWrap.setManyMaster(false);
					}
					

					
				}else{
					propertyWrap=new PropertyWrap(property, propMap.get("relaEntityName").toString(), propMap.get("mappedPropName").toString(), PropertyWrap.ASS_TYPE.ONE2MANY);


				}
				property.setPersistentClass(mc);
				if(relaClass==null){
					//延迟加载
					List<PropertyWrap> propertyWraps =delayLoadPropertyMap.get(propMap.get("relaEntityName").toString());
					if(propertyWraps==null){
						propertyWraps=new ArrayList<PropertyWrap>();
					}
					propertyWraps.add(propertyWrap);
					delayLoadPropertyMap.put(propMap.get("relaEntityName").toString(), propertyWraps);
				}else{					
					doRelation(propertyWrap);
				}
				
				
				continue;
				
			}else {
				propValue=new SimpleValue(table);
				if("STRING".equalsIgnoreCase(dataType)){					
					propValue.setTypeName("java.lang.String");								
				}else if("BOOLEAN".equalsIgnoreCase(dataType)){
					propValue.setTypeName("boolean");
				}else if("INT".equalsIgnoreCase(dataType)){
					propValue.setTypeName("int");
				}else if("DOUBLE".equalsIgnoreCase(dataType)){
					propValue.setTypeName("java.lang.Double");
				}else if("DATE".equalsIgnoreCase(dataType)){
					propValue.setTypeName("java.lang.String");
				}
												
			}

			
			Column column=new Column();
			column.setName(propMap.get("dbName").toString());
			column.setValue(propValue);
			propValue.addColumn(column);					
			property.setValue(propValue);	
			
			
			mc.addProperty(property);
			
			//是主键
			if(propMap.get("isPrimaryKey")!=null&&"true".equalsIgnoreCase(propMap.get("isPrimaryKey").toString())){
				mc.setIdentifierProperty(property);
				mc.setIdentifier(propValue);
			}

		}
		pushClass(mc);	

		if(delayLoadPropertyMap.get(mc.getEntityName())!=null){
			List<PropertyWrap> propertyWraps =delayLoadPropertyMap.get(mc.getEntityName());
			for (Iterator iterator = propertyWraps.iterator(); iterator
					.hasNext();) {
				PropertyWrap delayPropertyWrap = (PropertyWrap) iterator.next();
				doRelation(delayPropertyWrap);
			}
			delayLoadPropertyMap.remove(mc.getEntityName());
		}	
		
	}
	private void doRelation(PropertyWrap propertyWrap){
		Collection propValue=null;
		if(propertyWrap.getRelationType()==PropertyWrap.ASS_TYPE.ONE2MANY){
			PersistentClass relaClass=persistentClassMap.get(propertyWrap.getRelationEntityName());
			
			propValue=new Bag(relaClass);
			propValue.setRole(propertyWrap.getProperty().getPersistentClass().getEntityName()+"."+relaClass.getNodeName());
			propValue.setCollectionTable(relaClass.getTable());
			propertyWrap.getProperty().setValue(propValue);
			org.hibernate.mapping.OneToMany element = new org.hibernate.mapping.OneToMany( relaClass );
			propValue.setElement( element );	
			element.setReferencedEntityName(propValue.getOwner().getEntityName());
			
			
			KeyValue keyVal=relaClass.getIdentifier();
			DependantValue key = new DependantValue( propValue.getCollectionTable(), keyVal );
			
			key.setTypeName( null );
			
			Column joinCol=new Column();
			@SuppressWarnings("rawtypes")
			Iterator itor=relaClass.getProperty(propertyWrap.getMappedPropertyName()).getColumnIterator();
			while(itor.hasNext()){
				Column col=(Column)itor.next();
				joinCol.setName(col.getQuotedName(this.getDialect()));
			}
			element.setAssociatedClass(relaClass);
			
			key.addColumn(joinCol);
			propValue.setKey(key);		
		}else{
			PersistentClass leftClass=propertyWrap.getProperty().getPersistentClass();
			PersistentClass rightClass=persistentClassMap.get(propertyWrap.getRelationEntityName());
			propValue=new org.hibernate.mapping.Set(rightClass);
			propValue.setRole(leftClass.getEntityName()+"."+rightClass.getNodeName());
			//propValue.setFetchMode(FetchMode.SELECT);
			//propValue.setElementNodeName(boPropDef.getRelaBODef().getName());
			propertyWrap.getProperty().setValue(propValue);

			org.hibernate.mapping.Table table=null;

			Property otherProperty=null;
			try {
				otherProperty=rightClass.getProperty(propertyWrap.getMappedPropertyName());
			} catch (MappingException e) {
				//未找到属性，不抛出异常
			}
			if(otherProperty!=null){
				Collection otherCollection=(Collection)rightClass.getProperty(propertyWrap.getMappedPropertyName()).getValue();
				if(otherCollection.getCollectionTable()!=null){
					table=otherCollection.getCollectionTable();
				}
			}

			propValue.setCollectionTable(table==null?new org.hibernate.mapping.Table(propertyWrap.getJoinTblName()):table);
			
			org.hibernate.mapping.ManyToOne element = new org.hibernate.mapping.ManyToOne( propValue.getCollectionTable() );
			element.setReferencedEntityName(rightClass.getEntityName());
			element.setFetchMode(FetchMode.JOIN);
			//element.setLazy( false );
			propValue.setElement( element );
			
			KeyValue keyVal=leftClass.getIdentifier();

			
			
			DependantValue key = new DependantValue( propValue.getCollectionTable(), keyVal );
			
			key.setTypeName( null );				
			Column joinCol=new Column();
			joinCol.setName(propertyWrap.getJoinColName());
			element.addColumn(joinCol);
			
			propValue.getCollectionTable().addColumn(joinCol);
			if(otherProperty!=null){
				Collection otherCollection=((Collection)otherProperty.getValue());
				@SuppressWarnings("rawtypes")
				Iterator itor=otherCollection.getElement().getColumnIterator();
				if(itor.hasNext()){
					Column col=(Column)itor.next();
					key.addColumn(col);
					propValue.getCollectionTable().addColumn(col);
					((DependantValue)otherCollection.getKey()).addColumn(joinCol);
				}
				initCollection(otherCollection);
			}

			propValue.setKey(key);
		}
		
		propertyWrap.getProperty().getPersistentClass().addProperty(propertyWrap.getProperty());
		//persistentClassMap.put(propertyWrap.getProperty().getPersistentClass().getEntityName(), propertyWrap.getProperty().getPersistentClass());
		entityPersisterMap.put(propertyWrap.getProperty().getPersistentClass().getEntityName(), new SingleTableEntityPersister(propertyWrap.getProperty().getPersistentClass() ,null, this, toMapping(propertyWrap.getProperty().getPersistentClass())));
		initCollection(propValue);
	}
	
	/**
	 * 加入类
	 * @param persistentClass
	 */
	private  void pushClass(PersistentClass persistentClass){
		persistentClassMap.put(persistentClass.getEntityName(), persistentClass);
		entityPersisterMap.put(persistentClass.getEntityName(), new SingleTableEntityPersister(persistentClass ,null, this, toMapping(persistentClass)));
	}

	/**
	 * 初始化集合对象
	 */
	private  void initCollection(Collection collection){
		CollectionPersister collectionPersister = PersisterFactory.createCollectionPersister( this, collection, null,this) ;
		collectionPersisters.put(collection.getRole(), collectionPersister);
	}
	
	private static Mapping toMapping(final PersistentClass pClass){
		Mapping mapping=new Mapping() {
			/**
			 * Returns the identifier type of a mapped class
			 */
			public Type getIdentifierType(String persistentClass) throws MappingException {
				PersistentClass pc = pClass;
				if ( pc == null ) {
					throw new MappingException( "persistent class not known: " + persistentClass );
				}
				return pc.getIdentifier().getType();
			}

			public String getIdentifierPropertyName(String persistentClass) throws MappingException {
				PersistentClass pc = pClass;
				if ( pc == null ) {
					throw new MappingException( "persistent class not known: " + persistentClass );
				}
				if ( !pc.hasIdentifierProperty() ) {
					return null;
				}
				return pc.getIdentifierProperty().getName();
			}

			public Type getReferencedPropertyType(String persistentClass, String propertyName) throws MappingException {
				PersistentClass pc = pClass;
				if ( pc == null ) {
					throw new MappingException( "persistent class not known: " + persistentClass );
				}
				Property prop = pc.getReferencedProperty( propertyName );
				if ( prop == null ) {
					throw new MappingException(
							"property not known: " +
							persistentClass + '.' + propertyName
						);
				}
				return prop.getType();
			}
		};
		return mapping;
	}		
	
	
	/**
	 * TODO 类缩略名
	 */
	@Override
	public String[] getImplementors(String className) throws MappingException {		
		return new String[]{className};
	}


	@Override
	public String getImportedClassName(String name) {
		EntityPersister entityPersister=entityPersisterMap.get(name );
		if(entityPersister==null&&sfi!=null){
			return sfi.getImportedClassName(name);
		}else if(entityPersister!=null){
			return entityPersister.getEntityName();
		}else{
			return null;
		}
		//return this.getEntityPersister(name).getEntityName();
	}
	


	
	@Override
	public Type getIdentifierType(String className) throws MappingException {
		return getEntityPersister(className).getIdentifierType();
	}
	public String getIdentifierPropertyName(String className) throws MappingException {
		return getEntityPersister(className).getIdentifierPropertyName();
	}

	@Override
	public EntityPersister getEntityPersister(String entityName)
			throws MappingException {
		EntityPersister entityPersister=entityPersisterMap.get(entityName );	
		return sfi!=null&&entityPersister==null?sfi.getEntityPersister(entityName):entityPersister;
	}

	@Override
	public CollectionPersister getCollectionPersister(String role)
			throws MappingException {
		CollectionPersister collectionPersister=collectionPersisters.get(role );	
		return sfi!=null&&collectionPersister==null?sfi.getCollectionPersister(role):collectionPersister;
	}

	/**
	 * TODO 
	 */
	@Override
	public PersistentClass getClassMapping(String entityName) {
		//sfi.ge
		//return null;
		return  persistentClassMap.get(entityName );
	}	
	
	

	public void setSessionFactory(SessionFactoryImplementor sfi) {
		this.sfi = sfi;
	}

	public SessionFactoryImplementor getSessionFactory() {
		return sfi;
	}
	

	/******************************************************************************************************/
	
	
	
	@Override
	public Type getReferencedPropertyType(String className, String propertyName)
			throws MappingException {
		return null;
	}

	@Override
	public Session openSession(Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session openSession(Interceptor interceptor)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session openSession(Connection connection, Interceptor interceptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session openSession() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getCurrentSession() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassMetadata getClassMetadata(@SuppressWarnings("rawtypes") Class persistentClass)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName)
			throws HibernateException {

		return null;
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String roleName)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getAllClassMetadata() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getAllCollectionMetadata() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statistics getStatistics() {
		return null;
	}

	@Override
	public void close() throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void evict(@SuppressWarnings("rawtypes") Class persistentClass) throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evict(@SuppressWarnings("rawtypes") Class persistentClass, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictEntity(String entityName) throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictEntity(String entityName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictCollection(String roleName) throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictCollection(String roleName, Serializable id)
			throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictQueries() throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictQueries(String cacheRegion) throws HibernateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatelessSession openStatelessSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set getDefinedFilterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reference getReference() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Interceptor getInterceptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryPlanCache getQueryPlanCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type[] getReturnTypes(String queryString) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getReturnAliases(String queryString)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionProvider getConnectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TransactionManager getTransactionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryCache getQueryCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryCache getQueryCache(String regionName)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateTimestampsCache getUpdateTimestampsCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatisticsImplementor getStatisticsImplementor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedQueryDefinition getNamedQuery(String queryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetMappingDefinition getResultSetMapping(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region getSecondLevelCacheRegion(String regionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getAllSecondLevelCacheRegions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLExceptionConverter getSQLExceptionConverter() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Session openTemporarySession() throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session openSession(Connection connection,
			boolean flushBeforeCompletionEnabled,
			boolean autoCloseSessionEnabled,
			ConnectionReleaseMode connectionReleaseMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set getCollectionRolesByEntityParticipant(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLFunctionRegistry getSqlFunctionRegistry() {
		// TODO Auto-generated method stub
		return null;
	}


}
