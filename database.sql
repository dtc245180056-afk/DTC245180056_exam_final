CREATE DATABASE IF NOT EXISTS product_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE product_management;

CREATE TABLE IF NOT EXISTS products (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_products_price_nonnegative CHECK (price >= 0),
    CONSTRAINT chk_products_quantity_nonnegative CHECK (quantity >= 0)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

INSERT INTO products (name, price, quantity) VALUES
    ('Bàn phím cơ', 1250000.00, 12),
    ('Chuột không dây', 450000.00, 25),
    ('Màn hình 24 inch', 3290000.00, 8);
