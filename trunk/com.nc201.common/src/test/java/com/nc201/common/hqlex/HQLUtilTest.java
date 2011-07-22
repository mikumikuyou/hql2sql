package com.nc201.common.hqlex;

import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nc201.common.util.FileUtil;
import com.nc201.common.util.ObjectUtil;

public class HQLUtilTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testTrans2Sql() throws Exception {
		String userJson = FileUtil.readFileContent(this.getClass()
				.getResourceAsStream("./user.json"), "GBK");
		String bookJson = FileUtil.readFileContent(this.getClass()
				.getResourceAsStream("./book.json"), "GBK");
		String userClassJson = FileUtil.readFileContent(this.getClass()
				.getResourceAsStream("./course.json"), "GBK");

		HQLUtil.registerClass(userJson);

		HQLUtil.registerClass(bookJson);

		HQLUtil.registerClass(userClassJson);

		String hqlArrStr = FileUtil.readFileContent(this.getClass()
				.getResourceAsStream("./testhql.json"), "GBK");
		List<Map> list = ObjectUtil.json2List(hqlArrStr);

		String[] dbTypes = new String[] { "org.hibernate.dialect.DB2Dialect",
				"org.hibernate.dialect.Oracle9iDialect",
				"org.hibernate.dialect.HSQLDialect",
				"org.hibernate.dialect.MySQLDialect",
				"org.hibernate.dialect.SQLServerDialect" };

		StringBuffer json = new StringBuffer("[");

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Map map = (Map) iterator.next();
			if (json.length() > 1) {
				json.append(",");
			}
			json.append("{");
			String hql = map.get("hql").toString();
			json.append("\"hql\":" + "\"" + hql + "\",\"result\":{");
			String result = null;
			for (int i = 0; i < dbTypes.length; i++) {

				HQLUtil.setDialect(dbTypes[i]);
				result = HQLUtil.trans2Sql(hql);

				String dbType = HQLUtil.getDialectName();

				// System.out.println(dbType+":"+result);
				if (i > 0) {
					json.append(",");
				}
				json.append("\"" + dbType + "\":" + "\"" + result + "\"");
				Map resultMap = (Map) map.get("result");
				String standResult = resultMap.get(dbType).toString();
				assertEquals(standResult, result);
			}

			json.append("}}");

		}
		json.append("]");

		/*
		 * HQLUtil.trans2Sql(
		 * "select new map( u.name as name2,u.age as age2) from user u ");
		 * HQLUtil
		 * .trans2Sql("select u from book u where u.user.name is not null");
		 * HQLUtil.trans2Sql("from java.lang.Object");
		 */
		// System.out.println(json.toString());
	}

}
