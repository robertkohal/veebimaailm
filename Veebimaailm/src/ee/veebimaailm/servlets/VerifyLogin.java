package ee.veebimaailm.servlets;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import ee.veebimaailm.data.LoginResponse;
import ee.veebimaailm.db.DataFetcher;
import ee.veebimaailm.db.DataModifier;

/**
 * Servlet implementation class VertifyLogin
 */
@WebServlet("/server/private/VerifyLogin")
public class VerifyLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyLogin() {
        super();
        
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
    	LoginResponse loginresponse  = null;
		Gson gson = new Gson();
		
		if (session!=null) {
			UserProfile userprofile = (UserProfile) session.getAttribute("login");
			loginresponse = new LoginResponse("success",userprofile.getUsername());
			
		} else {
			loginresponse = new LoginResponse("fail","Not logged in.",34);
		}
		response.getWriter().write(gson.toJson(loginresponse));
		return;
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		
		HttpSession session = request.getSession(false);	
		LoginResponse loginresponse  = null;
		Gson gson = new Gson();
		String[] userinfo = null;
		//
		String logout = request.getParameter("logout");
		
		if (session!=null) {
			
			if (logout!=null) {
				session.invalidate();
				loginresponse = new LoginResponse("logout","Logout succesful.");
				response.getWriter().write(gson.toJson(loginresponse));
				return;
			}
			UserProfile userprofile = (UserProfile) session.getAttribute("login");
			loginresponse = new LoginResponse("success",userprofile.getUsername());
			response.getWriter().write(gson.toJson(loginresponse));
			return;
		}
		X509Certificate cert = extractCertificate(request);
		if (cert==null) {
			response.getWriter().write("Serti ei ole");
			return;
		} else {
			String certinfo = cert.getSubjectX500Principal().getName("RFC1779");
			userinfo = parseCertInfo(certinfo);
		}
		long SSN = Long.parseLong(userinfo[0]);
		String first_name = userinfo[1];
		String last_name = userinfo[2];
		String parameter_login = request.getParameter("login");
		DataFetcher datafetcher = null;
		
		if (logout!=null) {
			return;
		}
		try {
			if (parameter_login!=null) {
				datafetcher = new DataFetcher(getServletContext());
				int id_person = datafetcher.checkUserExists(SSN);
				if (id_person==-1) {
					DataModifier datainserter = new DataModifier(getServletContext());
					id_person = datainserter.insertPerson(first_name, last_name, SSN);
				}
				createSession(session, last_name, first_name, id_person, request);
				loginresponse = new LoginResponse("success",first_name+" "+last_name);
			} else {
				loginresponse = new LoginResponse("fail","Invalide input.",45);
			}
			response.getWriter().write(gson.toJson(loginresponse));
		} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}

	private void createSession(HttpSession session, String last_name,
			String first_name, int id_person, HttpServletRequest request) throws SQLException, NamingException {
		session = request.getSession();
		UserProfile user = new UserProfile(id_person,first_name+" "+last_name,0);
		session.setAttribute("login", user);
	}
	private String[] parseCertInfo(String cert) {
		String[] formated_userinfo = new String[3];
		String[] userinfo = cert.split("[=,]+");
		formated_userinfo[0] = userinfo[1];
		formated_userinfo[1] = userinfo[3];
		formated_userinfo[2] = userinfo[5];
	
		return formated_userinfo;
	}
	private X509Certificate extractCertificate(HttpServletRequest req) {
	    X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
	    if (null != certs && certs.length > 0) {
	        return certs[0];
	    }
	    return null;
	    //throw new RuntimeException("No X.509 client certificate found in request");
	}
}
