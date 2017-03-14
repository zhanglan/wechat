package com.zhanglan.demo.wechat;

import okhttp3.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author 张岚
 * @data 2016-4-18 下午1:49:05
 */
public class Tools {

	@NotNull
	public static String inputStream2String(InputStream in) {
		
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static Map<String, String> parseXml(String xmlData){
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlData);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element rootEle = doc.getRootElement();
		List<Element> list = rootEle.elements();
		HashMap<String, String> map = new HashMap<String, String>();
		for(Element e:list){
			map.put(e.getName(), e.getText());
		}
		return map;
	}
	
	public static String generatorXml(Map<String, String> reply){
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		for(Entry<String, String> entry:reply.entrySet()){
			Element e = root.addElement(entry.getKey());
			e.setText(entry.getValue());
		}
		return document.getRootElement().asXML();
	}
	
	@NotNull
	public static String get(String url){
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@NotNull
	public static String post(String url, String param) {
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(
				MediaType.parse("text/x-markdown; charset=utf-8"), param);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
