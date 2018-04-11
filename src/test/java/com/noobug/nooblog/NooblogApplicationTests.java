package com.noobug.nooblog;

import com.noobug.nooblog.security.TokenProvider;
import com.noobug.nooblog.tools.utils.ConfigUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NooblogApplicationTests {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    public void contextLoads() {
        int n = 0;
        String jwt = tokenProvider.generateToken("acc", "666", new ArrayList<>());
        n++;
    }

}
