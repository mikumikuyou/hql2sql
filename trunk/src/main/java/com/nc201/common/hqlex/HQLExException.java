package com.nc201.common.hqlex;
/**
 * hql 异常类
 * @author zhuzhf
 *
 */
public class HQLExException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public HQLExException(String message) {
    	super(message);
    }
}
