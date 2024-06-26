package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductsController
{
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> search(@RequestParam(name="cat", required = false) Integer categoryId,
                                          @RequestParam(name="minPrice", required = false) BigDecimal minPrice,
                                          @RequestParam(name="maxPrice", required = false) BigDecimal maxPrice,
                                          @RequestParam(name="color", required = false) String color
                                )
    {
        try
        {
            List<Product> products = productDao.search(categoryId, minPrice, maxPrice, color);
            return ResponseEntity.ok(products);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Product> getById(@PathVariable int id )
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

            return ResponseEntity.ok(product);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product)
    {
        try
        {
            Product createdProduct = productDao.create(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        try
        {
           var existingProduct = productDao.getById(id);
           if(existingProduct == null)
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

           productDao.update(id, product);
           return ResponseEntity.noContent().build();
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id)
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

            productDao.delete(id);
            return ResponseEntity.noContent().build();
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
