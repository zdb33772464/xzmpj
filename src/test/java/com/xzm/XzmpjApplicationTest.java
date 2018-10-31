package com.xzm;

import com.xzm.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XzmpjApplicationTest {
    @Autowired
    RedisUtil redisUtil;

    @Test
    public void testRedis(){
        redisUtil.set("mzname","zdb",60);
    }

}
