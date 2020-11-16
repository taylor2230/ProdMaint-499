package edu.metrostate.ics425.st3306.prodmaint.servlet;

import edu.metrostate.ics425.st3306.prodmaint.model.ProductCatalog;
import edu.metrostate.ics425.st3306.prodmaint.model.ProductRequests;

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
import java.util.*;

/**
 * this servlet takes the clients request to edit an object from the catalog and asks for confirmation
 * upon editing the details the client is forwarded to the under construction page
 * upon cancellation of request the client is returned to the catalog page
 */
@WebServlet(name = "ServletEditProduct", urlPatterns = {"/editProduct"})
public class ServletEditProduct extends HttpServlet {

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
	 * @param request  request that is sent to the servlet
	 * @param response response that is sent to the servlet
	 * @throws ServletException catch servlet errors
	 * @throws IOException      catch file errors
	 *                          <p>
	 *                          this servlet takes in the request data sent from the ServletDisplayProduct
	 *                          that information determines what object the user is requesting to edit
	 *                          if the user has not initiated an edit of the object then action is null
	 *                          if the object no longer exists the user is sent back to the view page with an error
	 *                          If object does exist then each attribute is updated as required
	 *                          All new information is verified for the same requirements as addProduct
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		var action = request.getParameter("change");

		ProductCatalog catalog = ProductCatalog.getInstance();

		String productCode = request.getParameter("product");
		String code = request.getParameter("product");
		String descriptor = request.getParameter("description");
		String cost = request.getParameter("price");
		LocalDate release = toDate(request.getParameter("release"));

		@SuppressWarnings("unchecked")
		HashMap<String, String> errorsList =
				(HashMap<String, String>) checkErrors(code, descriptor, cost);
		String requestType;

		if (action != null) {
			try {
				if(catalog.hasRecord(productCode)) {

					if(errorsList.size() > 0 ) {
						requestType = "/WEB-INF/view/catalogedit.jsp";
					} else {
						ArrayList<Object> edit = new ArrayList<>();
						ProductRequests product = catalog.getSingleRecord(productCode);
						Double price = Double.parseDouble(cost);
						if (!product.getDescription().equals(descriptor)) {
							edit.add("ProductDescription");
							edit.add(descriptor);
							edit.add(productCode);
							catalog.updateRecords(4, edit);
						}
						if (!(product.getPrice() == price)) {
							edit.clear();
							edit.add("ProductPrice");
							edit.add(price);
							edit.add(productCode);
							catalog.updateRecords(4, edit);
						}
						if(product.getReleaseDate() != release || product.getReleaseDate() == null || !product.getReleaseDate().equals("")) {
							edit.clear();
							edit.add("ProductReleaseDate");
							if(release != null) {
								Date date = Date.valueOf(release);
								edit.add(date);
							} else {
								edit.add("");
							}
							edit.add(productCode);
							catalog.updateRecords(4, edit);
						}
						catalog.close();
						requestType = "/catalog.jsp";
					}
				} else {
					request.setAttribute("errMsg", "Item cannot be found");
					requestType = "/catalog.jsp";
				}
			} catch (Error | SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				requestType = "/catalog.jsp";
			}
		} else if(productCode != null){
			requestType = "/WEB-INF/view/catalogedit.jsp";
		} else {
			requestType = "/catalog.jsp";
		}

		request.setAttribute("errors", errorsList);

		try {
			request.setAttribute("products", catalog.getRecords());
			catalog.close();
		} catch (Error | SQLException e) {
			e.printStackTrace();
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(requestType);
		dispatcher.forward(request, response);
	}

	/**
	 * See doPost
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
