package ee.veebimaailm.servlets;

import java.io.IOException;
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
import ee.veebimaailm.data.NominateAction;
import ee.veebimaailm.data.UserOperationResponse;
import ee.veebimaailm.db.DataFetcher;
import ee.veebimaailm.db.DataModifier;

/**
 * Servlet implementation class Nominate
 */
@WebServlet("/server/private/Nominate")
public class Nominate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Nominate() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request,response);
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new Gson();
		
		HttpSession session = request.getSession(false);
		
		if (session==null) {
			LoginResponse loginresponse = new LoginResponse("fail","Only authorized users.");
			response.getWriter().write(gson.toJson(loginresponse));
			return;
		}
		
		UserProfile userprofile = (UserProfile) session.getAttribute("login");
		Integer person_id = userprofile.getId_person();
		UserOperationResponse nominateresponse = new UserOperationResponse();
		
		if (request.getMethod().equals("GET")) {
			System.out.println("siin");
			DataFetcher datafetcher;
			Boolean isNominated = false;
			try {
				datafetcher = new DataFetcher(getServletContext());
				isNominated = datafetcher.isNominatedByPerson(person_id);
				if (isNominated.booleanValue()) {
					nominateresponse.setResult("alreadyNominated");
					datafetcher = new DataFetcher(getServletContext());
					String[] partyAndregion = datafetcher.getPartyAndRegionByPerson(person_id);
					nominateresponse.setParty(partyAndregion[0]);
					nominateresponse.setRegion(partyAndregion[1]);
				} else {
					nominateresponse.setResult("NotNominated");
				}
			} catch (SQLException | NamingException e) {
				e.printStackTrace();
			}
			response.getWriter().write(gson.toJson(nominateresponse));
			return;
		}
		
		NominateAction actionrequest = gson.fromJson(request.getReader(), NominateAction.class);
		Integer party_id = actionrequest.getParty_id();
		Integer region_id = actionrequest.getRegion_id();
		String action = actionrequest.getAction();
		
		if (action.equals("nominate")) {
			try {
				
				DataFetcher datafetcher = new DataFetcher(getServletContext());
				
				Boolean isNominated = datafetcher.isNominatedByPerson(person_id);
				if (isNominated.booleanValue()) {
					nominateresponse.setResult("alreadyNominated");
				} else {
					
					DataModifier datainserter = new DataModifier(getServletContext());
					
					int affectedrows = datainserter.insertCandidate(person_id,party_id,region_id);
					if (affectedrows==1) {
						nominateresponse.setResult("success");
					} else {
						nominateresponse.setResult("failed");
					}
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (action.equals("cancel")) {
			try {
				DataModifier datainserter = new DataModifier(getServletContext());
				int affectedrows = datainserter.deleteCandidate(person_id);
				if (affectedrows==1) {
					nominateresponse.setResult("success");
				} else {
					nominateresponse.setResult("alreadyDeleted");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.getWriter().write(gson.toJson(nominateresponse));
		return;
	}

}
