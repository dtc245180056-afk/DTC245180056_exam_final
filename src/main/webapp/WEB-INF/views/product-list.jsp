<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <p class="eyebrow">PRODUCT MANAGEMENT</p>
            <h1>Quản lý sản phẩm</h1>
            <p class="subtitle">Theo dõi thông tin và số lượng sản phẩm của bạn.</p>
        </div>
        <a class="button button-primary" href="${pageContext.request.contextPath}/products?action=new">
            + Thêm sản phẩm
        </a>
    </header>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}"/></div>
    </c:if>

    <section class="table-card" aria-label="Danh sách sản phẩm">
        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-state">
                    <h2>Chưa có sản phẩm</h2>
                    <p>Danh sách đang trống. Hãy thêm sản phẩm đầu tiên.</p>
                    <a class="button button-primary" href="${pageContext.request.contextPath}/products?action=new">
                        Thêm sản phẩm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <fmt:setLocale value="vi_VN"/>
                <div class="table-scroll">
                    <table>
                        <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Tên sản phẩm</th>
                            <th scope="col">Giá</th>
                            <th scope="col">Số lượng</th>
                            <th scope="col">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr>
                                <td class="id-cell"><c:out value="${product.id}"/></td>
                                <td class="product-name"><c:out value="${product.name}"/></td>
                                <td class="price-cell">
                                    <fmt:formatNumber value="${product.price}" type="number"
                                                      minFractionDigits="2" maxFractionDigits="2"
                                                      groupingUsed="true"/>
                                    <span class="currency">₫</span>
                                </td>
                                <td><c:out value="${product.quantity}"/></td>
                                <td>
                                    <div class="actions">
                                        <a class="button button-small button-edit"
                                           href="${pageContext.request.contextPath}/products?action=edit&amp;id=${product.id}">
                                            Sửa
                                        </a>
                                        <form class="inline-form"
                                              action="${pageContext.request.contextPath}/products?action=delete"
                                              method="post"
                                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">
                                            <input type="hidden" name="id" value="<c:out value='${product.id}'/>">
                                            <button class="button button-small button-delete" type="submit">Xóa</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
    <footer class="page-footer">ProductManagement · Quản lý đơn giản, hiệu quả</footer>
</main>
</body>
</html>
