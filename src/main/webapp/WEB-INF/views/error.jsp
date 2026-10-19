<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Có lỗi xảy ra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<main class="container form-container">
    <section class="form-card error-card">
        <p class="eyebrow">PRODUCT MANAGEMENT</p>
        <h1>Có lỗi xảy ra</h1>
        <div class="alert alert-error" role="alert"><c:out value="${errorMessage}"/></div>
        <a class="button button-primary" href="${pageContext.request.contextPath}/products">Quay lại danh sách</a>
    </section>
</main>
</body>
</html>
