package edu.metrostate.ics425.st3306.prodmaint.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProductCatalog implements Serializable, DatabaseAccess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ProductCatalog catalog;
	static {
		catalog = new ProductCatalog();
	}

	Connection connection = null;
	PreparedStatement statement = null;

	public ProductCatalog() {
		super();
	}

	public static ProductCatalog getInstance() {
		return catalog;
	}

	/**
	 * @throws ClassNotFoundException will catch if the driver is unable to be initialized
	 *                          <p>
	 *                          creates the connection the database based on the implemented interface values
	 *                          The value returned in the connection to the DB, if the value return is null then the DB did not successfully connect
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Connection connect() throws ClassNotFoundException {
		try {
			Class.forName(JDBC_DRIVER).newInstance();
			return DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (SQLException | InstantiationException | IllegalAccessException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

	/**
	 * @throws SQLException 	catch any SQL errors
	 *                          <p>
	 *                          closes the statement and connection to the DB
	 */
	@Override
	public void close() {
		try 
		{
			if(this.statement != null && !this.statement.isClosed()) {
				this.statement.close();
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param  type				the type of SQL query needed
	 *                          <p>
	 *                          contains pre-created SQL queries to the DB
	 *                          this returns the string that will be sent to the requesting class to create a prepared SQL statement
	 */
	@Override
	public String SQL(int type) {
		if(this.connection == null) {
			return null;
		}
		if(type == 0) {
			return "DELETE FROM Product WHERE ProductCode = ?";
		} else if(type == 1) {
			return "SELECT * FROM Product";
		} else if(type == 2) {
			return "SELECT * FROM Product WHERE ProductCode = ?";
		} else if (type == 3) {
			return "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";
		} else if (type == 4) {
			return "UPDATE Product SET ProductDescription = ? WHERE ProductCode = ?";
		}  else if (type == 5) {
			return "UPDATE Product SET ProductPrice = ? WHERE ProductCode = ?";
		} else if (type == 6) {
			return "UPDATE Product SET ProductReleaseDate = ? WHERE ProductCode = ?";
		}
		return null;
	}

	/**
	 * @throws SQLException 	catch any SQL errors
	 *                          <p>
	 *                          Returns all the records contain in the DB at the time of request
	 *                          Creates objects that are returned back to be parsed out or used as needed
	 */
	@Override
	public List<Product> getRecords() throws SQLException {
		try {
			List<Product> products = new LinkedList<>();
			this.connection = this.connect();
			String SQL = this.SQL(1);
			this.statement = this.connection.prepareStatement(SQL);
			ResultSet rs = this.statement.executeQuery();

			while(rs.next()) {
				ProductRequests pb = new ProductRequests();
				pb.setProductCode(rs.getString("ProductCode"));
				pb.setDescription(rs.getString("ProductDescription"));
				pb.setPrice(rs.getDouble("ProductPrice"));
				Date productDate = rs.getDate("ProductReleaseDate");

				if(productDate != null) {
					LocalDate date = productDate.toLocalDate();
					pb.setReleaseDate(date);
				} else {
					pb.setReleaseDate(null);
				}
				products.add(pb);
			}
			return products;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param productCode  		the product code that is being retrieved from the database
	 * @throws SQLException 	catch any potential SQL errors
	 *                          <p>
	 *                          Returns back a single record from the database based on the inputted product code
	 *                          if no product code exists then the return result is a null value
	 */
	@Override
	public ProductRequests getSingleRecord(String productCode) throws SQLException {
		try {
			this.connection = this.connect();
			String SQL = this.SQL(2);
			this.statement = this.connection.prepareStatement(SQL);
			this.statement.setString(1,productCode);
			ResultSet rs = this.statement.executeQuery();
			ProductRequests pb = new ProductRequests();
			if(rs.next()) {
				pb.setProductCode(rs.getString("ProductCode"));
				pb.setDescription(rs.getString("ProductDescription"));
				pb.setPrice(rs.getDouble("ProductPrice"));
				Date productDate = rs.getDate("ProductReleaseDate");

				if(productDate != null) {
					LocalDate date = productDate.toLocalDate();
					pb.setReleaseDate(date);
				} else {
					pb.setReleaseDate(null);
				}
			}

			return pb;

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param productCode  the product code that is being retrieved from the database
	 * @throws SQLException catch any potential SQL errors
	 *                          <p>
	 *                          Returns a bool value if the DB has the item in the list or not
	 *                          the return value is determined on the value existing within the request
	 */
	@Override
	public boolean hasRecord(String productCode) throws SQLException {
		try {
			this.connection = this.connect();
			String SQL = this.SQL(2);
			this.statement = this.connection.prepareStatement(SQL);
			this.statement.setString(1,productCode);
			ResultSet rs = this.statement.executeQuery();
			boolean b = rs.isBeforeFirst();
			return b;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return true;
		}
	}

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
	@Override
	public boolean updateRecords(int updateType, ArrayList<Object> params) throws ClassNotFoundException {
		try {
			if(updateType == 0) {
				String SQL = this.SQL(updateType);
				this.connection = this.connect();
				this.statement = this.connection.prepareStatement(SQL);
				this.statement.setString(1,(String) params.get(0));
				int rs = this.statement.executeUpdate();
				return rs > 0;
			} else if(updateType == 3) {
				String SQL = this.SQL(updateType);
				this.connection = this.connect();
				this.statement = this.connection.prepareStatement(SQL);
				this.statement.setString(1,(String) null);
				this.statement.setString(2,(String) params.get(0));
				this.statement.setString(3,(String) params.get(1));
				this.statement.setDouble(4,(Double) params.get(2));
				this.statement.setDate(5,(Date) params.get(3));
				int rs = this.statement.executeUpdate();
				return rs > 0;
			} else if (updateType > 3) {
				this.connection = this.connect();
				this.statement.setString(1,(String) params.get(0));

				if(params.get(0).equals("ProductDescription")) {
					String SQL = this.SQL(updateType);
					this.statement = this.connection.prepareStatement(SQL);
					this.statement.setString(1,(String) params.get(1));

				} else if (params.get(0).equals("ProductPrice")) {
					String SQL = this.SQL(updateType+1);
					this.statement = this.connection.prepareStatement(SQL);
					BigDecimal dec = new BigDecimal((Double) params.get(1));
					this.statement.setBigDecimal(1,(BigDecimal) dec);

				} else if (params.get(0).equals("ProductReleaseDate")) {
					String SQL = this.SQL(updateType+2);
					this.statement = this.connection.prepareStatement(SQL);
					this.statement.setDate(1,(Date) params.get(1));
				} else {
					return false;
				}
				this.statement.setString(2,(String) params.get(2));
				int rs = this.statement.executeUpdate();
				return rs > 0;

			}
		} catch (Error | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
