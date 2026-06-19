package com.betsettler.redis.service;

import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDBService {

	private final StringRedisTemplate redisTemplate;

	public RedisDBService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void hashPutAll(String key, Map<String, String> fields) {
		redisTemplate.opsForHash().putAll(key, fields);
	}

	public Map<Object, Object> hashGetAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	public void setAdd(String key, String value) {
		redisTemplate.opsForSet().add(key, value);
	}

	public Set<String> setMembers(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	public void flushDb() {
		redisTemplate.execute((RedisCallback<Void>) connection -> {
			connection.serverCommands().flushDb();
			return null;
		});
	}

}
