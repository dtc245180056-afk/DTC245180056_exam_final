package com.student.productmanagement.dao;

import com.student.productmanagement.model.Product;
import com.student.productmanagement.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private static final String SELECT_COLUMNS = "SELECT id, name, price, quantity FROM products";

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COLUMNS + " ORDER BY id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
        }
        return products;
    }

    public Optional<Product> getProductById(int id) throws SQLException {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapProduct(resultSet)) : Optional.empty();
            }
        }
    }

    public Product addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProductValues(statement, product);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating a product did not return an ID.");
                }
                product.setId(generatedKeys.getInt(1));
            }
        }
        return product;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setProductValues(statement, product);
            statement.setInt(4, product.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private static void setProductValues(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getName());
        statement.setBigDecimal(2, product.getPrice());
        statement.setInt(3, product.getQuantity());
    }

    private static Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity"));
    }
}
