package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.Repo;
import org.example.repo.paging.FriendshipDBPagingRepository;
import org.example.repo.paging.UserDBPagingRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class FriendshipService implements ServiceInterface<Tuple<Long,Long>,Friendship>, Observable<FriendshipChangeEvent> {
    private final FriendshipDBPagingRepository repoFriendship;
    private final UserDBPagingRepository repoUser;
    private Random id;


    public FriendshipService(UserDBPagingRepository repoUsers,FriendshipDBPagingRepository repoFriendship) {
        id = new Random();
        this.repoFriendship = repoFriendship;
        this.repoUser = repoUsers;
    }
    public List<User> getFriends(User u){
        List users = new ArrayList();
        repoFriendship.getAll().forEach(friendship -> {
            if(friendship.getFriendsPair().first() == u)
                users.add(friendship.getFriendsPair().second());
            else if(friendship.getFriendsPair().second() == u)
                users.add(friendship.getFriendsPair().first());
        });
        return users;
    }
    @Override
    public Friendship add(Friendship friendship) {
        Friendship adddedFriendship =  repoFriendship.add(friendship).orElse(null);
        if(adddedFriendship != null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD,adddedFriendship));
        }
        return adddedFriendship;
    }

    @Override
    public Friendship find(Tuple<Long,Long> id) {
        return repoFriendship.findOne(id).orElse(null);
    }

    @Override
    public Friendship delete(Friendship friendship) {
        Tuple<Long, Long> reverseID = new Tuple<>(
                friendship.getFriendsPair().second().getId(),
                friendship.getFriendsPair().first().getId());

        Friendship foundFriendship = find(reverseID);

        if (foundFriendship != null) {
            return repoFriendship.delete(reverseID).orElse(null);
        }

        Friendship deletedFriendship =  repoFriendship.delete(friendship.getId()).orElse(null);
        if(deletedFriendship != null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,deletedFriendship));
        }
        return deletedFriendship;
    }



    private List<User> DFS(User u, Map<User, Boolean> visited) {
        List<User> community = new ArrayList<>();
        visited.put(u, Boolean.TRUE);
        community.add(u);
        getFriends(u).forEach(it->{
            if (!visited.get(it)) {
                List<User> list = DFS(it, visited);
                community.addAll(list);
            }
        });
        return community;
    }

    public List<List<User>> getAllCommunities() {
        Map<User, Boolean> visited = new HashMap<>();
        repoUser.getAll().forEach(x -> {
            visited.put(x, Boolean.FALSE);
        });

        List<List<User>> community = new ArrayList<>();
        for (var it : repoUser.getAll()) {
            if (!visited.get(it)) {
                community.add(DFS(it, visited));
            }
        }
        return community;
    }
    public List<User> getMostSociableCommunity() {
        List<List<User>> aux = new ArrayList<>();
        repoUser.getAll().forEach(x-> aux.add(BFS(x)));
        return aux.stream()
                .max(Comparator.comparingInt(List::size))
                .orElse(Collections.emptyList());
    }

    private List<User> BFS(User u) {
        Queue<User> queue = new LinkedList<>();
        Map<User, User> parent = new HashMap<>();
        Map<User, Integer> level = new HashMap<>();
        queue.add(u);
        parent.put(u, null);
        level.put(u, 0);
        User end = u;
        int maxLevel = 0;
        while (!queue.isEmpty()) {
            User current = queue.poll();
            int currentLevel = level.get(current);
            for (User friend : getFriends(current)) {
                if (!parent.containsKey(friend)) {
                    parent.put(friend, current);
                    level.put(friend, currentLevel + 1);
                    queue.add(friend);

                    if (currentLevel + 1 > maxLevel) {
                        maxLevel = currentLevel + 1;
                        end = friend;
                    }
                } else {
                    if(queue.contains(friend))
                        if (level.get(current)>= level.get(friend))
                            parent.put( current,friend);
                }
            }
        }
        List<User> longestPath = new ArrayList<>();
        while (end != null) {
            longestPath.add(end);
            end = parent.get(end);
        }
        Collections.reverse(longestPath);
        return longestPath;
    }


    @Override
    public List<Friendship> getAll() {
        List<Friendship> friendshipList = new ArrayList<>();
        for (Friendship friendship : repoFriendship.getAll()) {
            friendshipList.add(friendship);
        }
        return friendshipList;

    }

    public List<Friendship> friendsFromMonth(Long ID, LocalDateTime date) {
        return getAll().stream()
                .filter(friendship ->
                        (friendship.getFriendsPair().first().getId().equals(ID) ||
                                friendship.getFriendsPair().second().getId().equals(ID))
                                && friendship.getDate().getMonth() == date.getMonth())
                .collect(Collectors.toList());
    }

    @Override
    public Friendship update(Friendship friendship) {
        return repoFriendship.update(friendship).orElse(null);
    }
    private List<org.example.utils.observer.Observer<FriendshipChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(org.example.utils.observer.Observer<FriendshipChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }
}
