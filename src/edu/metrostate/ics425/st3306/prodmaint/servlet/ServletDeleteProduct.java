package edu.metrostate.ics425.st3306.prodmaint.servlet;


import edu.metrostate.ics425.st3306.prodmaint.model.ProductCatalog;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * this servlet takes the clients request to delete an object from the catalog and asks for confirmation
 * upon confirmation the client is forwarded to the under construction page
 * upon cancellation of request the client is returned to the catalog page
 */
@WebServlet(name = "ServletDeleteProduct", urlPatterns = {"/deleteProduct"})
public class ServletDeleteProduct extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @param request  request that is sent to the servlet
     * @param response response that is sent to the servlet
     * @throws ServletException catch servlet errors
     * @throws IOException      catch file errors
     *                          <p>
     *                          this servlet takes in the request data sent from the ServletDisplayProduct
     *                          that information determines what object the user is requesting to delete
     *                          if the user has not initiated a delete of the object then action is null
     *                          if the user has initiated an delete of the object then the server checks that the object
     *                          still exists, if object does not exist user is sent back to the catalog page with
     *                          information regarding the missing item
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var action = request.getParameter("delete");
		ProductCatalog catalog = ProductCatalog.getInstance();
        String productCode = request.getParameter("product");
        String requestType;

        if (action != null) {
            try {
                if(catalog.hasRecord(productCode)) {
					ArrayList<Object> edit = new ArrayList<>();
					edit.add(productCode);
                    catalog.updateRecords(0, edit);
                    catalog.close();
                    requestType = "/catalog.jsp";
                } else {
                    request.setAttribute("errMsg", "Item cannot be found");
                    requestType = "/catalog.jsp";
                }
            } catch (Error | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                request.setAttribute("errMsg", "Item cannot be found");
                requestType = "/catalog.jsp";
            }
        } else if(productCode != null){
            requestType = "/WEB-INF/view/catalogdelete.jsp";
        } else {
            requestType = "/catalog.jsp";
        }

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
