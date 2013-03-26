package ee.veebimaailm.db;

public class FetcherQueries {
	final protected static String getAllFromRegion = "SELECT * FROM region";
	
	final protected static String getAllFromParty = "SELECT * FROM party";
	
	final protected static String getCandidatesByLetters = "SELECT candidate.id_candidate, " +
			 											   "CONCAT_WS(' ',person.first_name, person.last_name) AS person_name," +
			 											   "party.name AS party_name, region.name AS region_name "+ 
			 											   "FROM candidate,person,party,region "+ 
			 											   "WHERE candidate.id_person=person.id_person "+ 
			 											   "AND candidate.id_party=party.id_party "+ 
			 											   "AND candidate.id_region=region.id_region "+
			 											   "AND CONCAT_WS(' ',person.last_name,person.first_name) LIKE ? "+ 
			 											   "ORDER BY candidate.id_candidate;";
	
	final protected static String getCandidatesByPartyAndRegion = "SELECT candidate.id_candidate, " +
			 												   	  "CONCAT_WS(' ',person.first_name, person.last_name) AS person_name," +
			 												   	  "party.name AS party_name, region.name AS region_name "+ 
			 												   	  "FROM candidate,person,party,region "+ 
			 												   	  "WHERE candidate.id_person=person.id_person "+ 
			 												   	  "AND candidate.id_party=party.id_party "+ 
			 												   	  "AND candidate.id_region=region.id_region "+
			 												   	  "AND candidate.id_party=? "+
			 												   	  "AND candidate.id_region=? "+
			 												   	  "ORDER BY candidate.id_candidate;";
	
	final protected static String getCandidatesByParty = "SELECT candidate.id_candidate, " +
			   										     "CONCAT_WS(' ',person.first_name, person.last_name) AS person_name," +
			   										     "party.name AS party_name, region.name AS region_name "+ 
			   										     "FROM candidate,person,party,region "+ 
			   										     "WHERE candidate.id_person=person.id_person "+ 
			   										     "AND candidate.id_party=party.id_party "+ 
			   										     "AND candidate.id_region=region.id_region "+
			   										     "AND candidate.id_party=? "+
			   										     "ORDER BY candidate.id_candidate;";
	
	final protected static String getCandidatesByRegion = "SELECT candidate.id_candidate, " +
				  									      "CONCAT_WS(' ',person.first_name, person.last_name) AS person_name," +
				  									      "party.name AS party_name, region.name AS region_name "+ 
				  									      "FROM candidate,person,party,region "+ 
				  									      "WHERE candidate.id_person=person.id_person "+ 
				  									      "AND candidate.id_party=party.id_party "+ 
				  									      "AND candidate.id_region=region.id_region "+
				  									      "AND candidate.id_region=? "+
				  									      "ORDER BY candidate.id_candidate;";
	
	final protected static String getVotesByCountry = "SELECT name AS party_name," +
													  "(SELECT votes " +
													  "FROM " +
													  "(SELECT COUNT(*) AS votes, candidate.id_party AS party " +
													  "FROM vote,candidate " +
													  "WHERE vote.id_candidate=candidate.id_candidate " +
													  "GROUP BY id_party) AS result " +
													  "WHERE party=id_party) " +
													  "AS votes FROM party ORDER BY votes DESC";
	
	final protected static String getVotesByRegion = "SELECT name AS party_name,(SELECT votes FROM " + 
													 "(SELECT COUNT(*) AS votes, candidate.id_party AS party " + 
													 "FROM vote,candidate " + 
													 "WHERE vote.id_candidate=candidate.id_candidate " + 
													 "AND candidate.id_region=? " +
													 "GROUP BY id_party) AS result " + 
													 "WHERE party=id_party) " + 
													 "AS votes FROM party ORDER BY votes DESC;";
	
	final protected static String getVotesByParty = "SELECT CONCAT_WS(' ',person.first_name, person.last_name) AS person_name, " +
													"(SELECT count(*) FROM vote " +
													"WHERE candidate.id_candidate=vote.id_candidate) AS votes " +
													"FROM candidate,person " +
													"WHERE person.id_person=candidate.id_person " +
													"AND candidate.id_party=? ORDER BY votes DESC;" ;
	
	final protected static String getVotesByCandidate = "SELECT CONCAT_WS(' ',person.first_name, person.last_name) AS person_name, " +
														"(SELECT count(*) FROM vote " +
														"WHERE candidate.id_candidate=vote.id_candidate) AS votes " +
														"FROM candidate,person " +
														"WHERE person.id_person=candidate.id_person " +
														"AND candidate.id_candidate=?" ;
	
	final protected static String isVotedByPerson = "SELECT EXISTS(SELECT 1 FROM vote WHERE id_person=?) as isVoted";
	
	final protected static String getVoteTimeStamp = "SELECT vote_time FROM vote WHERE id_person=?";

}
