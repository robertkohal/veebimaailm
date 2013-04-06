package ee.veebimaailm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import ee.veebimaailm.data.LoginResponse;
import ee.veebimaailm.data.VoteAction;
import ee.veebimaailm.data.UserOperationResponse;
import ee.veebimaailm.db.DataFetcher;
import ee.veebimaailm.db.DataModifier;

/**
 * Servlet implementation class Vote
 */
@WebServlet("/server/private/Vote")
public class Vote extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Vote() {
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
		UserOperationResponse voteresponse = new UserOperationResponse();
		
		if (request.getMethod().equals("GET")) {
			DataFetcher datafetcher;
			Boolean isVoted = false;
			Long timestamp;
			try {
				datafetcher = new DataFetcher(getServletContext());
				isVoted = datafetcher.isVotedByPerson(person_id);
				if (isVoted.booleanValue()) {
					voteresponse.setResult("alreadyVoted");
					datafetcher = new DataFetcher(getServletContext());
					timestamp = datafetcher.getVoteTimeStamp(person_id);
					voteresponse.setTimestamp(timestamp);
				} else {
					voteresponse.setResult("NotVoted");
				}
			} catch (SQLException | NamingException e) {
				e.printStackTrace();
			}
			response.getWriter().write(gson.toJson(voteresponse));
			return;
		}
		VoteAction actionrequest = gson.fromJson(request.getReader(), VoteAction.class);
		Integer candidate_id = actionrequest.getCandidate_id();
		String action = actionrequest.getAction();
		
		
		if (action.equals("vote")) {
			try {
				
				DataFetcher datafetcher = new DataFetcher(getServletContext());
				
				Boolean isVoted = datafetcher.isVotedByPerson(person_id);
				if (isVoted.booleanValue()) {
					voteresponse.setResult("alreadyVoted");
				} else {
					Date vote_time = new Date();
					long timestampToBrowser = vote_time.getTime();
					
					SimpleDateFormat mysqlTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String timestampToMysql = mysqlTimeFormat.format(vote_time);
					DataModifier datainserter = new DataModifier(getServletContext());
					
					int affectedrows = datainserter.insertVote(person_id,candidate_id, timestampToMysql);
					if (affectedrows==1) {
						voteresponse.setResult("success");
						voteresponse.setTimestamp(timestampToBrowser);
					} else {
						voteresponse.setResult("failed");
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
				int affectedrows = datainserter.deleteVote(person_id);
				if (affectedrows==1) {
					voteresponse.setResult("success");
				} else {
					voteresponse.setResult("alreadyDeleted");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.getWriter().write(gson.toJson(voteresponse));
		return;
	}
}
