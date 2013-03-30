package ee.veebimaailm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

public class DataModifier {

	private PreparedStatement predbstatement;
	private Connection dbconnection;
	
	public DataModifier(ServletContext context) throws SQLException, NamingException {
		dbconnection = ((DataSource)context.getAttribute("datasource")).getConnection();
		predbstatement = null;
	}
	public int insertVote(Integer person_id,Integer candidate_id, String datetime) throws SQLException {
		predbstatement = dbconnection.prepareStatement(ModifierQueries.insertVote);
		predbstatement.setInt(1, candidate_id);
		predbstatement.setInt(2, person_id);
		predbstatement.setString(3, datetime);
		
		int affectedrows = predbstatement.executeUpdate();
		close();
		return affectedrows;
	}
	public int deleteVote(Integer person_id) throws SQLException {
		predbstatement = dbconnection.prepareStatement(ModifierQueries.deleteVote);
		predbstatement.setInt(1, person_id);
		
		int affectedrows = predbstatement.executeUpdate();
		close();
		return affectedrows;
	}
	public int insertPerson(String first_name,String last_name,String saltedpassword, String salt) throws SQLException {
		predbstatement = dbconnection.prepareStatement(ModifierQueries.insertPerson, Statement.RETURN_GENERATED_KEYS);
		predbstatement.setString(1, first_name);
		predbstatement.setString(2, last_name);
		predbstatement.setString(3, saltedpassword);
		predbstatement.setString(4, salt);
		predbstatement.executeUpdate();
		
		ResultSet resultset = predbstatement.getGeneratedKeys();
		resultset.next();
		int id_person = resultset.getInt(1);
		close();
		return id_person;
	}
	
	public int insertCandidate(Integer person_id, Integer party_id, Integer region_id) throws SQLException {
		predbstatement = dbconnection.prepareStatement(ModifierQueries.insertCandidate);
		predbstatement.setInt(1, party_id);
		predbstatement.setInt(2, region_id);
		predbstatement.setInt(3, person_id);
		
		int affectedrows = predbstatement.executeUpdate();
		close();
		return affectedrows;
	}
	public int deleteCandidate(Integer person_id) throws SQLException {
		predbstatement = dbconnection.prepareStatement(ModifierQueries.deleteCandidate);
		predbstatement.setInt(1, person_id);
		
		int affectedrows = predbstatement.executeUpdate();
		close();
		return affectedrows;
	}
	
	private void close() throws SQLException {
		predbstatement.close();
		dbconnection.close();
	}
	
}
