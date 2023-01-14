package com.hmdp.utils;

import com.hmdp.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        threadLocal.set(user);
    }

    public static UserDTO getUser(){
        return threadLocal.get();
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
