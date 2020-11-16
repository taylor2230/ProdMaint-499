<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/generic.css">
</head>
<body>
<section class="generic-header">
    <a class="generic-b-link" href="${pageContext.request.contextPath}/index.jsp">
        <button>HOME</button>
    </a>
    <a class="generic-b-link" href="catalog">
        <button>VIEW PRODUCTS</button>
    </a>
</section>
<section class="product-details">
    <h1 class="title">Edit Product</h1>
    <form class="generic-form" action="editProduct" method="POST">
        <label class="generic-label" for="product">Product ID</label>
        <input class="generic-text" id="product" name="product" type="text" placeholder="product ID.."
               value="${param.product}" readonly>

        <label class="generic-label" for="description">Product Description</label>
        <input class="generic-text" id="description" name="description" type="text" placeholder="product description.."
               value="${param.description}">

        <label class="generic-label" for="price">Product Price</label>
        <input class="generic-text" id="price" name="price" type="number" step=".01" placeholder="product price.."
               value="${param.price}">

        <label class="generic-label" for="release">Product Release</label>
        <input class="generic-text" id="release" name="release" type="date" placeholder="product release date..MM/dd/YYYY"
               title="Format: MM/DD/YYYY" value="${param.release}">

        <input class="generic-button" type="submit" id="change" name="change" value="UPDATE">
        <a class="cancel-button" href="catalog">
            <button type="button">CANCEL</button>
        </a>
    </form>
    <c:if test="${!empty errors}">
        <div class="generic-errors-wrapper">
            <c:forEach items="${errors}" var="errors" varStatus="stat">
                <p class="generic-error">${errors.value}</p>
            </c:forEach>
        </div>
    </c:if>
</section>
</body>
</html>