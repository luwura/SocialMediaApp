package org.example.repo.paging;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.db.FriendshipDBRepository;
import org.example.repo.pagingUtils.Page;
import org.example.repo.pagingUtils.PageImplementation;
import org.example.repo.pagingUtils.Pageable;
import org.example.repo.pagingUtils.PagingRepository;
import org.example.repo.db.UserDBRepository;
import org.example.validation.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDBPagingRepository extends FriendshipDBRepository implements PagingRepository<Tuple<Long,Long>, Friendship>
{


    public FriendshipDBPagingRepository(String url, String username, String password, Validator<Friendship> validator,UserDBRepository userRepo) {
        super(url, username, password,validator,userRepo);
    }

    @Override
    public Page<Friendship> getAll(Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users limit ? offset ?");

        ) {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize() * (pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            Set<Friendship> friendships = new HashSet<>();
            while (resultSet.next()) {
                Long firstUserID = resultSet.getLong("user1_id");
                Long secondUserID = resultSet.getLong("user2_id");
                User firstUser = userRepo.findOne(firstUserID).orElse(null);
                User secondUser = userRepo.findOne(secondUserID).orElse(null);
                Friendship f = new Friendship(new Tuple<User,User>(firstUser,secondUser));
                f.setId(new Tuple<>(firstUserID, secondUserID));
                friendships.add(f);
            }

            return new PageImplementation<Friendship>(pageable,friendships.stream()) ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
