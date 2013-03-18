package ee.veebimaailm.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import ee.veebimaailm.data.Party;
import ee.veebimaailm.data.Region;

public class DataFetcher {
	
	private Statement dbstatement;
	private ResultSet dbresultset;
	private Connection dbconnection;
	
	public DataFetcher(ServletContext context) throws SQLException, NamingException {
		dbconnection = ((DataSource)context.getAttribute("datasource")).getConnection();
		dbstatement = null;
		dbresultset = null;
	}
	public ArrayList<Region> getRegions() throws SQLException {
		ArrayList<Region> regionList = new ArrayList<Region>();

		dbstatement = dbconnection.createStatement();
		dbresultset = dbstatement.executeQuery("SELECT * FROM region");
		while (dbresultset.next()) {
			Region selectedRegion = new Region(dbresultset.getInt("id_region"),
											   dbresultset.getString("name")); 
			regionList.add(selectedRegion);
        }
		this.close();
		return regionList;
	}
	public ArrayList<Party> getPartys() throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		dbstatement = dbconnection.createStatement();
		dbresultset = dbstatement.executeQuery("SELECT * FROM party");
		while (dbresultset.next()) {
			Party selectedParty = new Party(dbresultset.getInt("id_party"),
											dbresultset.getString("name")); 
			partyList.add(selectedParty);
        }
		this.close();
		return partyList;
	}
	
	private void close() throws SQLException {
		dbresultset.close();
		dbstatement.close();
		dbconnection.close();
	}
}
