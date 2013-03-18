package ee.veebimaailm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import ee.veebimaailm.data.Party;
import ee.veebimaailm.data.PartyList;
import ee.veebimaailm.db.DataFetcher;

/**
 * Servlet implementation class GetPartys
 */
@WebServlet("/server/GetPartys")
public class GetPartys extends HttpServlet {
	
	private String jsonresponse;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPartys() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		DataFetcher datafetcher = null;
		ArrayList<Party> partyList = null;
		Gson gson = new Gson();
		try {
			datafetcher = new DataFetcher(getServletContext());
			partyList = datafetcher.getPartys();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PartyList list = new PartyList(partyList);
        jsonresponse = gson.toJson(list);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(jsonresponse);
	}

}
