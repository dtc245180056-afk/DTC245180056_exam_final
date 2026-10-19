# ProductManagement

Ứng dụng web quản lý sản phẩm cho bài thi cuối kỳ của sinh viên `dtc245180056`. Ứng dụng hỗ trợ xem, thêm, sửa và xóa sản phẩm bằng Java Servlet/JSP, JDBC và MySQL.

## Công nghệ sử dụng

- Java 17
- Jakarta Servlet 6.0 và JSP trên Apache Tomcat 10.1
- Maven (đóng gói WAR)
- MySQL 8.x và MySQL Connector/J
- JSTL 3, HTML, CSS và JavaScript cơ bản

## Yêu cầu môi trường

- JDK 17
- Apache Maven 3.8 trở lên
- Apache Tomcat 10.1
- MySQL 8.0.16 trở lên (để áp dụng đầy đủ các CHECK constraint trong `database.sql`)

## Cấu trúc thư mục

```text
ProductManagement/
├── pom.xml
├── database.sql
├── README.md
└── src/main/
    ├── java/com/student/productmanagement/
    │   ├── dao/ProductDAO.java
    │   ├── model/Product.java
    │   ├── servlet/ProductServlet.java
    │   └── util/DBConnection.java
    └── webapp/
        ├── index.jsp
        ├── assets/css/style.css
        └── WEB-INF/views/
            ├── product-form.jsp
            ├── product-list.jsp
            └── error.jsp
```

## Database

`database.sql` tạo database `product_management` với mã hóa `utf8mb4`, bảng InnoDB `products` và ba dòng dữ liệu mẫu. Bảng gồm:

| Cột | Kiểu dữ liệu | Mô tả |
| --- | --- | --- |
| `id` | `INT` | Khóa chính, tự tăng |
| `name` | `VARCHAR(255)` | Tên sản phẩm, bắt buộc |
| `price` | `DECIMAL(12,2)` | Giá không âm |
| `quantity` | `INT` | Số lượng không âm |

### Tạo database

Chạy script bằng MySQL CLI:

```bash
mysql -u your_mysql_username -p < database.sql
```

Thay `your_mysql_username` bằng tài khoản MySQL của bạn. MySQL sẽ hỏi mật khẩu; không lưu mật khẩu trong source code.

## Cấu hình MySQL

Ứng dụng đọc cấu hình theo thứ tự: Java system property (`-D...`) trước, sau đó đến biến môi trường:

| System property | Biến môi trường | Mặc định |
| --- | --- | --- |
| `db.url` | `DB_URL` | `jdbc:mysql://localhost:3306/product_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC` |
| `db.username` | `DB_USERNAME` | Không có; bắt buộc cấu hình |
| `db.password` | `DB_PASSWORD` | Rỗng nếu không đặt |

Ví dụ PowerShell (thay các giá trị viết hoa trong ngoặc vuông bằng thông tin của bạn; mật khẩu không được ghi vào source):

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/product_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
$env:DB_USERNAME = "[MYSQL_USERNAME]"
$env:DB_PASSWORD = "[MYSQL_PASSWORD]"
```

Hãy đặt biến trong cùng cửa sổ terminal dùng để khởi chạy Tomcat, hoặc cấu hình chúng trong môi trường của máy chủ. Tài khoản MySQL cần quyền truy cập database `product_management`.

## Chức năng

- Hiển thị danh sách sản phẩm, giá có phân cách hàng nghìn và thông báo khi danh sách trống.
- Thêm sản phẩm với kiểm tra tên, giá và số lượng ở phía server.
- Sửa thông tin sản phẩm; xử lý ID sai định dạng hoặc sản phẩm không tồn tại.
- Xóa bằng HTTP POST, có hộp thoại xác nhận trên trình duyệt.
- Chuyển hướng sau khi thêm/sửa/xóa thành công để tránh gửi lại biểu mẫu.
- Escape dữ liệu hiển thị bằng JSTL `c:out`; truy vấn JDBC dùng `PreparedStatement`.

## Build và triển khai

Từ thư mục chứa `pom.xml`, dùng JDK 17 và chạy:

```powershell
mvn clean package
```

WAR được tạo tại `target/ProductManagement.war`. Khởi động MySQL và cấu hình các biến môi trường ở trên, sau đó chép WAR vào thư mục `webapps` của Tomcat 10.1. Khởi động Tomcat (Windows: `bin\startup.bat`). Mở:

```text
http://localhost:8080/ProductManagement/
```

## Lỗi thường gặp

- **Không kết nối được MySQL:** kiểm tra MySQL đang chạy, `DB_URL`, username/password và quyền truy cập database.
- **Access denied:** cập nhật `DB_USERNAME`/`DB_PASSWORD` cho đúng tài khoản; mật khẩu trống chỉ phù hợp nếu tài khoản MySQL được cấu hình cho phép.
- **Unknown database/table:** chạy `database.sql` vào đúng máy chủ MySQL.
- **Không tương thích Servlet/Jakarta:** ứng dụng cần Tomcat 10.1; Tomcat 9 dùng `javax.servlet` nên không tương thích với bản này.
- **Maven báo không tìm thấy compiler Java 17:** cài JDK 17 và kiểm tra `JAVA_HOME` cùng `PATH` trỏ đến JDK đó.
- **Lỗi thiếu quyền ghi/xóa:** cấp quyền phù hợp cho tài khoản MySQL trên database `product_management`.

## Kiểm tra chức năng

Maven build và kiểm thử CRUD cần được chạy trên môi trường có JDK 17, Maven, MySQL và Tomcat. Cấu hình kết nối database lấy từ môi trường triển khai; ứng dụng không chứa tài khoản hay mật khẩu mặc định.
