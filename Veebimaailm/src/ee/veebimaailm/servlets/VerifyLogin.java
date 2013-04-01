package ee.veebimaailm.servlets;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.UUID;

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
@WebServlet("/server/VerifyLogin")
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
		String key = request.getParameter("key");
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password");
		DataFetcher datafetcher = null;
		
		if (logout!=null) {
			return;
		}
		if (username!=null) {
			if (!username.contains(" ")) {
				loginresponse = new LoginResponse("fail","Username must contain space.",25);
				response.getWriter().write(gson.toJson(loginresponse));
				return;
			}
		}
		if (key!=null) {
			if (key.length()!=0 && key.equals("somerandomworD633")) {
				if (username!=null && password!=null) {
					
					
					if (username.length()<8 || password.length()<8) {
						loginresponse = new LoginResponse("fail","Username or password too short.",22);
						response.getWriter().write(gson.toJson(loginresponse));
						return;
					} 
					try {
						datafetcher = new DataFetcher(getServletContext());
						boolean isNameDublicate = datafetcher.isNameDublicated(username);
						if (isNameDublicate) {
							loginresponse = new LoginResponse("fail","Name already exists",34);
							response.getWriter().write(gson.toJson(loginresponse));
							return;
						}
						DataModifier datainserter = new DataModifier(getServletContext());
						
						String last_name = username.substring(username.lastIndexOf(' ')+1);
						String first_name = username.substring(0, username.lastIndexOf(' '));
						String salt = generateRandomSalt();
						String saltedpassword = password+salt;
						
						int id_person = datainserter.insertPerson(first_name, last_name, saltedpassword, salt);
						createSession(session, last_name, first_name, id_person, request);
						loginresponse = new LoginResponse("success",first_name+" "+last_name);
						
						response.getWriter().write(gson.toJson(loginresponse));
					} catch (SQLException | NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				loginresponse = new LoginResponse("fail","Invalide registry key",22);
				response.getWriter().write(gson.toJson(loginresponse));
				return;
			}
		} else {
			if (username!=null && password!=null) {
				String last_name = username.substring(username.lastIndexOf(' ')+1);
				String first_name = username.substring(0, username.lastIndexOf(' '));
				
				try {
					datafetcher = new DataFetcher(getServletContext());
					boolean passwordCorrect = datafetcher.checkPassword(password, last_name, first_name);
					if (passwordCorrect) {
						int id_person = datafetcher.getIDPersonByName(first_name, last_name);
						createSession(session, last_name, first_name, id_person, request);
						
						loginresponse = new LoginResponse("success",first_name+" "+last_name);
						response.getWriter().write(gson.toJson(loginresponse));
					} else {
						loginresponse = new LoginResponse("fail","Username or password incorrect.",45);
						response.getWriter().write(gson.toJson(loginresponse));
					}
				} catch (SQLException | NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				loginresponse = new LoginResponse("fail","Invalide input.",45);
				response.getWriter().write(gson.toJson(loginresponse));
			}
		}
	}

	private void createSession(HttpSession session, String last_name,
			String first_name, int id_person, HttpServletRequest request) throws SQLException, NamingException {
		session = request.getSession();
		UserProfile user = new UserProfile(id_person,first_name+" "+last_name);
		session.setAttribute("login", user);
	}
	public String generateRandomSalt() {
		SecureRandom random = new SecureRandom();
		StringBuffer salt = new StringBuffer();
		for (int i=0;i<32;i++) {
			char symbol = (char)(random.nextInt(93)+33);
			salt.append(symbol);
		}
		return salt.toString();
	}
}
