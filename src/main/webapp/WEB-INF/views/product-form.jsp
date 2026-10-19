<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${product.id > 0 ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<main class="container form-container">
    <a class="back-link" href="${pageContext.request.contextPath}/products">← Danh sách sản phẩm</a>
    <section class="form-card">
        <p class="eyebrow">PRODUCT MANAGEMENT</p>
        <h1>${product.id > 0 ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới'}</h1>
        <p class="subtitle">Nhập đầy đủ thông tin bên dưới.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert"><c:out value="${errorMessage}"/></div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/products?action=${product.id > 0 ? 'update' : 'create'}">
            <c:if test="${product.id > 0}">
                <input type="hidden" name="id" value="<c:out value='${product.id}'/>">
            </c:if>
            <div class="form-field">
                <label for="name">Tên sản phẩm</label>
                <input id="name" name="name" type="text" maxlength="255" required
                       value="<c:out value='${product.name}'/>" autocomplete="off">
            </div>
            <div class="form-field">
                <label for="price">Giá (VNĐ)</label>
                <input id="price" name="price" type="number" min="0" step="0.01" required
                       value="<c:out value='${priceInput}'/>">
                <small>Giá không âm, tối đa 2 chữ số thập phân.</small>
            </div>
            <div class="form-field">
                <label for="quantity">Số lượng</label>
                <input id="quantity" name="quantity" type="number" min="0" step="1" required
                       value="<c:out value='${quantityInput}'/>">
            </div>
            <div class="form-actions">
                <a class="button button-secondary" href="${pageContext.request.contextPath}/products">Hủy</a>
                <button class="button button-primary" type="submit">Lưu sản phẩm</button>
            </div>
        </form>
    </section>
</main>
</body>
</html>
