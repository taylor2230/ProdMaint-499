<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/catalog.css">
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
    <a class="generic-b-link" href="addProduct">
        <button>ADD PRODUCT</button>
    </a>
</section>
<section class="product-display">
    <div class="product-title">
        <input class="product-item" value="Code">
        <input class="product-item" value="Description">
        <input class="product-item" value="Price">
        <input class="product-item" value="Release Date">
        <input class="product-item" value="Years Released">
    </div>
    <c:forEach items="${products}" var="product" varStatus="i">
        <div class="product">
            <form class="product-form" action="catalog" method="post">
                <input class="product-item" id="product" name="product" type="text"
                       value="${product.getProductCode()}" readonly>
                <input class="product-item" id="description" name="description" type="text"
                       value="${product.getDescription()}" readonly>
                <input class="product-item" id="price" name="price" type="text" value="${product.getPrice()}"
                       readonly>
                <input class="product-item" id="release" name="release" type="text"
                       value="${product.getReleaseDate()}" readonly>
                <c:choose>
                    <c:when test="${product.getYearsReleased() >= 0}">
                        <input class="product-item" id="release-years" name="release-years" type="text"
                               value="${product.getYearsReleased()}" readonly>
                    </c:when>
                    <c:otherwise>
                        <input class="product-item" id="release-years" name="release-years" type="text" value=""
                               readonly>
                    </c:otherwise>
                </c:choose>

                <input class="product-item" name="edit" type="submit" value="EDIT">
                <input class="product-item" name="edit" type="submit" value="DELETE">
            </form>
        </div>
    </c:forEach>
    <h3 class="err">${errMsg}</h3>
</section>
</body>
</html>
