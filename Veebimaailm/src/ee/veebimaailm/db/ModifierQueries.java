package ee.veebimaailm.db;

public class ModifierQueries {
	final protected static String insertVote = "INSERT INTO vote(id_candidate,id_person,vote_time) VALUES (?,?,?)";
	final protected static String deleteVote = "DELETE FROM vote WHERE id_person=?";
}
