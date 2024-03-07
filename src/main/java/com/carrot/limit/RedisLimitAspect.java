package com.carrot.limit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

@Aspect
@Component
public class RedisLimitAspect {

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    private static final String LUA_PATH = "redis_limit.lua";

    private static final DefaultRedisScript<Number> redisScript;

    static {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Number.class);
        ClassPathResource classPathResource = new ClassPathResource(LUA_PATH);
        redisScript.setScriptSource(new ResourceScriptSource(classPathResource));
    }


    @Around("@annotation(com.carrot.limit.RedisLimiter)")
    public Object limit(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) signature;
            RedisLimiter limiter = msig.getMethod().getAnnotation(RedisLimiter.class);

            String prefix = limiter.prefix();
            String key = prefix + limiter.key();
            int count = limiter.count();
            int period = limiter.period();

            Number num = redisTemplate.execute(redisScript, Collections.singletonList(key), count, period);
            if (num != null && num.intValue() == 0) {
                return "limit";
            } else {
                return pjp.proceed(pjp.getArgs());
            }
        }

        return pjp.proceed(pjp.getArgs());
    }
}
