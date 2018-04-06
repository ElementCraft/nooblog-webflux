package com.noobug.nooblog;

import com.noobug.nooblog.tools.utils.ConfigUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NooblogApplicationTests {

    @Autowired
    private ConfigUtil configUtil;

    @Test
    public void contextLoads() {
        int n = configUtil.getInt("test1", 555);

        n++;
    }

}
