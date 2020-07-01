package com.ihrm.system;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroTest {
    @Test
    public void testCreateMd5Password(){
        String password = "123456";
        String salt = "18275993802";
        String s = new Md5Hash(password, salt, 3).toString();
        System.out.println("password: " + s);
    }
}
