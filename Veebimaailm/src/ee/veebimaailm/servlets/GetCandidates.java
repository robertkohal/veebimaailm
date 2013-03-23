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

import ee.veebimaailm.data.Candidate;
import ee.veebimaailm.data.CandidateList;
import ee.veebimaailm.db.DataFetcher;

/**
 * Servlet implementation class GetStatistics
 */
@WebServlet("/server/GetCandidates")
public class GetCandidates extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCandidates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String party_id = request.getParameter("party_id");
		String region_id = request.getParameter("region_id");
		String name_letters = request.getParameter("letters");	
		String jsonresponse;
		DataFetcher datafetcher = null;
		ArrayList<Candidate> candidateList = null;
		
		Gson gson = new GsonBuilder().create();
		
		try {
			datafetcher = new DataFetcher(getServletContext());
			if (name_letters!=null) {
				candidateList = datafetcher.getCandidatesByLetters(name_letters);
			} else if (party_id!=null && region_id!=null) {
				candidateList = datafetcher.getCandidatesByPartyAndRegion(party_id,region_id);
			} else if (party_id!=null) {
				candidateList = datafetcher.getCandidatesByParty(party_id);
			} else if (region_id!=null) {
				candidateList = datafetcher.getCandidatesByRegion(region_id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CandidateList list = new CandidateList(candidateList);
		jsonresponse = gson.toJson(list);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(jsonresponse);
	}

}
