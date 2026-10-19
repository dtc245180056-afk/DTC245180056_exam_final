package com.student.productmanagement.servlet;

import com.student.productmanagement.dao.ProductDAO;
import com.student.productmanagement.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private static final String LIST_VIEW = "/WEB-INF/views/product-list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/product-form.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";
    private static final Pattern PRICE_PATTERN = Pattern.compile("(?:\\d+(?:\\.\\d{1,2})?|\\.\\d{1,2})");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\d+");
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareEncoding(request, response);
        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            showProducts(request, response);
        } else if (action.equals("new")) {
            request.setAttribute("product", new Product());
            request.setAttribute("priceInput", "");
            request.setAttribute("quantityInput", "");
            forward(request, response, FORM_VIEW);
        } else if (action.equals("edit")) {
            editProduct(request, response);
        } else {
            showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy chức năng được yêu cầu.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareEncoding(request, response);
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            saveProduct(request, response, false);
        } else if ("update".equals(action)) {
            saveProduct(request, response, true);
        } else if ("delete".equals(action)) {
            deleteProduct(request, response);
        } else {
            showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy chức năng được yêu cầu.");
        }
    }

    private void showProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Product> products = productDAO.getAllProducts();
            request.setAttribute("products", products);
            String success = request.getParameter("success");
            if ("created".equals(success)) {
                request.setAttribute("successMessage", "Thêm sản phẩm thành công.");
            } else if ("updated".equals(success)) {
                request.setAttribute("successMessage", "Cập nhật sản phẩm thành công.");
            } else if ("deleted".equals(success)) {
                request.setAttribute("successMessage", "Xóa sản phẩm thành công.");
            }
            forward(request, response, LIST_VIEW);
        } catch (SQLException exception) {
            handleFailure(request, response, "Không thể tải danh sách sản phẩm.", exception);
        }
    }

    private void editProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null || id <= 0) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "ID sản phẩm không hợp lệ.");
            return;
        }

        try {
            Optional<Product> product = productDAO.getProductById(id);
            if (product.isEmpty()) {
                showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy sản phẩm cần sửa.");
                return;
            }
            request.setAttribute("product", product.get());
            request.setAttribute("priceInput", product.get().getPrice().toPlainString());
            request.setAttribute("quantityInput", Integer.toString(product.get().getQuantity()));
            forward(request, response, FORM_VIEW);
        } catch (SQLException exception) {
            handleFailure(request, response, "Không thể tải thông tin sản phẩm.", exception);
        }
    }

    private void saveProduct(HttpServletRequest request, HttpServletResponse response, boolean update)
            throws ServletException, IOException {
        Integer id;
        if (update) {
            id = parseId(request.getParameter("id"));
        } else {
            id = 0;
        }
        if (id == null || (update && id <= 0)) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "ID sản phẩm không hợp lệ.");
            return;
        }

        String name = request.getParameter("name");
        String priceInput = request.getParameter("price");
        String quantityInput = request.getParameter("quantity");
        Product product = new Product();
        product.setId(id);
        product.setName(name == null ? "" : name.trim());
        request.setAttribute("product", product);
        request.setAttribute("priceInput", priceInput == null ? "" : priceInput);
        request.setAttribute("quantityInput", quantityInput == null ? "" : quantityInput);

        String validationError = validateAndSetProduct(product, priceInput, quantityInput);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            forward(request, response, FORM_VIEW);
            return;
        }

        try {
            if (update) {
                if (!productDAO.updateProduct(product)) {
                    showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Không tìm thấy sản phẩm cần cập nhật.");
                    return;
                }
            } else {
                productDAO.addProduct(product);
            }
            response.sendRedirect(request.getContextPath()
                    + "/products?success=" + (update ? "updated" : "created"));
        } catch (SQLException exception) {
            handleFailure(request, response, "Không thể lưu sản phẩm.", exception);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null || id <= 0) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "ID sản phẩm không hợp lệ.");
            return;
        }

        try {
            if (!productDAO.deleteProduct(id)) {
                showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy sản phẩm cần xóa.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/products?success=deleted");
        } catch (SQLException exception) {
            handleFailure(request, response, "Không thể xóa sản phẩm.", exception);
        }
    }

    private static String validateAndSetProduct(Product product, String priceInput, String quantityInput) {
        if (product.getName().isBlank()) {
            return "Tên sản phẩm không được để trống.";
        }
        if (product.getName().length() > 255) {
            return "Tên sản phẩm không được vượt quá 255 ký tự.";
        }
        if (priceInput == null || !PRICE_PATTERN.matcher(priceInput.trim()).matches()) {
            return "Giá phải là số không âm và có tối đa 2 chữ số thập phân.";
        }

        BigDecimal price = new BigDecimal(priceInput.trim());
        if (price.compareTo(MAX_PRICE) > 0) {
            return "Giá vượt quá giới hạn cho phép.";
        }
        product.setPrice(price);

        if (quantityInput == null || !QUANTITY_PATTERN.matcher(quantityInput.trim()).matches()) {
            return "Số lượng phải là số nguyên không âm.";
        }
        try {
            product.setQuantity(Integer.parseInt(quantityInput.trim()));
        } catch (NumberFormatException exception) {
            return "Số lượng vượt quá giới hạn cho phép.";
        }
        return null;
    }

    private static Integer parseId(String value) {
        if (value == null || !value.matches("\\d+")) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void handleFailure(HttpServletRequest request, HttpServletResponse response,
                               String publicMessage, SQLException exception)
            throws ServletException, IOException {
        getServletContext().log(publicMessage, exception);
        showError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, publicMessage);
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, int status, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("errorMessage", message);
        forward(request, response, ERROR_VIEW);
    }

    private static void prepareEncoding(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
    }

    private static void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }
}
