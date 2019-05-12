import java.sql.*;
public class DataBase { //בסיס הנתונים

	private Connection conn;
	private Statement stmt;

	//----------------------CONSTRUCTOR -----------------------------------------
	public DataBase(){
		try {

			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://localhost:3306/test";
			conn=DriverManager.getConnection(url, "root", "root");
			stmt=conn.createStatement();
		
		} 
		catch (ClassNotFoundException e) {
		
			e.printStackTrace(); }
		catch (SQLException e) { 
			
			e.printStackTrace(); }
		

} // constructor
	
	
	
	
	
	//----------------------CREATE TABLE METHOD -----------------------------------------
	public void createTable(String tableName, boolean tablesExistAlready){//מקבלת כבנאי את שם הטבלה fireEvents
		try {
			
			if (tablesExistAlready){
				stmt.executeUpdate("Drop TABLE "+tableName);
			}
			
			String table="CREATE TABLE "+tableName+"(ID int, Location int, Distance int, Severity int, Address Varchar(50))"; //� ���� ������ �����
			stmt.executeUpdate(table);
			
		} catch (SQLException e) { 
		
			e.printStackTrace(); }
		
	}//createTables
	
	//----------------------INSERT TO A TABLE METHOD -----------------------------------------
	public void insertToTable(String tableName, FireEvent e){ //מכניס לטבלה את פרטי האירוע
	String str="INSERT INTO " +tableName+" VALUES("
			+ e.getID()+"," 
			+e.getLocation()+ "," 
			+ e.getDistance()+","
			+e.getSeverity()+ ","
			+"'"+e.getAddress() +"'" + ")" ;
	

	try {
		stmt.executeUpdate(str);
	} catch (SQLException e1) {
	

		e1.printStackTrace();
	}
	}
	
	
	//----------------------PULL OUT DATA METHOD -----------------------------------------
	public String extract(String tableName,String colName){ //מחלץ נתון מתוך מערכת המידע
		ResultSet result=null;
		String ans="";
		String str="select * from " + tableName;
		try {
			result=stmt.executeQuery(str);
			result.next(); // move to the first line
			ans=result.getString(colName);
			
			
		} catch (SQLException e) { e.printStackTrace();	}
		
	return ans;
		
		
	}
}
	