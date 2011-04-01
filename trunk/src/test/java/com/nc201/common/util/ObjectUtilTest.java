package com.nc201.common.util;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ObjectUtilTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testJson2Map() {
		try {
			String s1 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"doctor\":{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}}";
			Map map=ObjectUtil.json2Map(s1);

			String s2=ObjectUtil.map2Json(map);
			assertTrue(ObjectUtil.isEquals(s1, s2));
			
			map.put("NL", null);
			s2=ObjectUtil.map2Json(map);
			assertFalse(ObjectUtil.isEquals(s1, s2));
						
		} catch (Exception e) {
			fail("Json error");
		}
	}
	@Test
	public void testJson2Map2() {
		try {
			String s1 = "[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]";
			@SuppressWarnings("rawtypes")
			List list=ObjectUtil.json2List(s1);
			String s2=ObjectUtil.toJson(list);
			assertTrue(ObjectUtil.isEquals(s1, s2));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Json error");
		}
	}
	

	@SuppressWarnings("rawtypes")
	@Test
	public void testMap2Json() {
		Map<String,Object> user=new HashMap<String,Object>();
		user.put("name", "张三");
		//user.put("NL", null);
		user.put("SR", "2009-10-30");
		user.put("XS", 5000.5);
		

		
		//2。课程
		List<Map<String,Object>> courses=new ArrayList<Map<String,Object>>();
		Map<String,Object>course1=new HashMap<String,Object>();
		course1.put("KCMC", "数学");
		course1.put("ZKCS", 118);
		course1.put("LSXM", "祖冲之");
		
		Map<String,Object>course2=new HashMap<String,Object>();
		course2.put("KCMC", "语文");
		course2.put("ZKCS", 128);
		course2.put("LSXM", "孔子");
		
		Map<String,Object>course3=new HashMap<String,Object>();
		course3.put("KCMC", "英语");
		course3.put("ZKCS", 138);
		course3.put("LSXM", "白求恩");	
		
		courses.add(course1);
		courses.add(course2);
		courses.add(course3);
		
		//3。班级
		Map<String,Object>userClass=new HashMap<String,Object>();
		userClass.put("BJMC", "雷锋班");
		userClass.put("BJRS", 108);
		userClass.put("JSMC", "世外桃源");	
		

		
		//选修课程
		user.put("XXKC", courses);
		user.put("SZBJ", userClass);
		
		try {
			String s1=ObjectUtil.map2Json(user);
			//String s2=ObjectUtil.map2Json(user);
			Map user2=ObjectUtil.json2Map(s1);
			assertTrue(ObjectUtil.isEquals(user, user2));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testIsContainStringString() {

		try {
			String s1 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			String s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			assertTrue(ObjectUtil.isContain(s1, s2));
			
			
			s2="{\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			assertTrue(ObjectUtil.isContain(s1, s2));
			
		
			
			s2="{\"NL2\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			assertFalse(ObjectUtil.isContain(s1, s2));
			
			s2="{\"yyyy\":26,\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			assertFalse(ObjectUtil.isContain(s1, s2));
			
			s2="{\"yyyy\":26,\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"}]}";
			assertFalse(ObjectUtil.isContain(s1, s2));

			s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩2\"}]}";
			assertFalse(ObjectUtil.isContain(s1, s2));
			
			
			s1 = "{\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			s2 = "{\"NL\":null,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}]}";
			assertFalse(ObjectUtil.isContain(s1, s2));	
			
			
			
			s1 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"SZBJ\":{\"BJMC\":\"雷锋班\",\"BJRS\":108,\"JSMC\":\"世外桃源\"}}";
			s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"SZBJ\":{\"BJMC\":\"雷锋班\",\"BJRS\":108,\"JSMC\":\"世外桃源\"}}";
			assertTrue(ObjectUtil.isContain(s1, s2));			
			
			s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"SZBJ2\":{\"BJMC\":\"雷锋班\",\"BJRS\":108,\"JSMC\":\"世外桃源\"}}";
			assertFalse(ObjectUtil.isContain(s1, s2));	
			
			s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"SZBJ\":{\"BJMC\":\"雷锋班\",\"BJRS\":108}}";
			assertTrue(ObjectUtil.isContain(s1, s2));			

			
			s2 = "{\"NL\":26,\"name\":\"张三\",\"SR\":\"1979-10-30\",\"XS\":5000.5,\"SFDY\":true,\"YHKC\":[{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"},{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"},{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}],\"SZBJ\":{\"BJMC\":\"雷锋班\",\"BJRS\":108,\"JSMC\":\"世外桃源\",\"JSMC2\":\"世外桃源\"}}";
			assertFalse(ObjectUtil.isContain(s1, s2));			
		} catch (Exception e) {
			fail("Json error");
		}
		// fail("Not yet implemented");
	}
	@Test
	public void testArrayDiff(){
		assertArrayEquals(new String[0], ObjectUtil.arrayDiff(new String[]{"1","2","3"}, new String[]{"1","2","3"}));
		assertArrayEquals(new String[]{"1"}, ObjectUtil.arrayDiff(new String[]{"1","2","3"}, new String[]{"4","2","3"}));
	}
	
	@Test
	public void testMergeArray(){
		assertArrayEquals(new String[]{"1","4","2","3"}, ObjectUtil.mergeArray(new String[]{"1","4"}, "2","3"));
	}
	
	@Test
	public void testIsEquals() throws Exception{
		String courseJson1="{\"KCMC\":\"数学\",\"ZKCS\":118,\"LSXM\":\"祖冲之\"}";
		String courseJson2="{\"KCMC\":\"语文\",\"ZKCS\":128,\"LSXM\":\"孔子\"}";
		String courseJson3="{\"KCMC\":\"英语\",\"ZKCS\":138,\"LSXM\":\"白求恩\"}";		
		assertTrue(ObjectUtil.isEquals(new String[]{courseJson1,courseJson2,courseJson3},courseJson3,courseJson2,courseJson1));
		assertFalse(ObjectUtil.isEquals(new String[]{courseJson1,courseJson2,courseJson2},courseJson3,courseJson1,courseJson2));
		assertTrue(ObjectUtil.isEquals(new String[]{courseJson1,courseJson2,courseJson2},courseJson2,courseJson1,courseJson2));
	}
	
	//@Test
	public void testToJson()throws Exception{
		//String USER_DEF="{\"labelName\":\"用户\",\"name\":\"user\",\"dbName\":\"TEST_USER\",\"props\":[{\"labelName\":\"年龄\",\"dataType\":\"INT\",\"dataLength\":2},{\"labelName\":\"姓名\",\"name\":\"name\",\"dbName\":\"name\",\"dataType\":\"STRING\",\"notAllowEmpty\":true},{\"labelName\":\"生日\",\"dataType\":\"DATE\"},{\"labelName\":\"薪水\",\"dataType\":\"DOUBLE\",\"dataLength\":\"9,2\"},{\"labelName\":\"是否党员\",\"dataType\":\"BOOLEAN\"}]}";
		//BODef boDef=ObjectUtil.json2Obj(USER_DEF, BODef.class);
		//assertEquals(ObjectUtil.toJson(boDef), "{\"dbName\":\"TEST_USER\",\"name\":\"user\",\"labelName\":\"用户\",\"versionNo\":0,\"isLastest\":false,\"props\":[{\"labelName\":\"年龄\",\"dataType\":\"INT\",\"dataLength\":\"2\",\"notAllowEmpty\":false,\"notAllowDuplicate\":false,\"isManyMaster\":false,\"isPrimaryKey\":false},{\"dbName\":\"name\",\"name\":\"name\",\"labelName\":\"姓名\",\"dataType\":\"STRING\",\"notAllowEmpty\":true,\"notAllowDuplicate\":false,\"isManyMaster\":false,\"isPrimaryKey\":false},{\"labelName\":\"生日\",\"dataType\":\"DATE\",\"notAllowEmpty\":false,\"notAllowDuplicate\":false,\"isManyMaster\":false,\"isPrimaryKey\":false},{\"labelName\":\"薪水\",\"dataType\":\"DOUBLE\",\"dataLength\":\"9,2\",\"notAllowEmpty\":false,\"notAllowDuplicate\":false,\"isManyMaster\":false,\"isPrimaryKey\":false},{\"labelName\":\"是否党员\",\"dataType\":\"BOOLEAN\",\"notAllowEmpty\":false,\"notAllowDuplicate\":false,\"isManyMaster\":false,\"isPrimaryKey\":false}]}");
		//System.out.println(ObjectUtil.toJson(boDef));
	}
}
