local count
-- 获取key值[限流用到的key]
local key = KEYS[1]
-- 获取第一个参数大小[限流的值]
local limit = tonumber(ARGV[1])
-- 获取当前的流量大小[没有为0]
local current = tonumber(redis.call('get', key) or "0")

-- 判断当前流量是否超出
if current + 1 > limit then
    return 0  -- 超出当前流量[限流]
else
    count = redis.call("INCRBY", key, 1)  -- 没有超出则加1
    if tonumber(count) == 1 then   -- 第一次设置的时候设置过期时间
        redis.call('expire', KEYS[1], ARGV[2])
    end
    return 1
end
