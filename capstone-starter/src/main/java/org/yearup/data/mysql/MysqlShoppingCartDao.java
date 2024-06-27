package org.yearup.data.mysql;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;

@Component
public class MysqlShoppingCartDao implements ShoppingCartDao
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = "SELECT p.productId, p.name, p.description, p.price, p.categoryId, c.quantity, c.discountPercent " +
                "FROM ShoppingCart c " +
                "JOIN Products p ON c.productId = p.productId " +
                "WHERE c.userId = ?";

        List<ShoppingCartItem> items = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            ShoppingCartItem item = new ShoppingCartItem();
            Product product = new Product();

            product.setProductId(rs.getInt("productId"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setPrice(rs.getBigDecimal("price"));
            product.setCategoryId(rs.getInt("categoryId"));

            item.setProduct(product);
            item.setQuantity(rs.getInt("quantity"));
            item.setDiscountPercent(rs.getBigDecimal("discountPercent"));

            return item;
        });

        ShoppingCart cart = new ShoppingCart();
        items.forEach(item -> cart.add(item));

        return cart;
    }


    @Override
    public void addToCart(int userId, int productId) {
        String sql = "INSERT INTO ShoppingCart (userId, productId, quantity) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

        try {
            jdbcTemplate.update(sql, userId, productId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void updateCartItem(int userId, int productId, int quantity) {
        String sql = "UPDATE ShoppingCart SET quantity = ? WHERE userId = ? AND productId = ?";
        jdbcTemplate.update(sql, quantity, userId, productId);
    }

    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM ShoppingCart WHERE userId = ?";
        jdbcTemplate.update(sql, userId);
    }
}

