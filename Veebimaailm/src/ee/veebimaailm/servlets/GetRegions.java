package ee.veebimaailm.servlets;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import ee.veebimaailm.data.Region;
import ee.veebimaailm.data.RegionList;
import ee.veebimaailm.db.DataFetcher;


/**
 * Servlet implementation class First_test
 */
@WebServlet("/server/GetRegions")
public class GetRegions extends HttpServlet {

	private String jsonresponse;
	private static final long serialVersionUID = 1L;
  
    /**
     * @see HttpServlet#HttpServlet()
     */
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		
		//String initial = config.getInitParameter("initial");
		DataFetcher datafetcher = null;
		ArrayList<Region> regionList = null;
		Gson gson = new Gson();
		try {
			datafetcher = new DataFetcher(getServletContext());
			regionList = datafetcher.getRegions();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RegionList list = new RegionList(regionList);
        jsonresponse = gson.toJson(list);
	    
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(jsonresponse);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//}

}
