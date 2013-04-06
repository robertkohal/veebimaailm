package ee.veebimaailm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ee.veebimaailm.data.CandidateList;
import ee.veebimaailm.data.PartyList;
import ee.veebimaailm.db.DataFetcher;

/**
 * Servlet implementation class GetCandidates
 */
@WebServlet("/server/GetVotes")
public class GetVotes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetVotes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String party_id = request.getParameter("party_id");
		String region_id = request.getParameter("region_id");
		String letters = request.getParameter("letters");	
		String jsonresponse;
		DataFetcher datafetcher = null;
		
		@SuppressWarnings("rawtypes")
		ArrayList voteslist = null;
		
		Object list = null;
		
		Gson gson = new GsonBuilder().create();

		try {
			datafetcher = new DataFetcher(getServletContext());
			if (region_id==null && party_id==null && letters==null) {
				voteslist = datafetcher.getVotesByCountry();
				list = new PartyList(voteslist);
			} else if (region_id!=null && party_id!=null) {
				voteslist = datafetcher.getVotesByPartyAndRegion(party_id,region_id);
				list = new CandidateList(voteslist);
			} else if (region_id!=null) {
				voteslist = datafetcher.getVotesByRegion(region_id);
				list = new PartyList(voteslist);
			} else if (party_id!=null) {
				voteslist = datafetcher.getVotesByParty(party_id);
				list = new CandidateList(voteslist);
			} else if (letters!=null) {
				voteslist = datafetcher.getVotesByLetters(letters);
				list = new CandidateList(voteslist);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonresponse = gson.toJson(list);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(jsonresponse);
	}
	

}
