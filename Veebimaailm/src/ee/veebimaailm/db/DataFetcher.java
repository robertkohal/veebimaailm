package ee.veebimaailm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import ee.veebimaailm.data.Candidate;
import ee.veebimaailm.data.Party;
import ee.veebimaailm.data.Region;

public class DataFetcher {
	
	private Statement dbstatement;
	private PreparedStatement predbstatement;
	private ResultSet dbresultset;
	private Connection dbconnection;
	
	public DataFetcher(ServletContext context) throws SQLException, NamingException {
		dbconnection = ((DataSource)context.getAttribute("datasource")).getConnection();
		dbstatement = null;
		predbstatement = null;
		dbresultset = null;
	}
	public ArrayList<Region> getRegions() throws SQLException {
		ArrayList<Region> regionList = new ArrayList<Region>();
		
		dbstatement = dbconnection.createStatement();
		dbresultset = dbstatement.executeQuery(Queries.getAllFromRegion);
		while (dbresultset.next()) {
			Region selectedRegion = new Region(dbresultset.getInt("id_region"),
											   dbresultset.getString("name")); 
			regionList.add(selectedRegion);
        }
		close();
		return regionList;
	}
	public ArrayList<Party> getPartys() throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		dbstatement = dbconnection.createStatement();
		dbresultset = dbstatement.executeQuery(Queries.getAllFromParty);
		
		while (dbresultset.next()) {
			Party selectedParty = new Party(dbresultset.getInt("id_party"),
											dbresultset.getString("name")); 
			partyList.add(selectedParty);
        }
		close();
		return partyList;
	}
	public ArrayList<Candidate> getCandidatesByLetters(String letters) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getCandidatesByLetters);
		predbstatement.setString(1, letters+"%");
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}

	public ArrayList<Candidate> getCandidatesByPartyAndRegion(String id_party,String id_region) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getCandidatesByPartyAndRegion);
		predbstatement.setString(1, id_party);
		predbstatement.setString(2, id_region);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getCandidatesByParty(String id_party) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getCandidatesByParty);
		predbstatement.setString(1, id_party);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getCandidatesByRegion(String id_region) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getCandidatesByRegion);
		predbstatement.setString(1, id_region);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Party> getVotesByCountry() throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		dbstatement = dbconnection.createStatement();
		dbresultset = dbstatement.executeQuery(Queries.getVotesByCountry);
		
		fillPartyList(partyList);
		close();
		return partyList;
		
	}
	public ArrayList<Party> getVotesByRegion(String region_id) throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getVotesByRegion);
		predbstatement.setString(1, region_id);
		
		dbresultset = predbstatement.executeQuery();
		
		fillPartyList(partyList);
		close();
		return partyList;

	}
	
	public ArrayList<Candidate> getVotesByParty(String party_id) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getVotesByParty);
		predbstatement.setString(1, party_id);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getVotesByCandidate(String candidate_id) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(Queries.getVotesByCandidate);
		predbstatement.setString(1, candidate_id);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithVotes(candidateList);
		close();
		return candidateList;
	}
	private void fillCandidateListWithVotes(ArrayList<Candidate> candidateList)
			throws SQLException {
		while (dbresultset.next()) {
			Candidate selectedCandidate = new Candidate(dbresultset.getString("person_name"),
														dbresultset.getLong("votes")); 
			candidateList.add(selectedCandidate);
        }
	}
	private void fillCandidateListWithoutVotes(ArrayList<Candidate> candidateList) throws SQLException {
		while (dbresultset.next()) {
			Candidate selectedCandidate = new Candidate(dbresultset.getInt("id_candidate"),
														dbresultset.getString("person_name"),
														dbresultset.getString("party_name"),
														dbresultset.getString("region_name")); 
			candidateList.add(selectedCandidate);
        }
	}
	
	private void fillPartyList(ArrayList<Party> partyList) throws SQLException {
		while (dbresultset.next()) {
			Party selectedParty = new Party(dbresultset.getString("party_name"),
											dbresultset.getLong("votes")); 
			partyList.add(selectedParty);
        }
	}
	private void close() throws SQLException {
		dbresultset.close();
		if (dbstatement!=null) {
			dbstatement.close();
		} else if (predbstatement!=null) {
			predbstatement.close();
		}
		dbconnection.close();
	}
	
}
