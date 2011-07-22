package com.nc201.common.hqlex;

import com.nc201.common.hqlex.DetachedSessionFactoryImpl;
import com.nc201.common.hqlex.HQLUtil;

public class Demo1 {
	public static void main(String[] args) {
		String userJson= "{"
						+"   \"name\":\"user\","
						+"   \"dbName\":\"Test_User\","
						+"   \"props\":["
						+"      {"
						+"         \"name\":\"id\","
						+"         \"dbName\":\"id\","
						+"         \"dataType\":\"STRING\","
						+"         \"isPrimaryKey\":true"
						+"      },"
						+"      {"
						+"         \"name\":\"name\","
						+"         \"dbName\":\"name\","
						+"         \"dataType\":\"STRING\""
						+"      },"
						+"      {"
						+"         \"name\":\"age\","
						+"         \"dbName\":\"age\","
						+"         \"dataType\":\"int\""
						+"      },"
						+"      {"
						+"         \"name\":\"salary\","
						+"         \"dbName\":\"slary\","
						+"         \"dataType\":\"double\""
						+"      }"
						+"   ]"
						+"}";
		HQLUtil.registerClass(userJson);

		System.out.println(HQLUtil.trans2Sql("from user "));
		System.out.println(HQLUtil.trans2Sql("from user where name='张三'"));
	}
}
