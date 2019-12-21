package com.moon.ailatrieuphu.email;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptPass {
    public static String bcrypt(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(13));
    }
}
