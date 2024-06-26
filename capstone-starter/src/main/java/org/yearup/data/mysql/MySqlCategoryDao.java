package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {

        String query = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query))
        {
            while (resultSet.next())
            {
                categories.add(mapRow(resultSet));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String query = "SELECT * FROM categories WHERE category_id = ?";
        Category category = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {

            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    category = mapRow(resultSet);
                }
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public Category create(Category category)
    {
       String query = "INSERT INTO categories (name, description) VALUES (?, ?)";
       try(Connection connection = getConnection();
           PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
       {
           statement.setString(1, category.getName());
           statement.setString(2, category.getDescription());

           int affectedRows = statement.executeUpdate();
           if(affectedRows > 0)
           {
               try (ResultSet generatedKeys = statement.getGeneratedKeys())
               {
                   if (generatedKeys.next())
                   {
                       category.setCategoryId(generatedKeys.getInt(1));
                   }
               }
           }
       }
       catch (SQLException e)
        {
            e.printStackTrace();
        }
       return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
       String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

       try(Connection connection = getConnection();
           PreparedStatement statement = connection.prepareStatement(query))
       {
           statement.setString(1, category.getName());
           statement.setString(2, category.getDescription());
           statement.setInt(3,categoryId);

           statement.executeUpdate();
       } catch (SQLException e)
       {
           e.printStackTrace();
       }
    }

    @Override
    public void delete(int categoryId)
    {
        String query = "DELETE FROM categories WHERE category_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
