package com.zhanglan.demo.wechat;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * Servlet implementation class WeichatServlet
 */
@WebServlet("/WeichatServlet")
public class WeichatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeichatServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		WXBizMsgCrypt wxcpt;
		String sToken = "zhanglan123456789";
		String sCorpID = "wx855fe578f9caf4a8";
		String sEncodingAESKey = "KtAiqnaIbNNVzr3hbPGq2xu80uEkwmkxE3CFXE1hAdA";
		
		
		String sEchoStr="";
		String sVerifyMsgSig ="";
		String sVerifyTimeStamp ="";
		String sVerifyNonce = "";
		String sVerifyEchoStr = "";
		
		String sReqData = "";
		try {
			sVerifyMsgSig = URLDecoder.decode(req.getParameter("msg_signature"),"utf-8");
			sVerifyTimeStamp = URLDecoder.decode(req.getParameter("timestamp"),"utf-8");
			sVerifyNonce = URLDecoder.decode(req.getParameter("nonce"),"utf-8");
			sVerifyEchoStr = URLDecoder.decode(req.getParameter("echostr"),"utf-8");
			wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
			sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,
					sVerifyNonce, sVerifyEchoStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println(sEchoStr);
	}

}
