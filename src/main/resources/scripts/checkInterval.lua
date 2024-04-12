local key = KEYS[1];
local interval = tonumber(ARGV[1]);
local currentTime = tonumber(ARGV[2]);

-- 获取当前有序集合的元素数量
local currentNum = redis.call('zcard', key);

-- 清空超过限制数量的元素
if currentNum >= tonumber(ARGV[3]) then
    redis.call('zremrangebyrank', key, 0, currentNum - tonumber(ARGV[3]))
end

-- 判断是否超过限制次数
if currentNum == 0 then
    redis.call('zadd', key, currentTime, currentTime);
    redis.call('EXPIRE', key, 24 * 60 * 60) -- 设置过期时间为24小时
    return 1;
else
    local lastTime = redis.call('zrange', key, -1, -1)[1];
    if currentTime - lastTime < interval * 1000000 then
        return 0;
    else
        redis.call('zadd', key, currentTime, currentTime);
        redis.call('EXPIRE', key, 24 * 60 * 60) -- 设置过期时间为24小时
        return 1;
    end;
end;
