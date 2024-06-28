DELETE FROM products
WHERE product_id IN (
    SELECT product_id
    FROM (
        SELECT product_id
        FROM products
        WHERE name = 'Laptop'
        ORDER BY product_id
        LIMIT 2 OFFSET 1
    ) AS subquery
);
