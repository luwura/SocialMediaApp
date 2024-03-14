package org.example.service;

import org.example.domain.User;
import org.example.repo.Repo;
import org.example.repo.paging.UserDBPagingRepository;
import org.example.repo.pagingUtils.Page;
import org.example.repo.pagingUtils.Pageable;
import org.example.repo.pagingUtils.PageableImplementation;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;
import org.example.utils.observer.Observable;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class UserService implements ServiceInterface<Long,User>,Observable<UserChangeEvent> {
    private final UserDBPagingRepository repoUser;
    private final FriendshipService repoFriendship;
    byte[] key = new byte[16];


    public UserService(UserDBPagingRepository repoUsers,FriendshipService repoFriendship) {
        this.repoUser = repoUsers;
        this.repoFriendship = repoFriendship;
    }


    @Override
    public User add(User e) {
        User returnedUser=repoUser.add(e).orElse(null);
        if(returnedUser != null)
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD,returnedUser));

        return returnedUser;
    }

    @Override
    public User update(User e) {
        User oldUser = repoUser.findOne(e.getId()).orElse(null);
        if(oldUser != null){
            User returnedUser = repoUser.update(e).orElse(null);
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE,returnedUser));
            return returnedUser;
        }

        return oldUser;
    }

    @Override
    public User delete(User e) {
       // repoFriendship.getFriends(e).forEach(friend->repoFriendship.delete(new Friendship(new Tuple<>(e,friend), LocalDateTime.now())));
        User deletedUser = repoUser.delete(e.getId()).orElse(null);
        if(deletedUser != null){
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE,deletedUser));
        }
    return deletedUser;
    }

    @Override
    public User find(Long id) {
        return repoUser.findOne(id).orElse(null);
    }



    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        for (User user : repoUser.getAll()) {
            userList.add(user);
        }
        return userList;

    }

    public String encrypt(String strToEncrypt){
        //secureRandom.nextBytes(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public User checkCredentials(String user_email, String user_password){
        Set<User> users = new HashSet<>();
        this.repoUser.getAll().forEach(users::add);
        for(User u:users){
            if(u.getEmail().equals(user_email) && u.getPassword().equals(encrypt(user_password)))
                return u;
        }
        return null;
    }

    private List<org.example.utils.observer.Observer<UserChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(org.example.utils.observer.Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

    public Set<User> getNextUsers() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getUsersOnPage(this.page);
    }

    public Set<User> getUsersOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> userPage = repoUser.getAll(pageable);
        return userPage.getContent().collect(Collectors.toSet());
    }


}

