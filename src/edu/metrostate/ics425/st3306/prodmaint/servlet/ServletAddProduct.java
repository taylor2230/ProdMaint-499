package edu.metrostate.ics425.st3306.prodmaint.servlet;


import edu.metrostate.ics425.st3306.prodmaint.model.ProductCatalog;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * this servlet controls the ability of a client to add a product to the catalog
 * upon success the client is returned to the catalog page
 * upon failure the client is returned back to the addProduct page with the information entered and a list of errors
 */
@WebServlet(name = "addProduct", urlPatterns = {"/addProduct"})
public class ServletAddProduct extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param x as string value
	 * @return parsed string as date field
	 *
	 * this attempts to parse the string sent by the user
	 * The are multiple string formats provided and it will attempt each format until one is found as successful
	 * if the string cannot be parsed then the date field is not set
	 */
	private LocalDate toDate(String x) {
		List<String> types = Arrays.asList("MM/dd/yyyy", "MM-dd-yyyy", "M/d/yyyy", "M-d-yyyy",
				"M/dd/yy", "M-dd-yy", "MM/d/yyyy", "MM-d-yyyy", "yyyy-MM-dd"); //provide a LOV to attempt to parse through
		for (String type : types) {
			try {
				DateTimeFormatter format = DateTimeFormatter.ofPattern(type, Locale.ENGLISH);
				return LocalDate.parse(x, format);
			} catch (Exception ignored) {

			}
		}
		return null;
	}

	/**
	 * @param n as string value to be determined if it is a number
	 * @return boolean shows whether or not it can be turned into a Double
	 *
	 * this method determines if the String that was sent from the user can be set a Double or not
	 * if it can it returns true, else false
	 */

	@SuppressWarnings("unused")
	private boolean isNumber(String n) {
		if (n == null) return false;
		try {
			double x = Double.parseDouble(n);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}


	/**
	 * @param c this is the product code sent by the user
	 * @param d this is the description sent by the user
	 * @param p this is the price sent by the user, in string form
	 * @param r this is the date sent by the user, already set a date, see toDate javadoc
	 * @return returns a new List of the object product
	 *
	 * this method takes in all of the information provided by the user and organize the object into a list
	 * each piece of data is set for the object, price is determined to be a number or not in order to be set
	 * the list containing the object is then returned
	 */
	private ArrayList<Object> createProduct(String c, String d, String p, LocalDate r) {
		ArrayList<Object> newProduct = new ArrayList<Object>();
		newProduct.add(c);
		newProduct.add(d);
		if (isNumber(p)) newProduct.add(Double.parseDouble(p));
		if(r != null) {
			Date date = Date.valueOf(r);
			newProduct.add(date);
		} else {
			newProduct.add(null);
		}
		return newProduct;
	}

	/**
	 * @param c this is the product code sent by the user
	 * @param d this is the description sent by the user
	 * @param p this is the price sent by the user, in string form
	 * @return returns a Hashtable containing the errors that were found with a message
	 *
	 * the method takes in all the information from the user and determines if any fields are missing
	 * then it determines what specific fields are missing or contain white space empty characters
	 * it then returns the errors in a hashtable to be processed
	 */
	private Object checkErrors(String c, String d, String p) {
		HashMap<String, String> errors = new HashMap<>();

		if (c == null || d == null || p == null) {
			errors.put("Error", "All Fields Except Release Date Are Required");
		}

		if ((c == null || c.equals(""))) {
			errors.put("Product ID", "Product ID is Missing or Blank; Please Enter a Value");
		}

		if ((d == null || d.equals(""))) {
			errors.put("Description", "Description is Missing or Blank; Please Enter a Value");
		}

		if ((p == null || p.equals(""))) {
			errors.put("Price", "Price is Missing or Blank; Please Enter a Value");
		}

		if (isNumber(p)) {
			if(Double.parseDouble(p) < 0) {
				errors.put("Price", "Price is less than 0; Please Enter a Value Greater Than or Equal to 0");
			}
		}

		return errors;
	}

	/**
	 * @param request the request from the user
	 * @param response the response object
	 * @throws ServletException to handle errors with the servlet
	 * @throws IOException to handle files errors
	 *
	 * this method will take in the data from the request ad determine the action that is needed
	 * if there is no form data then the page is simply displayed
	 * if there is form data then the data is validated for errors first
	 * the method will check for identical product codes and missing or incomplete data from the client
	 * if no errors are found then the product is added to the catalog and returns the client to the catalog page
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		var action = request.getParameter("product");
		String requestType;
		boolean outcome = false;
		
		if (action != null) {

			new ProductCatalog();
			ProductCatalog catalog = ProductCatalog.getInstance();

			String code = request.getParameter("product");
			String descriptor = request.getParameter("description");
			String cost = request.getParameter("price");
			LocalDate release = toDate(request.getParameter("release"));

			HashMap<String, String> errorList =
					(HashMap<String, String>) checkErrors(code, descriptor, cost);


			try {
				if(catalog.hasRecord(code)) {
					errorList.put("Item", "An Item With That ID Already Exists");
				}
			} catch (NullPointerException | SQLException e) {
				e.printStackTrace();
			}

			if (errorList.size() > 0) {
				request.setAttribute("errors", errorList);
				requestType = "/WEB-INF/view/catalogadd.jsp";
			} else {
				ArrayList<Object> add = createProduct(code, descriptor, cost, release);
				try {
					outcome = catalog.updateRecords(3, add);
					if(!outcome) {
		                request.setAttribute("errMsg", "Cannot Process Request");
					}
					request.setAttribute("products", catalog.getRecords());
					catalog.close();
				} catch (Error | SQLException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				requestType = "/catalog";
			}
		} else {
			requestType = "/WEB-INF/view/catalogadd.jsp";
		}

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(requestType);
		dispatcher.forward(request, response);
	}

	/**
	 * See doPost javadoc
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
	ServletException, IOException {
		doPost(request, response);
	}
}

