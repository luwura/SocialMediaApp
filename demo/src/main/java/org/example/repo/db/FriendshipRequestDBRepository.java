package org.example.repo.db;

import org.example.domain.Friendship;
import org.example.domain.FriendshipRequest;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.Repo;
import org.example.validation.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRequestDBRepository implements Repo<Tuple<Long,Long>, FriendshipRequest> {

        protected String url;
        protected String username;
        protected String password;
        Validator<FriendshipRequest> validator;
        protected UserDBRepository userRepo;

    public FriendshipRequestDBRepository(String url, String username, String password, Validator<FriendshipRequest> validator,UserDBRepository userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepo = userRepo;
    }

        @Override
        public Optional<FriendshipRequest> findOne(Tuple<Long,Long> id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendship_requests\n" +
                    "where from_id = ? and to_id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id.first()));
            statement.setInt(2, Math.toIntExact(id.second()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long firstUserID = resultSet.getLong("from_id");
                Long secondUserID = resultSet.getLong("to_id");
                String status = resultSet.getString("status");
                User firstUser = userRepo.findOne(firstUserID).orElse(null);
                User secondUser = userRepo.findOne(secondUserID).orElse(null);
                FriendshipRequest f = new FriendshipRequest(firstUser,secondUser,status);
                f.setId(id);
                return Optional.ofNullable(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

        @Override
        public Iterable<FriendshipRequest> getAll() {
        Set<FriendshipRequest> friendsSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from friendship_requests");
             ResultSet resultSet = statement.executeQuery();) {

            while(resultSet.next())
            {
                Tuple<Long,Long> id = new Tuple<>(resultSet.getLong("from_id"),resultSet.getLong("to_id"));
                User user1 = userRepo.findOne(id.first()).orElse(null);
                User user2 = userRepo.findOne(id.second()).orElse(null);
                String status = resultSet.getString("status");
                FriendshipRequest f = new FriendshipRequest(user1,user2,status);
                f.setId(id);

                friendsSet.add(f);

            }

            return friendsSet;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

        @Override
        public Optional<FriendshipRequest> add(FriendshipRequest entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        String insertSQL = "insert into friendship_requests (from_id, to_id,status) values (?,?,?);";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(insertSQL);) {

            statement.setInt(1, Math.toIntExact(entity.getFrom().getId()));
            statement.setInt(2, Math.toIntExact(entity.getTo().getId()));
            statement.setString(3, entity.getStatus());

            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

        @Override
        public Optional<FriendshipRequest> delete(Tuple<Long,Long> id) {

        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<FriendshipRequest> user = findOne(id);

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("delete from friendship_requests where from_id = ? and to_id = ?");) {

            statement.setInt(1,  Math.toIntExact(id.first()));
            statement.setInt(2,  Math.toIntExact(id.second()));

            int response = statement.executeUpdate();
            System.out.println("Deleted " + response + " friendship(s)");
            return response == 1 ? user : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

        @Override
        public Optional<FriendshipRequest> update(FriendshipRequest entity) {

            if (entity == null)
                throw new IllegalArgumentException("entity must not be null");

            String updateSQL = "UPDATE friendship_requests\n" +
                    "SET status = ?\n" +
                    "WHERE from_id = ? and to_id = ?;";

            try (Connection con = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = con.prepareStatement(updateSQL);) {

                statement.setString(1, entity.getStatus());
                statement.setInt(2,Math.toIntExact(entity.getId().first()));
                statement.setInt(3,Math.toIntExact(entity.getId().second()));

                int response = statement.executeUpdate();
                return response == 1 ? Optional.of(entity) : Optional.empty();


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
}


