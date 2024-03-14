package org.example.repo.paging;

import org.example.domain.User;
import org.example.repo.pagingUtils.Page;
import org.example.repo.pagingUtils.PageImplementation;
import org.example.repo.pagingUtils.Pageable;
import org.example.repo.pagingUtils.PagingRepository;
import org.example.repo.db.UserDBRepository;
import org.example.validation.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, User>
{


    public UserDBPagingRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password,validator);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users limit ? offset ?");

        ) {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize() * (pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            Set<User> users = new HashSet<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String username = resultSet.getString("username");

                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(firstName, lastName, username, email, password);
                user.setId(id);
                users.add(user);
            }

            return new PageImplementation<User>(pageable,users.stream()) ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
