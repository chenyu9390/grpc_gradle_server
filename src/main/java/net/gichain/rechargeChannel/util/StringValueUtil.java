package net.gichain.rechargeChannel.util;

import java.math.BigDecimal;
import java.util.Map;

public class StringValueUtil {
	/**
	 * 字符串拼接
	 * @author ck
	 * @date 2019/6/12 10:22 
	 * @param args
	 * @return java.lang.String
	**/
	public static String appendString(String ...args){
		StringBuilder builder = new StringBuilder();
		for(String arg : args){
			builder.append(arg);
		}
		return builder.toString();
	}

	/**
	 * 获取value的默认值
	 * @param value value
	 * @param def 默认值
	 * @return
	 */
	public static String getDefaultValue(String value,String def){
		return StringValueUtil.isEmpty(value)?def:value;
	}

	/**
	 * @author ck
	 * @since  是否为空校验
	 * @date 2019/4/9 10:51
	 * @param value
	 * @return boolean
	 **/
	public static boolean isEmpty(String value){
		if(value == null || "null".equalsIgnoreCase(value) || "".equals(value)){
			return true;
		}
		return false;
	}

	/**
	 * 查询float值
	 * @param value value
	 * @return
	 */
	public static float getFloatValue(String value){
		try{
			return Float.parseFloat(value);
		}catch (Exception e){

		}
		return 0;
	}

	/**
	 * 查询int值
	 * @param value value
	 * @return
	 */
	public static Integer getIntValue(String value){
		try{
			return Integer.parseInt(value);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断是不是整形
	 * @param value
	 * @return
	 */
	public static boolean isInt(String value){
		try{
			Integer.parseInt(value);
			return true;
		}catch (Exception e){
		}
		return false;
	}

	/**
	 * 判断是不是数字
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value){
		try{
			new BigDecimal(value);
			return true;
		}catch (Exception e){
		}
		return false;
	}

	/**
	 * 是否大于0
	 * @param value value
	 * @param isEqual 是否包含等于0
	 * @return
	 */
	public static boolean isMoreThanZero(String value,boolean isEqual){
		try{
			if(isEqual){
				return new BigDecimal(value).compareTo(BigDecimal.ZERO) >= 0;
			}else {
				return new BigDecimal(value).compareTo(BigDecimal.ZERO)>0;
			}

		}catch (Exception e){
		}
		return false;
	}

	/**
	 * 查询long值
	 * @param value value
	 * @return
	 */
	public static long getLongValue(String value){
		try{
			return Long.parseLong(value);
		}catch (Exception e){

		}
		return 0;
	}

	/**
	 * 数值转换百分比
	 * @param value value
	 * @return
	 */
	public static String getPercentValue(String value){
		try{
			return new BigDecimal(value).multiply(new BigDecimal("100")).toPlainString();
		}catch (Exception e){

		}
		return null;
	}

	/**
	 * map中查询值
	 * @param map map
	 * @param key key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getValueByMap(Map<String,Object> map,String key,String defaultValue){
		return map.get(key)==null?defaultValue:StringValueUtil.isEmpty(map.get(key).toString())?defaultValue:map.get(key).toString();
	}

	public static Boolean getValueByMap(Map<String,Object> map,String key,Boolean defaultValue){
		return map.get(key)==null?defaultValue:StringValueUtil.isEmpty(map.get(key).toString())?defaultValue:Boolean.valueOf(map.get(key).toString());
	}
	/**
	 * map中查询值
	 * @param map map
	 * @param key key
	 * @param bigDecimal 默认值
	 * @return
	 */
	public static String getValueByMap(Map<String,Object> map, String key, BigDecimal bigDecimal){
		return map.get(key)==null?bigDecimal.toPlainString():StringValueUtil.isEmpty(map.get(key).toString())?bigDecimal.stripTrailingZeros().toPlainString():new BigDecimal(map.get(key).toString()).stripTrailingZeros().toPlainString();
	}

	/**
	 * map中查询值
	 * @param map map
	 * @param key key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static int getIntValueByMap(Map<String,Object> map,String key,int defaultValue){
		return map.get(key)==null?defaultValue:StringValueUtil.isEmpty(map.get(key).toString())?defaultValue:StringValueUtil.getIntValue(map.get(key).toString());
	}

	/**
	 * map中查询值
	 * @param map map
	 * @param key key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static Long getValueByMap(Map<String,Object> map,String key,long defaultValue){
		return map.get(key)==null?defaultValue:StringValueUtil.isEmpty(map.get(key).toString())?defaultValue:StringValueUtil.getLongValue(map.get(key).toString());
	}

	/**
	 * 利率值校验
	 * @param rate 利率
	 * @return
	 */
	public static boolean rateCheck(String rate){
		if(StringValueUtil.isEmpty(rate) || !StringValueUtil.isNumber(rate) || StringValueUtil.getFloatValue(rate) < 0 || StringValueUtil.getFloatValue(rate) > 1){
			return false;
		}
		return true;
	}

	public static void main(String[] args){
		boolean is = rateCheck("saa");
		System.out.println(StringValueUtil.getIntValue("0.001"));
	}
}