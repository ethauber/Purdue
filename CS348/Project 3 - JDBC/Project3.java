//Step 1. Import required packages
import java.sql.*;
import java.io.*;

import java.util.*;
import java.util.regex.*;

//javac -cp .:ojdbc8.jar Project3.java
//java -cp .:ojdbc8.jar Project3 sample/input.txt

public class Project3 {
		// JDBC driver name and database URL
		static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
		static final String DB_URL = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";

		// Database Credentials
		static final String USER = "ehauber";
		static final String PASS = "aFhZoQZX";

		// Database connection
		static Connection conn = null;

		// Database statement var
		static Statement stmt = null;

		// Current Session for User
		static String currentUser = null;

		// Regex pattern to extract information between single quotes instead of parseing by commas because the addresss may have a comma
		private static final Pattern TAG_REGEX = Pattern.compile("'(.+?)'");
		
		// Global vars for autokey cipher
		final static String alphaLower = "abcdefghijklmnopqrstuvwxyz";
		final static String alphaUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public static void main(String[] args) {
				if (args.length < 2) {
					System.out.println("Usage: java -cp .:ojdbc8.jar Project3 input.txt output.txt");
					return;
				}
				
				//infile at zero and outfile at one
				//System.out.printf("\nInputFile is: %s and OutputFile is: %s\n", args[0], args[1]);
				//open file and parse by one space and newlines
				try {
					//input file setup
					FileInputStream fstream = new FileInputStream(args[0]);
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					
					//output file setup
					PrintStream out = new PrintStream(new FileOutputStream(args[1]));
					System.setOut(out);

					//jdbc setup
					//STEP 2: Register JDBC driver
					Class.forName(JDBC_DRIVER);

					//STEP 3: Open a connection
					//System.out.println("Connecting to a selected database...");
					conn = DriverManager.getConnection(DB_URL, USER, PASS);
					//System.out.println("Connected database successfully...");


					String strLine;
					int i = 1; //defatult number of commands ran to 1
					while ((strLine = br.readLine()) != null) {
							String[] tokens = strLine.split("\\s+(?![^\\(]*\\))");

							//outputting the command to execute
							System.out.printf("%d: %s\n",i, strLine);

							/*for (int j = 0; j < tokens.length; j++) {
							  System.out.printf(" %s", tokens[j]);
							  }
							  System.out.println();*/

							//choosing the method to use from input line
							switch (tokens[0]) {
									case "QUIT":
											//make program gracefully quit
											//System.out.println("QUIT output");
											//System.out.println();
											//clean up environment
											br.close();
											in.close();
											fstream.close();
											stmt.close();
											conn.close();
											//make program return
											return;
											//break;
									case "LOGIN":
											Login(tokens);
											System.out.println();
											break;
									case "SELECT":
											if (!tokens[1].equals("*")) {
												System.out.println("SELECT functionality not supported. This program only supports \"SELECT *\"");
											}
											else {
												Select(tokens);
											}
											System.out.println();
											break;
									case "CREATE":
											switch (tokens[1]) {
													case "ROLE":
															if (currentUser.equals("admin")) {
																CreateRole(tokens);
																System.out.println("Role created successfully");
																System.out.println();
															}
															else {
																System.out.println("Authorization failure");
																System.out.println();
															}
															break;
													case "USER":
															if (currentUser.equals("admin")) {
																CreateUser(tokens);
																System.out.println("User created successfully");
																System.out.println();
															}
															else {
																System.out.println("Authorization failure");
																System.out.println();
															}
															break;
											}
											break;
									case "GRANT":
											switch (tokens[1]) {
													case "ROLE":
															if (currentUser.equals("admin")) {
																GrantRole(tokens);
																System.out.println("Role assigned successfully");
																System.out.println();
															}
															else {
																System.out.println("Authorization failure");
																System.out.println();
															}
															break;
													case "PRIVILEGE":
															if (currentUser.equals("admin")) {
																GrantPrivilege(tokens);
																System.out.println("Privilege granted successfully");
																System.out.println();
															}
															else {
																System.out.println("Authorization failure");
																System.out.println();
															}
															break;
											}
											break;
									case "REVOKE":
											if (tokens[1].equals("PRIVILEGE")) {
												if (currentUser.equals("admin")) {
													RevokePrivilege(tokens);
													System.out.println("Privilege revoked successfully");
													System.out.println();
												}
												else {
													System.out.println("Authorization failure");
													System.out.println();
												}
											}
											break;
									case "INSERT":
											if (tokens[1].equals("INTO")) {
												Insert(tokens);
											}
											else {
												System.out.println("Insert improperly formatted. Usage: \"INSERT INTO\"");
											}
											break;
							}
							i++; //increment number of commands ran
					}
					// clean up file environment
					br.close();
					in.close();
					fstream.close();
					// clean up jdbc environment
					//rs.close();
					stmt.close();
					conn.close();
			}catch (SQLException se) {
					// Handle errors for JDBC
					se.printStackTrace();
			}catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
			}finally {
					//finally block used to close resources
					try {
						if (stmt != null) stmt.close();
					}catch (SQLException se2) {
					}// do nothing
					try {
						if (conn != null) conn.close();
					} catch (SQLException se) {
						se.printStackTrace();
					}
			}// end finally try
		}//end main

	//encrypt and decrypt
    public static String BuildNewKey(String text, String key) {
        String newKey = key;
        int tLength = text.length();
        for (int i = 0; i < tLength; i++) {
            //only add alpha upper and lower chars to key
            char charT = text.charAt(i);
            if (charT < 65 || (charT > 90 && charT < 97) || charT > 122) {
                continue;
            }
            else {
                newKey += charT;
            }
        }
        //System.out.println("new key is " + newKey);
        return newKey;
    }// End of BuildNewKey
    
    public static String Encrypt(String plainText, String key) {
        int ptLength = plainText.length();
        int klength = key.length();
        String cipherText = "";
        String newKey = BuildNewKey(plainText, key);
        //ensure key is the same length as the plain text
        newKey = newKey.substring(0, newKey.length()-klength);
        //System.out.println("newkey after substring is " + newKey);
        
        int j = 0; //to keep track of where we are in the new key text
        char charNK;
        for (int i = 0; i < ptLength; i++) {
            char charPT = plainText.charAt(i);
            //preserve plainText case
            //do nothing if plainText ascii value is not a-z A-Z
            if (charPT < 65 || (charPT > 90 && charPT < 97) || charPT > 122) {
                cipherText += charPT;
            }
            //if capital encrypt with alphaUpper
            else if (charPT >= 65 && charPT <= 90) {
                charNK = newKey.charAt(j);
                //make key's char uppercase
                charNK = Character.toUpperCase(charNK);
                
                //make both chars zero based from 65
                int numPT = (int)charPT - 65;
                int numNK = (int)charNK - 65;
                int charEnc = (numPT + numNK) % 26;
                cipherText += alphaUpper.charAt(charEnc);
                j++; //increment j since we used a char from the new key text
            }
            //if lower encrypt with alphaLower
            else if(charPT >= 97 && charPT <= 122) {
                charNK = newKey.charAt(j);
                //make key's char to lowercase
                charNK = Character.toLowerCase(charNK);
                
                //make both chars zero base from 97
                int numPT = (int)charPT - 97;
                int numNK = (int)charNK - 97;
                int charEnc = (numPT + numNK) % 26;
                cipherText += alphaLower.charAt(charEnc);
                j++;
            }
            else {
                System.out.println("Something bad happened in encrypt!");   
            }
        }
        //System.out.println();
        return cipherText;
    }// End of Encrypt
    
    public static String Decrypt(String cipherText, String key) {
        int ctLength = cipherText.length();
        int klength = key.length();
        String plainText = "";
        String newKey = key;
        //ensure key is the same length as the cipher text
        if (newKey.length() > ctLength) newKey = newKey.substring(0, ctLength);
        
        //newKey = newKey.substring(0, newKey.length()-klength);
        //System.out.println("newkey after substring is " + newKey);
        
        int j = 0; //to keep track of where we are in the new key text
        char charNK;
        for (int i = 0; i < ctLength; i++) {
            char charCT = cipherText.charAt(i);
            //preserve plainText case
            //do nothing if plainText ascii value is not a-z A-Z
            if (charCT < 65 || (charCT > 90 && charCT < 97) || charCT > 122) {
                plainText += charCT;
            }
            //if capital encrypt with alphaUpper
            else if (charCT >= 65 && charCT <= 90) {
                charNK = newKey.charAt(j);
                //make key's char uppercase
                charNK = Character.toUpperCase(charNK);
                
                //make both chars zero based from 65
                int numCT = (int)charCT - 65;
                int numNK = (int)charNK - 65;
                int charEnc = (numCT - numNK) % 26;
                charEnc = (charEnc < 0)? charEnc + 26 : charEnc;
                plainText += alphaUpper.charAt(charEnc);
                newKey += alphaUpper.charAt(charEnc);
                j++; //increment j since we used a char from the new key text
            }
            //if lower encrypt with alphaLower
            else if(charCT >= 97 && charCT <= 122) {
                charNK = newKey.charAt(j);
                //make key's char to lowercase
                charNK = Character.toLowerCase(charNK);
                
                //make both chars zero base from 97
                int numCT = (int)charCT - 97;
                int numNK = (int)charNK - 97;
                int charEnc = (numCT - numNK) % 26;
                charEnc = (charEnc < 0)? charEnc + 26 : charEnc;
                plainText += alphaLower.charAt(charEnc);
                newKey += alphaLower.charAt(charEnc);
                j++;
            }
            else {
                System.out.println("Something bad happened in decrypt!");   
            }
        }
        //System.out.println();
        return plainText;
    }// End of decrypt
		
	public static List<String> parseAttributes(final String attributes) {
		final List<String> values = new ArrayList<String>();
		final Matcher matcher = TAG_REGEX.matcher(attributes);
		while (matcher.find()) {
			values.add(matcher.group(1));
		}
		return values;
	}
		
	public static void Select(String[] cmd) {
		// 0      1 2    3
		// SELECT * FROM tableName
		int privId = -1;
		int userId = -1;
		List<Integer> roles = new ArrayList<Integer>();
		try {
			// get privid for select
			stmt = conn.createStatement();
			String sql = "Select privid, privname from privileges where privname = 'SELECT'";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) privId = rs.getInt("privid");
			rs.close();
			if(privId == -1) {System.out.println("Privilege with name 'INSERT' was not found"); return;}
			
			// get current user's id
			stmt = conn.createStatement();
			sql = "Select userid, username from users where username = '" + currentUser + "'";
			rs = stmt.executeQuery(sql);
			if(rs.next()) userId = rs.getInt("userid");
			rs.close();
			if(userId == -1) {System.out.println("Current user=" + currentUser + " not found in users table"); return;}
			
			// check if current user has privilege to SELECT from table
			stmt = conn.createStatement();
			boolean hasPrivilege = false;
			String subSql = "(Select roleid, userid from UsersRoles where userid=" + userId + ") b";
			sql = "Select b.roleid, rp.privid, rp.tablename from rolesPrivileges rp right join " + subSql + " on rp.roleid=b.roleid where tableName='" + cmd[3] + "' and privid=" + privId + " order by b.roleid";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				hasPrivilege = true;
				roles.add(rs.getInt("roleid"));
				int tempPrivId = rs.getInt("privid");
				String tempTableName = rs.getString("tablename");
				if ((tempPrivId != privId) || !(tempTableName.equals(cmd[3]))) {
					System.out.println("Privilege Id and/or tableName does not match the query given");
					hasPrivilege = false;
				}
			}
			rs.close();
			
			if (!hasPrivilege) {
				System.out.println("Authorization failure");
			}
			else {
				//build string to get all keys user has
				String allRoles = "";
				for (int z = 0; z < roles.size(); z++) {
					if (z == roles.size()-1) {
						allRoles += "roleid=" + roles.get(z);
					}
					else {
						allRoles += "roleid=" + roles.get(z) + " or ";
					}
				}
				//debug v
				//System.out.println("  All roles sub sql="+allRoles);
				
				//get key for the role of the user
				stmt = conn.createStatement();
				sql = "Select encryptionkey, roleid from roles where " + allRoles;
				rs = stmt.executeQuery(sql);
				Map<Integer, String> keys = new HashMap<Integer, String>();
				while(rs.next()) {
					keys.put(rs.getInt("roleid"),rs.getString("encryptionkey"));
				}
				rs.close();
				//debug v
				//for ( Map.Entry<Integer, String> entry : keys.entrySet()) {
				//	String e = entry.getValue();
				//	Integer intt = entry.getKey();
				//	System.out.printf("   Roleid=%d with EncryptionKey=%s\n", intt, e);
				//}
				
				//now select from given table and do not display the "encrypted" or "owner role" columns
				stmt = conn.createStatement();
				sql = "Select * from " + cmd[3];
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount() - 2; //subtract as to not show the "encrypted" or "owner role" columns
				
				//print column names
				for (int k = 1; k <= columnCount; k++) {
					String colName = rsmd.getColumnName(k);
					if (k == columnCount) {System.out.print(colName);}
					else {System.out.printf("%s, ",colName);}
				}
				System.out.println();
				
				//print attributes
				while(rs.next()) {
					String row = "";
					for (int l = 1; l <= columnCount; l++) {
						int ownerRole = rs.getInt("OwnerRole");
						int encryptedCol = rs.getInt("EncryptedColumn");
						if (l == columnCount) {
							if (roles.contains(ownerRole) && l == encryptedCol) {
								String strTempD = rs.getString(l);
								//debug v
								//System.out.println("\nDecrypting using key " + keys.get(ownerRole) +" OwnerRole is:" + ownerRole + " Str2Dec:" + strTempD);
								
								row += Decrypt(strTempD, (String)keys.get(ownerRole));
							}
							else {row += rs.getString(l);}
						}
						else {
							if (roles.contains(ownerRole) && l == encryptedCol) {
								String strTempD = rs.getString(l);
								//debug v
								//System.out.println("\nDecrypting using key " + keys.get(ownerRole) +" OwnerRole is:" + ownerRole + " Str2Dec:" + strTempD);
								
								row += Decrypt(strTempD, (String)keys.get(ownerRole)) + ", ";
							}
							else {row += rs.getString(l) + ", ";}
						}
					}
					System.out.println(row);
				}
				rs.close();
			}
			
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Insert(String[] cmd) {
		// 0      1    2         3                  4       5        6******!!
		// INSERT INTO tableName VALUES(valuleList) ENCRYPT columnNo ownerRole
		int privId = -1;
		int userId = -1;
		int roleId = -1;
		try{
			// get privid for insert
			stmt = conn.createStatement();
			String sql = "Select privid, privname from privileges where privname = 'INSERT'";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) privId = rs.getInt("privid");
			rs.close();
			if(privId == -1) {System.out.println("Privilege with name 'INSERT' was not found"); return;}  

			// get current user's id
			stmt = conn.createStatement();
			sql = "Select userid, username from users where username = '" + currentUser + "'";
			rs = stmt.executeQuery(sql);
			if(rs.next()) userId = rs.getInt("userid");
			rs.close();
			if(userId == -1) {System.out.println("Current user=" + currentUser + " not found in users table"); return;}
			
			// get role's id
			stmt = conn.createStatement();
			sql = "Select roleid, rolename from roles where rolename = '" + cmd[6] + "'";
			rs = stmt.executeQuery(sql);
			if(rs.next()) roleId = rs.getInt("roleid");
			rs.close();
			if(roleId == -1) {System.out.println("roleid not found in roles table for rolename='"+cmd[6]+"'"); return;}

			// check if current user has privilege to INSERT
			stmt = conn.createStatement();
			boolean hasPrivilege = false;
			String subSql = "(Select roleid, userid from UsersRoles where userid=" + userId + " and roleid=" + roleId + ") b";
			sql = "Select b.roleid, rp.privid, rp.tablename from rolesPrivileges rp right join " + subSql + " on rp.roleid=b.roleid where tableName='" + cmd[2] + "' and privid=" + privId; 
			/*
			select b.roleid, rp.privid, rp.tablename from rolesprivileges rp right join (select roleid, userid from usersroles where userid=2) b on rp.roleid=b.roleid where tablename='Schools' and privid=1;
			*/

			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				hasPrivilege = true;
			}
			rs.close();
			if (!hasPrivilege) {
				System.out.println("Authorization failure");
				System.out.println();
			}
			else {
				System.out.println("Row inserted successfully");
				System.out.println();
				//now still need to break up attribute list and insert into the appropriate table
				List<String> attributes = parseAttributes(cmd[3]);
				String key = "";
				//System.out.println("key started as \""+key+"\"");
				int rowNum = Integer.parseInt(cmd[5]);
				//System.out.println("rowNum="+rowNum+"");
				if(rowNum > 0) { //don't encrypt if rowNum is zero
					stmt = conn.createStatement();
					sql = "Select encryptionkey from roles where roleid=" + roleId;
					rs = stmt.executeQuery(sql);
					rs.next();
					key = rs.getString("encryptionkey");
					//System.out.println("Key is now \""+key+"\"");
					//minus one to convert one base index to zero base index
					attributes.set(rowNum-1,Encrypt(attributes.get(rowNum-1),key)); //encrypt and set attribute
					rs.close();
				}
				//build attribute string for sql statement
				String attrStr = "";
				for (int k = 0; k < attributes.size(); k++) {
					//should look like 'attr1','attr2',...,'attrN',
					attrStr += "'" + attributes.get(k) + "',"; //still need to add encrypCol and OwnerRole
				}
				//System.out.println("Attribute string was built as \""+attrStr+"\"");
				//add encrypCol and OwnerRole to attrStr
				attrStr += "'"+cmd[5]+"','"+roleId+"'";
				//now insert attributes with enrypted (or not) attributes for appropirate table
				stmt = conn.createStatement();
				sql = "INSERT INTO " + cmd[2] + " VALUES (" + attrStr + ")";
				//System.out.println("sql=\""+sql+"\"");
				stmt.executeUpdate(sql);
			}
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end Insert
		
	public static void RevokePrivilege(String[] cmd) {
		// 0      1         2        3    4        5  6
		// REVOKE PRIVILEGE privName FROM roleName ON tableName
		
		int privId = -1;
		int roleId = -1;
		try{
			stmt = conn.createStatement();
			String sql = "Select privid, privname from privileges where privname = '" + cmd[2] + "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			privId = rs.getInt("privid");
			rs.close();

			stmt = conn.createStatement();
			sql = "Select roleid, rolename from roles where rolename = '" + cmd[4] + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			roleId = rs.getInt("roleid");

			stmt = conn.createStatement();
			//sql = "INSERT INTO RolesPrivileges " + "VALUES (" + roleId + ", " + privId + ", '" + cmd[6] + "')";
			sql = "DELETE FROM RolesPrivileges WHERE roleid=" + roleId + " AND privid=" + privId + " AND tableName='" + cmd[6] + "'";
			stmt.executeUpdate(sql);
			rs.close();
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end RevokePrivilege
		
	public static void GrantPrivilege(String[] cmd) {
		// 0     1         2        3  4        5  6
		// GRANT PRIVILEGE privName TO roleName ON tableName
		
		int privId = -1;
		int roleId = -1;
		try{
			stmt = conn.createStatement();
			String sql = "Select privid, privname from privileges where privname = '" + cmd[2] + "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			privId = rs.getInt("privid");
			rs.close();

			stmt = conn.createStatement();
			sql = "Select roleid, rolename from roles where rolename = '" + cmd[4] + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			roleId = rs.getInt("roleid");

			stmt = conn.createStatement();
			sql = "INSERT INTO RolesPrivileges " + "VALUES (" + roleId + ", " + privId + ", '" + cmd[6] + "')";
			stmt.executeUpdate(sql);
			rs.close();
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end GrantPrivilege
		
	public static void GrantRole(String[] cmd) {
		int userId = -1;
		int roleId = -1;
		try{
			/* now need to get userId and roleId from the Users and Roles table 
				using the username (cmd[2]) and rolename (cmd[3])*/
			stmt = conn.createStatement();
			String sql = "Select userid, username from users where username = '" + cmd[2] + "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			userId = rs.getInt("userid");
			rs.close();

			stmt = conn.createStatement();
			sql = "Select roleid, rolename from roles where rolename = '" + cmd[3] + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			roleId = rs.getInt("roleid");

			stmt = conn.createStatement();
			sql = "INSERT INTO UsersRoles " + "VALUES (" + userId + ", " + roleId + ")";
			stmt.executeUpdate(sql);
			rs.close();
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end GrantRole

	public static int GenUID(String tableName) { //generates a UID that is not in the table tableName
		int uid = 1;
		try{
			//STEP 4: Execute a query
			stmt = conn.createStatement();

			//order by one orders the table by the first column aka the id's
			String sql = "Select * from " + tableName + " order by 1"; 
			ResultSet rs = stmt.executeQuery(sql);

			//Step 5: Extract data from result set
			boolean empty = true;
			while(rs.next()) {
					int id = rs.getInt(1); //ids are always in first column
					if (uid == id) uid++;
					empty = false;
			}

			if (empty) {
					System.out.printf("Table did not have data to preform dynamic generation of UID.\n");
			}
			else {
					//System.out.printf("Table was not empty.\n");
					//System.out.printf("UID: %d\n", uid);
			}
			rs.close();
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return uid;	   
	}// end genUID

	//method to create roles
	public static void CreateRole(String[] cmd) {
		int uid = -1;
		try{
			//STEP 4: Execute a query
			stmt = conn.createStatement();
			uid = GenUID("roles");
			String sql = "INSERT INTO Roles " + "VALUES (" + uid + ", '" + cmd[2] + "', '" + cmd[3] + "')";
			stmt.executeUpdate(sql);
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end CreateRole

	//method to create users
	public static void CreateUser(String[] cmd) {
		int uid = -1;
		try{
			//STEP 4: Execute a query
			stmt = conn.createStatement();

			uid = GenUID("users");
			String sql = "INSERT INTO Users " + "VALUES (" + uid + ", '" + cmd[2] + "', '" + cmd[3] + "')";
			stmt.executeUpdate(sql);
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}// end CreateUser

	//method to login users
	public static void Login(String[] cmd) {
		try{
			//Step 4: Execute a query
			//System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "Select * from users where username='" + cmd[1] + "' and password='" + cmd[2] + "'";
			//System.out.println("Sql stmt is " + sql);
			ResultSet rs = stmt.executeQuery(sql);

			//Step 5: Extract data from result set
			boolean empty = true;
			while(rs.next()) {
					/* int userid = rs.getInt("userid");
					 * String username = rs.getString("username");
					 * String password = rs.getString("password");
					 * System.out.printf("UserId: %d, UserName: %s, 
						Password: %s\n", userid, username, password); */
					empty = false;
			}

			if (empty) {
					System.out.println("Invalid login");
			}
			else {
					System.out.println("Login successful");
					currentUser = cmd[1];
					//System.out.printf("currentUser: %s\n", currentUser);
			}
			//stmt.close();
			rs.close();
		}catch (SQLException se) {
			se.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		//finally {
		//	try {
		//		if (stmt != null) stmt.close(); 
		//	}catch(SQLException se) {
		//	}//do nothing
		//}
	}// end Login

}//end FirstExample
