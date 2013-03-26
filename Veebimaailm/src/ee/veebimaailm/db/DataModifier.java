package ee.veebimaailm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	
	private void close() throws SQLException {
		predbstatement.close();
		dbconnection.close();
	}
}
