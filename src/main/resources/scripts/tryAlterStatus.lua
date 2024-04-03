local key = KEYS[1];
local limitNum = tonumber(ARGV[1]);
local seconds = tonumber(ARGV[2]);
local currentTime = tonumber(ARGV[3]);

-- 删除指定时间之前的数据
redis.call('zremrangebyscore', key, '-inf', currentTime - seconds * 1000000000);

-- 获取当前有序集合的元素数量
local currentNum = redis.call('zcard', key);

-- 判断是否超过限制次数
if currentNum < limitNum then
    redis.call('zadd', key, currentTime, currentTime);
    return 1;
else
    return 0;
end;
