package com.shanghai.util;

import org.springframework.util.StringUtils;

import com.spatial4j.core.context.jts.JtsSpatialContextFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 多边形辅助类
 * @author jin.lv
 *
 */
public class GeometryUtil {
	
	private static GeometryFactory geometryFactory = new JtsSpatialContextFactory().getGeometryFactory();
	private static WKTReader reader = new WKTReader( geometryFactory );
    
    /**
     * intersect polygon
     * @param arg1 polygon
     * @param arg2 polygon
     * @return
     * @throws ParseException
     */
    public static String getIntersectPolygon(String arg1,String arg2) throws ParseException{  
    	
        Polygon polygon1 = (Polygon) reader.read(transferPolygonToString(arg1));  
        Polygon polygon2 = (Polygon) reader.read(transferPolygonToString(arg2));  
        Geometry interPolygon = polygon1.intersection(polygon2);//相交点  
        if(interPolygon != null){
        	String polygon = interPolygon.toText();
        	if(StringUtils.isEmpty(polygon)){
    			return null;
    		}
        	polygon = polygon.substring(10, polygon.length()-2);
        	String[] args1 = StringUtil.splitByComma(polygon);
        	StringBuffer sb = new StringBuffer();
        	for (String arg : args1) {
        		String[] args2 = StringUtil.splitByBlank(arg.trim());
        		sb.append(args2[0]).append(",").append(args2[1]).append(";");
			}
        	return sb.toString().substring(0,sb.toString().length()-1);
        }
        return null;  
    }  
    
    /**
     * Polygon2 is contains Polygon1
     * @param arg1 Polygon1
     * @param arg2 Polygon2
     * @return
     * @throws ParseException
     */
    public static boolean isContainsPolygon(String arg1,String arg2) throws ParseException{  
		Polygon op = (Polygon) reader.read(transferPolygonToString(arg1));
		Polygon np = (Polygon) reader.read(transferPolygonToString(arg2));
		return np.contains(op);
    }
	
    
    /**
     * transfer polygon to string 
     * @param polygon
     * @return
     */
    private static String transferPolygonToString(String polygon){
		StringBuffer sb = new StringBuffer();
		sb.append("POLYGON((");
		String[] agrs = StringUtil.splitBySemicolon(polygon);
		for (int i = 0; i < agrs.length; i++) {
			String[] p = StringUtil.splitByComma(agrs[i]);
			sb.append(p[0]).append(" ").append(p[1]).append(",");
		}
		String temp = sb.toString().substring(0,sb.toString().length()-1);
		return temp+"))";
		
	}
    
    public static void main(String[] args) {
    	String arg1 = "31.23745,121.391491;31.226696,121.396212;31.228054,121.388744;31.227357,121.375783;31.238624,121.383679;31.23745,121.391491";
    	String arg2 = "31.23745,121.391491;31.226696,121.396212;31.228054,121.388744;31.227357,121.375783;31.238624,121.383679;31.23745,121.391491";
    	try {
			System.out.println(isContainsPolygon(arg1, arg2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
