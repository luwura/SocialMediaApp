package org.example.repo.db;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.Repo;
import org.example.validation.Validator;

public class FriendshipDBRepository implements Repo<Tuple<Long,Long>, Friendship> {

    protected String url;
    protected String username;
    protected String password;
    Validator<Friendship> validator;
    protected UserDBRepository userRepo;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator,UserDBRepository userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepo = userRepo;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long,Long> id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships\n" +
                    "where user1_id = ? and user2_id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id.first()));
            statement.setInt(2, Math.toIntExact(id.second()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long firstUserID = resultSet.getLong("user1_id");
                Long secondUserID = resultSet.getLong("user2_id");
                User firstUser = userRepo.findOne(firstUserID).orElse(null);
                User secondUser = userRepo.findOne(secondUserID).orElse(null);
                Friendship f = new Friendship(new Tuple<User,User>(firstUser,secondUser));
                f.setId(id);
                return Optional.ofNullable(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> getAll() {
        Set<Friendship> friendsSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery();) {

            while(resultSet.next())
            {
                Tuple<Long,Long> id = new Tuple<>(resultSet.getLong("user1_id"),resultSet.getLong("user2_id"));
                User user1 = userRepo.findOne(id.first()).orElse(null);
                User user2 = userRepo.findOne(id.second()).orElse(null);
                Friendship f = new Friendship(new Tuple<>(user1,user2),LocalDateTime.now());
                f.setId(id);

                friendsSet.add(f);

            }

            return friendsSet;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> add(Friendship entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        String insertSQL = "insert into friendships (user1_id, user2_id,date) values (?,?,?);";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(insertSQL);) {

            statement.setInt(1, Math.toIntExact(entity.getFriendsPair().first().getId()));
            statement.setInt(2, Math.toIntExact(entity.getFriendsPair().second().getId()));
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<Friendship> delete(Tuple<Long,Long> id) {

        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<Friendship> user = findOne(id);

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("delete from friendships where user1_id = ? and user2_id = ?");) {

            statement.setInt(1,  Math.toIntExact(id.first()));
            statement.setInt(2,  Math.toIntExact(id.second()));

            int response = statement.executeUpdate();
            System.out.println("Deleted " + response + " friendship(s)");
            return response == 0 ? user : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }

}
