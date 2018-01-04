// http://www.tutorialspoint.com/jdbc/jdbc-quick-guide.htm

//Step 1. Import required packages
import java.sql.*;

public class FirstExample {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
	static final String DB_URL = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";

	// Database Credentials
	static final String USER = "ehauber";
	static final String PASS = "aFhZoQZX";
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			//Step 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//Step 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//Step 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "Select * from school";
			ResultSet rs = stmt.executeQuery(sql);
			
			//Step 5: Extract data from result set
			while(rs.next()) {
				int schoolid = rs.getInt("schoolid");
				String schoolname = rs.getString("schoolname");
				String address = rs.getString("address");
				System.out.print("SCHOOLID: " + schoolid);
				System.out.print(", SCHOOLNAME: " + schoolname);
				System.out.println(", ADDRESS: " + address);
			}
			
			//Step 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se) {
			//Handle errors for JDBC_DRIVER
			se.printStackTrace();
		}catch(Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null) {
					stmt.close();
				}
			}catch(SQLException se2) {
			}// nothing we can do
			try{
				if(conn!=null) {
					conn.close();
				}
			}catch(SQLException se) {
				se.printStackTrace();
			}//end finally try
		}//end try
		System.out.println("Goodbye!");
	}//end main
}//end FirstExample
