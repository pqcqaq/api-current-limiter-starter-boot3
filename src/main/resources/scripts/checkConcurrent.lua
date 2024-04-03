local key = KEYS[1]
local limitNum = tonumber(ARGV[1])
local set = ARGV[2] == "true"
local currentTime = tonumber(ARGV[3])

local infos = redis.call("LRANGE", key, 0, -1)

if not set then
    if #infos == 0 then
        return 1
    end
    redis.call("LPOP", key)
    return 1
end

if #infos == 0 then
    redis.call("RPUSH", key, currentTime)
    return 1
else
    if #infos < limitNum then
        redis.call("RPUSH", key, currentTime)
        return 1
    else
        return 0
    end
end
