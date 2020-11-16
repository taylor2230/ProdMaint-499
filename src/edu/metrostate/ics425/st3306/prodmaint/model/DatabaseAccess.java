package edu.metrostate.ics425.st3306.prodmaint.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface DatabaseAccess {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/ProdMaint?useLegacyDatetimeCode=false&serverTimezone=UTC";

    static final String USER = "root";
    static final String PASSWORD = "";

	/**
	 * @throws ClassNotFoundException will catch if the driver is unable to be initialized
	 *                          <p>
	 *                          creates the connection the database based on the implemented interface values
	 *                          The value returned in the connection to the DB, if the value return is null then the DB did not successfully connect
	 */
    Connection connect() throws ClassNotFoundException;
    
	/**
	 * @throws SQLException 	catch any SQL errors
	 *                          <p>
	 *                          closes the statement and connection to the DB
	 */
    void close();
    
	/**
	 * @param  type				the type of SQL query needed
	 *                          <p>
	 *                          contains pre-created SQL queries to the DB
	 *                          this returns the string that will be sent to the requesting class to create a prepared SQL statement
	 */
    String SQL(int type);
    
	/**
	 * @throws SQLException 	catch any SQL errors
	 *                          <p>
	 *                          Returns all the records contain in the DB at the time of request
	 *                          Creates objects that are returned back to be parsed out or used as needed
	 */
    @SuppressWarnings("rawtypes")
	List getRecords() throws SQLException;
    
	/**
	 * @param productCode  		the product code that is being retrieved from the database
	 * @throws SQLException 	catch any potential SQL errors
	 *                          <p>
	 *                          Returns back a single record from the database based on the inputted product code
	 *                          if no product code exists then the return result is a null value
	 */
    boolean hasRecord(String code) throws SQLException;
    
	/**
	 * @param productCode  the product code that is being retrieved from the database
	 * @throws SQLException catch any potential SQL errors
	 *                          <p>
	 *                          Returns a bool value if the DB has the item in the list or not
	 *                          the return value is determined on the value existing within the request
	 */
	ProductRequests getSingleRecord(String prod) throws SQLException;
	
	/**
	 * @param productCode  		the product code that is being retrieved from the database
	 * @param params			an array containing the parameters for the specific prepared SQL statement based on order
	 * @throws ClassNotFoundException 
	 * @throws SQLException 	catch any potential SQL errors
	 *                          <p>
	 *                          executes the query based on the type of request
	 *                          this does not perform select operations
	 *                          create, update, and delete operations only
	 */
	boolean updateRecords(int updateType, ArrayList<Object> params) throws ClassNotFoundException;

}
