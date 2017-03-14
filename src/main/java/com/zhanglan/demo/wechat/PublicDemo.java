package com.zhanglan.demo.wechat;

import com.qq.weixin.mp.aes.SHA1;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/PublicDemo")
public class PublicDemo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PublicDemo() {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setContentType("text/html;charset=utf-8");
		res.setCharacterEncoding("UTF-8");
		boolean isGet = req.getMethod().toLowerCase().equals("get");
		if (isGet) {
			// 验证url
			String signature = req.getParameter("signature");// 微信加密签名
			String timestamp = req.getParameter("timestamp");// 时间戳
			String nonce = req.getParameter("nonce");// 随机数
			String echostr = req.getParameter("echostr");// 随机字符串
			PrintWriter out = res.getWriter();
			if (access(Constant.Token, timestamp, nonce, signature)) {
				out.write(echostr);
			} else {
				out.write("false");
			}
			out.flush();
		} else {
			// 消息处理
			receiveMsg(req, res);
		}

	}

	/**
	 * 验证url
	 * 
	 * @param tokon
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @return
	 */
	public boolean access(String tokon, String timestamp, String nonce,
			String signature) {
		try {
			List<String> params = new ArrayList<String>();
			params.add(tokon);
			params.add(timestamp);
			params.add(nonce);
			//将token、timestamp、nonce三个参数进行字典序排序
			Collections.sort(params, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			//排序后连接字符串，进行sha1加密
			String temp = SHA1.encode(params.get(0) + params.get(1)
					+ params.get(2));
			//比较加密后的字符串与加密签名，相同则验证成功
			if (temp.equals(signature)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 接收消息，进行后续处理
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	public void receiveMsg(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		ServletInputStream in = req.getInputStream();
		
		String xmlMsg = Tools.inputStream2String(in);// 获取接收的数据包，解析成xml字符串
		
		Map<String, String> inMap = Tools.parseXml(xmlMsg);// 将xml转换为map
		
		String msgType = inMap.get("MsgType");// 接收的消息类型
		
		Map<String, String> reply = new HashMap<String, String>();// reply用来组织返回的数据，以达到自动回复的目的
		reply.put("ToUserName", inMap.get("FromUserName"));
		reply.put("FromUserName", inMap.get("ToUserName"));
		reply.put("CreateTime", new Date().getTime() + "");
		reply.put("MsgType", "text");
		
		PrintWriter out = res.getWriter();
		String outMsg = "";
		if (msgType.equals("text")) {// text表示接收的是文本消息
			if ("联系方式".equals(inMap.get("Content"))) {
				reply.put("Content",
						"电话:15668380050\nQQ:474529814\n邮箱:zhang_lan@inspur.com");
				outMsg = Tools.generatorXml(reply);
			} else if("图文消息".equals(inMap.get("Content"))){
				outMsg = getArticles(inMap);
			}else {
				reply.put("Content", xmlMsg);
				outMsg = Tools.generatorXml(reply);
			}
		} else if (msgType.equals("event")) {// event表示接收的是推送事件
//			if (inMap.get("Event").equals("subscribe")) {// 该推送事件为关注事件
//				reply.put("Content", "欢迎订阅我的公众号");
//				outMsg = Tools.generatorXml(reply);
//			} else {
				reply.put("Content", xmlMsg);
				outMsg = Tools.generatorXml(reply);
//			}
		} else {
			reply.put("Content", xmlMsg);
			outMsg = Tools.generatorXml(reply);
		}
		out.write(outMsg);
		out.flush();
	}
	/**
	 * 回复图文消息的消息结构
	 * @param inMap
	 * @return
	 */
	public String getArticles(Map<String, String> inMap){
		return "<xml>" +
				"<ToUserName>" +
				"<![CDATA[" + inMap.get("FromUserName")+
				"]]>" +
				"</ToUserName>" +
				"<FromUserName><![CDATA[" +inMap.get("ToUserName")+
				"]]></FromUserName>" +
				"<CreateTime>" +new Date().getTime()+
				"</CreateTime>" +
				"<MsgType><![CDATA[news]]></MsgType>" +
				"<ArticleCount>2</ArticleCount>" +
				"<Articles>" +
				"<item>" +
				"<Title><![CDATA[" +"标题1"+
				"]]></Title>" +
				"<Description><![CDATA[" +"描述1"+
				"]]></Description>" +
				"<PicUrl><![CDATA[http://10.10.10.67/DocCenterService/pubDoc?doc_id=2946]]></PicUrl>" +
				"<Url><![CDATA[http://www.baidu.com]]></Url>" +
				"</item>" +
				"<item>" +
				"<Title><![CDATA[" +"标题2"+
				"]]></Title>" +
				"<Description><![CDATA[" +"描述2"+
				"]]></Description>" +
				"<PicUrl><![CDATA[http://10.10.10.67/DocCenterService/pubDoc?doc_id=2946]]></PicUrl>" +
				"<Url><![CDATA[http://www.baidu.com]]></Url>" +
				"</item>" +
				"</Articles></xml>";
	}
	

}
