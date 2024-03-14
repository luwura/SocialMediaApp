package org.example.repo.db;


import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.example.domain.User;
import org.example.repo.Repo;
import org.example.validation.Validator;

public class UserDBRepository implements Repo<Long, User> {

    protected String url;
    protected String username;
    protected String password;
    Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users\n" +
                    "where id = ?;");

        ) {
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                User u = new User(firstName,lastName,username,password,email);
                u.setId(id);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<User> getAll() {
        Set<User> userSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from users;");
             ResultSet resultSet = statement.executeQuery();) {

            while(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                User user = new User(firstName, lastName,username,password,email);
                user.setId(id);

                userSet.add(user);

            }

            return userSet;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> add(User entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        String insertSQL = "insert into users (first_name, last_name,username,password,email) values (?,?,?,?,?);";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(insertSQL);) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setString(5, entity.getEmail());

            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<User> delete(Long id) {

        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<User> user = findOne(id);

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("delete from users "+"where id = ?;");) {

            statement.setInt(1,  Math.toIntExact(id));

            int response = statement.executeUpdate();
        return response != 0 ? user : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<User> update(User entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        String updateSQL = "UPDATE users\n" +
                "SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?\n" +
                "WHERE id = ?;";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(updateSQL);) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setString(5, entity.getEmail());
            statement.setInt(6,Math.toIntExact(entity.getId()));

            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}