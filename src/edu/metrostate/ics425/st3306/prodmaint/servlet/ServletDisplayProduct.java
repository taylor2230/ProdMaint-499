package edu.metrostate.ics425.st3306.prodmaint.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

import edu.metrostate.ics425.st3306.prodmaint.model.ProductCatalog;

/**
 * this servlet controls the ability to see the catalog
 * upon client request, from catalog, the client will be forwarded to edit, delete, or add product pages
 * the catalog page will always reflect what is within the catalog itself and forward control as needed to make modifications
 */
@WebServlet(name = "catalog", urlPatterns = {"/catalog"})
public class ServletDisplayProduct extends HttpServlet {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2L;

    public ServletDisplayProduct() {
        super();
    }

    /**
     * @param request  the request from the browser with the information required to properly process
     * @param response the response from the request
     * @throws ServletException catch any potential servlet errors
     * @throws IOException      catch any potential file errors
     *                          <p>
     *                          this method determines the request from the user.
     *                          The user is either requiring an edit, deletion, or view of the product
     *                          In the parameters it is determined what the user is requesting
     *                          If the user requests an object edit that is passed to the ServletEditProduct
     *                          If the user requests an object deletion that is passed to the ServletDeleteProduct
     *                          If the user is only requesting to view the catalog aka load the page then action will be null
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new ProductCatalog();
        ProductCatalog catalog = ProductCatalog.getInstance();
        var action = request.getParameter("edit");
        String requestType;

        if (action != null) {
            String productCode = request.getParameter("product");
            try {
                if(!catalog.hasRecord(productCode)) {
                    request.setAttribute("errMsg", "Item cannot be found");
                    requestType = "/catalog.jsp";
                } else {
                    if (action.equals("EDIT")) requestType = "editProduct";
                    else requestType = "deleteProduct";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requestType = "/catalog.jsp";
            }
        } else {

            try {
                request.setAttribute("products", catalog.getRecords());
				catalog.close();
            } catch (SQLException ce) {
                ce.printStackTrace();
                request.setAttribute("errMsg", "Cannot Process Request");
            }

            requestType = "/catalog.jsp";
        }

        request.getRequestDispatcher(requestType).forward(request, response);
    }

    /**
     * see doPost information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
