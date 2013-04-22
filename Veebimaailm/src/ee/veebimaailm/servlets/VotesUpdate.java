package ee.veebimaailm.servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

/**
 * Servlet implementation class WebSocketExample
 */
@WebServlet("/server/VotesUpdate")
public class VotesUpdate extends WebSocketServlet {
	 private static final long serialVersionUID = 1L;
	 private static ArrayList<MyMessageInbound> mmiList = new ArrayList<MyMessageInbound>();
	
	 @Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		 return new MyMessageInbound();
	}
		
	 
	 private class MyMessageInbound extends MessageInbound{
	 WsOutbound myoutbound;
	  
	 @Override
	 public void onOpen(WsOutbound outbound){
		 try {
			 this.myoutbound = outbound;
			 mmiList.add(this);
			 outbound.writeTextMessage(CharBuffer.wrap("Hello!"));
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }
	  
	 @Override
	 public void onClose(int status){
		 mmiList.remove(this);
	 }
	  
	 @Override
	 public void onTextMessage(CharBuffer cb) throws IOException{
		 System.out.println("Accept Message : "+ cb);
	 }
	  
	 @Override
	 public void onBinaryMessage(ByteBuffer bb) throws IOException{}
	 }
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 HttpSession session = request.getSession(false);
		 if (session==null) {
			 response.getWriter().write("session puudu");
			 return;
		 }
		 for(MyMessageInbound mmib: mmiList){
			 CharBuffer buffer = CharBuffer.wrap("update");
			 mmib.myoutbound.writeTextMessage(buffer);
			 mmib.myoutbound.flush();
	 	 }
	 }
}