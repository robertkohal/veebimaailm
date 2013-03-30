package ee.veebimaailm.db;

public class ModifierQueries {
	final protected static String insertVote = "INSERT INTO vote(id_candidate,id_person,vote_time) VALUES (?,?,?)";
	final protected static String deleteVote = "DELETE FROM vote WHERE id_person=?";
	final protected static String insertPerson = "INSERT INTO person(first_name,last_name,password,salt) VALUES (?,?,SHA2(?,512),?)";
	final protected static String insertCandidate = "INSERT INTO candidate(id_party,id_region, id_person) VALUES (?,?,?)";
	final protected static String deleteCandidate = "DELETE FROM candidate WHERE id_person=?";
}
