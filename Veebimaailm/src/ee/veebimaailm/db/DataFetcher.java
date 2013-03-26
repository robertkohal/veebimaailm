package ee.veebimaailm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import ee.veebimaailm.data.Candidate;
import ee.veebimaailm.data.Party;
import ee.veebimaailm.data.Region;

public class DataFetcher {
	
	private PreparedStatement predbstatement;
	private ResultSet dbresultset;
	private Connection dbconnection;
	
	public DataFetcher(ServletContext context) throws SQLException, NamingException {
		dbconnection = ((DataSource)context.getAttribute("datasource")).getConnection();
		predbstatement = null;
		dbresultset = null;
	}
	public ArrayList<Region> getRegions() throws SQLException {
		ArrayList<Region> regionList = new ArrayList<Region>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getAllFromRegion);
		dbresultset = predbstatement.executeQuery();
		
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
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getAllFromParty);
		dbresultset = predbstatement.executeQuery();
		
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
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getCandidatesByLetters);
		predbstatement.setString(1, letters+"%");
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}

	public ArrayList<Candidate> getCandidatesByPartyAndRegion(String id_party,String id_region) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getCandidatesByPartyAndRegion);
		predbstatement.setString(1, id_party);
		predbstatement.setString(2, id_region);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getCandidatesByParty(String id_party) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getCandidatesByParty);
		predbstatement.setString(1, id_party);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getCandidatesByRegion(String id_region) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getCandidatesByRegion);
		predbstatement.setString(1, id_region);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithoutVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Party> getVotesByCountry() throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getVotesByCountry);
		dbresultset = predbstatement.executeQuery();
		
		fillPartyList(partyList);
		close();
		return partyList;
		
	}
	public ArrayList<Party> getVotesByRegion(String region_id) throws SQLException {
		ArrayList<Party> partyList = new ArrayList<Party>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getVotesByRegion);
		predbstatement.setString(1, region_id);
		
		dbresultset = predbstatement.executeQuery();
		
		fillPartyList(partyList);
		close();
		return partyList;

	}
	
	public ArrayList<Candidate> getVotesByParty(String party_id) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getVotesByParty);
		predbstatement.setString(1, party_id);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithVotes(candidateList);
		close();
		return candidateList;
	}
	public ArrayList<Candidate> getVotesByCandidate(String candidate_id) throws SQLException {
		ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
		
		predbstatement = dbconnection.prepareStatement(FetcherQueries.getVotesByCandidate);
		predbstatement.setString(1, candidate_id);
		
		dbresultset = predbstatement.executeQuery();
		fillCandidateListWithVotes(candidateList);
		close();
		return candidateList;
	}
	
	public boolean isVotedByPerson(Integer person_id) throws SQLException {
		predbstatement = dbconnection.prepareStatement(FetcherQueries.isVotedByPerson);
		predbstatement.setInt(1, person_id);
		
		dbresultset = predbstatement.executeQuery();
		
		dbresultset.next();
		int isVoted = dbresultset.getInt("isVoted");
		if (isVoted==0) {
			return false;
		}
		return true;
	}
	
	public Long getVoteTimeStamp(Integer person_id) throws SQLException {
		Long VoteTimeStamp;
		predbstatement = dbconnection.prepareStatement(FetcherQueries.isVotedByPerson);
		predbstatement.setInt(1, person_id);
		
		dbresultset = predbstatement.executeQuery();
		
		if (dbresultset.isBeforeFirst() ) {    
			VoteTimeStamp = new Long(-1);
		} else {
			dbresultset.next();
			VoteTimeStamp = dbresultset.getTimestamp("vote_time").getTime();
		}
		close();
		return VoteTimeStamp;
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
		predbstatement.close();
		dbconnection.close();
	}
	
}
