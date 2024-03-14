package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.FriendshipRequest;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.db.FriendshipRequestDBRepository;
import org.example.repo.paging.FriendshipDBPagingRepository;
import org.example.repo.paging.UserDBPagingRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.FriendshipRequestChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FriendshipRequestService implements ServiceInterface<Tuple<Long,Long>,FriendshipRequest>,Observable<FriendshipRequestChangeEvent> {
    private final FriendshipDBPagingRepository repoFriendship;
    private final UserDBPagingRepository repoUser;
    private final FriendshipRequestDBRepository repoFriendshipReq;


    public FriendshipRequestService(UserDBPagingRepository repoUsers,FriendshipDBPagingRepository repoFriendship,FriendshipRequestDBRepository repoFriendshipReq) {
        this.repoFriendship = repoFriendship;
        this.repoUser = repoUsers;
        this.repoFriendshipReq = repoFriendshipReq;
    }
    @Override
    public FriendshipRequest add(FriendshipRequest request) {
        FriendshipRequest friendshipRequest =  repoFriendshipReq.add(request).orElse(null);

        if(friendshipRequest != null){
            notifyObservers(new FriendshipRequestChangeEvent(ChangeEventType.ADD,friendshipRequest));
        }
        return friendshipRequest;
    }
    @Override
    public FriendshipRequest find(Tuple<Long,Long> id) {
        return repoFriendshipReq.findOne(id).orElse(null);
    }

    @Override
    public FriendshipRequest delete(FriendshipRequest friendship) {
        Tuple<Long, Long> reverseID = new Tuple<>(
                friendship.getFrom().getId(),
                friendship.getTo().getId());

        FriendshipRequest foundFriendship = find(reverseID);

        if (foundFriendship != null) {
            return repoFriendshipReq.delete(reverseID).orElse(null);
        }

        FriendshipRequest deletedFriendship =  repoFriendshipReq.delete(friendship.getId()).orElse(null);
        return deletedFriendship;
    }

    @Override
    public FriendshipRequest update(FriendshipRequest friendship) {
        FriendshipRequest updatedRequest =  repoFriendshipReq.update(friendship).orElse(null);
        if(updatedRequest != null){
            notifyObservers(new FriendshipRequestChangeEvent(ChangeEventType.UPDATE,updatedRequest));
        }
        return updatedRequest;
    }

    public List<FriendshipRequest> getAll() {
        List<FriendshipRequest> friendshipList = new ArrayList<>();
        for (FriendshipRequest friendship : repoFriendshipReq.getAll()) {
            friendshipList.add(friendship);
        }
        return friendshipList;

    }

    public List<User> getUsersByStatus(User user, String status) {
        List<User> userList = new ArrayList<>();
        switch (status) {
            case "accepted":
                getAll().stream().filter(u -> u.getStatus().equals(status)).forEach(u -> {
                    if (u.getFrom().equals(user))
                        userList.add(u.getTo());
                    else if (u.getTo().equals(user))
                        userList.add(u.getFrom());
                });
                break;
            case "pending":
                getAll().stream().filter(u -> u.getStatus().equals(status) && u.getTo().equals(user)).forEach(u -> userList.add(u.getFrom()));
                break;
            case "rejected":
                repoUser.getAll().forEach(u->userList.add(u));
                userList.remove(user);
                getAll().stream().filter(u -> u.getStatus().equals("accepted")).forEach(u ->{
                    if(u.getTo().equals(user))
                        userList.remove(u.getFrom());
                    else if(u.getFrom().equals(user))
                        userList.remove(u.getTo());
                } );
                getAll().stream().filter(u -> u.getStatus().equals("pending")&& u.getTo().equals(user)).forEach(u -> userList.remove(u.getFrom()));
                userList.remove(repoUser.findOne(1l).orElse(null));
                break;
            default:
                break;
        }
        return userList;
    }


    private List<org.example.utils.observer.Observer<FriendshipRequestChangeEvent>> observers=new ArrayList<>();


    @Override
    public void addObserver(org.example.utils.observer.Observer<FriendshipRequestChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendshipRequestChangeEvent> e) {
    }

    @Override
    public void notifyObservers(FriendshipRequestChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }
}