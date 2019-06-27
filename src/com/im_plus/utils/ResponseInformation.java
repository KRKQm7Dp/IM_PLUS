package com.im_plus.utils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 此类用于给 ajax 请求返回状态信息（json 格式）
 */
public class ResponseInformation {

	public static String getSuccessInformation(){
		JSONObject json = new JSONObject();
		try {
			json.put("status", "success");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static String getErrorInformation(String reason) {
		JSONObject json = new JSONObject();
		try {
			json.put("status", "error");
			json.put("reason", reason);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static String getErrorInformation(Exception ex) {
		JSONObject json = new JSONObject();
		try {
			json.put("status", "success");
			json.put("reason", ex.getMessage());
		} catch (Exception e) {
			e.getSuppressed();
		}
		return json.toString();
	}
}
