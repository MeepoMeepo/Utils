package com.shanghai.util;

import org.springframework.util.StringUtils;

/**
 * 
 * @author jin.lv
 * String 辅助类
 */
public class StringUtil {

	/**
	 * 根据“,”进行分割
	 * @param arg
	 * @return
	 */
	public static final String[] splitByComma(String arg){
		if (StringUtils.isEmpty(arg)) {
			throw new IllegalArgumentException("arg is empty!");
		}
		String[] args = arg.split(",");
		return args;
	}
	
	/**
	 * 根据“;”进行分割
	 * @param arg
	 * @return
	 */
	public static final String[] splitBySemicolon(String arg){
		if (StringUtils.isEmpty(arg)) {
			throw new IllegalArgumentException("arg is empty!");
		}
		String[] args = arg.split(";");
		return args;
	}
	
	public static void main(String[] args) {
		String[] s = splitByBlank("28.203434 112.96732");
		System.out.println(s[0]);
		System.out.println(s[1]);
	}
	
	/**
	 * 根据“ ”进行分割
	 * @param arg
	 * @return
	 */
	public static final String[] splitByBlank(String arg){
		if (StringUtils.isEmpty(arg)) {
			throw new IllegalArgumentException("arg is empty!");
		}
		String[] args = arg.split(" ");
		return args;
	}
}
