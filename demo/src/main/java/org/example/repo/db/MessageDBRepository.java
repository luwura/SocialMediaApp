package org.example.repo.db;

import org.example.domain.*;
import org.example.repo.Repo;
import org.example.validation.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MessageDBRepository implements Repo<Long, Message> {

    protected String url;
    protected String username;
    protected String password;
    Validator<Message> validator;
    protected UserDBRepository userRepo;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator,UserDBRepository userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepo = userRepo;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        Long firstUserID=0l;
        String message=null;
        Long replyToID=null;
        LocalDateTime localDateTime;
            List<User> toList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from messages where id=?"
        )){
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                firstUserID = resultSet.getLong("from_id");
                message = resultSet.getString("message");
                replyToID = resultSet.getLong("reply_to_id");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("date");
                localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from message_recievers where message_id=?"
            )){
            statement.setInt(1, Math.toIntExact(firstUserID));
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long secondUserID = (long)resultSet.getInt("user_id");
                User user = userRepo.findOne(secondUserID).orElse(null);
                toList.add(user);
            }
            }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Message f;
        User user = userRepo.findOne(firstUserID).orElse(null);
        if(replyToID!=null)
            f=new Message(user,toList,message,replyToID);
        else
            f = new Message(user,toList,message);
        f.setId(id);
        return Optional.ofNullable(f);
    }

        @Override
        public Iterable<Message> getAll() {
        Set<Message> messageSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from messages");
             ResultSet resultSet = statement.executeQuery();) {

            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("from_id");
                List<User> toList = new ArrayList<>();

                try(Connection connection = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement1 = connection.prepareStatement("select * from message_recievers where message_id=?"
                    )){
                    statement1.setInt(1, Math.toIntExact(id));
                    ResultSet resultSet1 = statement1.executeQuery();
                    while(resultSet1.next()) {
                        Long secondUserID = (long)resultSet1.getLong("user_id");
                        User user = userRepo.findOne(secondUserID).orElse(null);
                        toList.add(user);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                String message = resultSet.getString("message");
                Long replyID = resultSet.getLong("reply_to_id");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                User firstUser = userRepo.findOne(firstUserID).orElse(null);
                Message f;
                if(replyID!=null)
                    f=new Message(firstUser,toList,message,localDateTime,replyID);
                else
                    f = new Message(firstUser,toList,message,localDateTime);
                f.setId(id);
                messageSet.add(f);
            }

            return messageSet;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Message> add(Message entity) {
        int response = 0;

        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        String insertSQL = "insert into messages(from_id,date,message,reply_to_id) values(?,?,?,?)";
        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(insertSQL)) {

            statement.setInt(1, Math.toIntExact(entity.getFrom().getId()));
            Timestamp timestamp = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(2, timestamp);
            statement.setString(3, entity.getMessage());
            if (entity.getReplyTo() != null)
                statement.setInt(4, Math.toIntExact(entity.getReplyTo()));
            else
                statement.setObject(4, null);

            response = statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        insertSQL = "select id from messages where from_id=? and date=? and message=?;";
        Long message_id=0l;
        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(insertSQL)) {

            statement.setInt(1, Math.toIntExact(entity.getFrom().getId()));
            Timestamp timestamp = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(2, timestamp);
            statement.setString(3, entity.getMessage());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                message_id = resultSet.getLong("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String statementText = "insert into message_recievers(user_id, message_id) values ";
        String valuesPlaceholders = IntStream.range(0, entity.getTo().size())
                .mapToObj(i -> "(?, ?)")
                .collect(Collectors.joining(", "));
        statementText += valuesPlaceholders;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement1 = connection.prepareStatement(statementText)) {

            AtomicInteger k = new AtomicInteger(1);
            final Long final_id =message_id;

            entity.getTo().forEach(u -> {
                try {
                    statement1.setInt(k.getAndIncrement(), Math.toIntExact(u.getId()));
                    statement1.setInt(k.getAndIncrement(), Math.toIntExact(final_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            // Use executeUpdate instead of executeQuery for modifying statements
            response = statement1.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return response >0 ? Optional.of(entity) : Optional.empty();
    }


    @Override
    public Optional<Message> delete(Long id) {

        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<Message> user = findOne(id);

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("delete from messages where id=?");) {

            statement.setInt(1,  Math.toIntExact(id));

            int response = statement.executeUpdate();
            System.out.println("Deleted " + response + " friendship(s)");
            return response == 1 ? user : Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
    /*public Iterable<Message> getChat(Long loggedUser,List<Long> otherUser){
        Set<Message> messageSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from messages where\n(from_id=? and to_id=?) or (to_id=? and from_id=?)\n ;");
             ) {
            //group by id, from_id, to_id, message, date, reply_to_id
            // order by date ASC
            statement.setInt(1, Math.toIntExact(loggedUser));
            statement.setInt(2, Math.toIntExact(otherUser));
            statement.setInt(3, Math.toIntExact(loggedUser));
            statement.setInt(4, Math.toIntExact(otherUser));
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("from_id");
                List<User> toList = new ArrayList<>();

                try(Connection connection = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement1 = connection.prepareStatement("select * from message_recievers where message_id=?"
                    )){
                    statement1.setInt(1, Math.toIntExact(firstUserID));
                    ResultSet resultSet1 = statement.executeQuery();
                    while(resultSet1.next()) {
                        Long secondUserID = resultSet1.getLong("user_id");
                        User user = userRepo.findOne(secondUserID).orElse(null);
                        toList.add(user);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                String message = resultSet.getString("message");
                Long replyID = resultSet.getLong("reply_to_id");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                User firstUser = userRepo.findOne(firstUserID).orElse(null);
                Message f;
                if(replyID!=null)
                    f=new Message(firstUser,toList,message,localDateTime,replyID);
                else
                    f = new Message(firstUser,toList,message,localDateTime);
                f.setId(id);
                messageSet.add(f);
            }

            return messageSet;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
/*
    public Optional<Long> findId(Message entity) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from messages where from_id=? and to_id=? and message=?"
            )){
            statement.setInt(1, Math.toIntExact(entity.getFrom().getId()));
            statement.setInt(2, Math.toIntExact(entity.getTo().getId()));
            statement.setString(3, entity.getMessage());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long Id = resultSet.getLong("id");
                return Optional.ofNullable(Id);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }*/

}
