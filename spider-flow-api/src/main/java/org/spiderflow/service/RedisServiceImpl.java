package org.spiderflow.service;

import org.spiderflow.common.config.CustomClassPathResource;
import org.spiderflow.core.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package org.spiderflow.service
 * @date 2024-09-02 15:48
 * @description Type your description over here.
 */
@Service
public class RedisServiceImpl implements RedisService {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void set(String key, Object value, long expireTime) {
		redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
	}

	@Override
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Boolean del(String key) {
		return redisTemplate.delete(key);
	}

	@Override
	public Long del(List<String> keys) {
		return redisTemplate.delete(keys);
	}

	@Override
	public Boolean expire(String key, long expireTime) {
		return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
	}

	@Override
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	@Override
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public Long incr(String key, long delta) {
		return redisTemplate.opsForValue().increment(key, delta);
	}

	@Override
	public Long decr(String key, long delta) {
		return redisTemplate.opsForValue().increment(key, -delta);
	}

	@Override
	public Object hGet(String key, String hashKey) {
		return redisTemplate.opsForHash().get(key, hashKey);
	}

	@Override
	public Boolean hSet(String key, String hashKey, Object value, long expireTime) {
		redisTemplate.opsForHash().put(key, hashKey, value);
		return expire(key, expireTime);
	}

	@Override
	public void hSet(String key, String hashKey, Object value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}

	@Override
	public Map<Object, Object> hGetAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	@Override
	public Boolean hSetAll(String key, Map<String, Object> map, long expireTime) {
		redisTemplate.opsForHash().putAll(key, map);
		return expire(key, expireTime);
	}

	@Override
	public void hSetAll(String key, Map<String, ?> map) {
		redisTemplate.opsForHash().putAll(key, map);
	}

	@Override
	public void hDel(String key, Object[] hashKey) {
		redisTemplate.opsForHash().delete(key, hashKey);
	}

	@Override
	public Boolean hHasKey(String key, String hashKey) {
		return redisTemplate.opsForHash().hasKey(key, hashKey);
	}

	@Override
	public Long hIncr(String key, String hashKey, Long delta) {
		return redisTemplate.opsForHash().increment(key, hashKey, delta);
	}

	@Override
	public Long hDecr(String key, String hashKey, Long delta) {
		return redisTemplate.opsForHash().increment(key, hashKey, -delta);
	}

	@Override
	public Set<Object> sMembers(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	@Override
	public Long sAdd(String key, Object[] values) {
		return redisTemplate.opsForSet().add(key, values);
	}

	@Override
	public Long sAdd(String key, long expireTime, Object[] values) {
		Long count = redisTemplate.opsForSet().add(key, values);
		expire(key, expireTime);
		return count;
	}

	@Override
	public Boolean sIsMember(String key, Object value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}

	@Override
	public Long sSize(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	@Override
	public Long sRemove(String key, Object[] values) {
		return redisTemplate.opsForSet().remove(key, values);
	}

	@Override
	public List<Object> lRange(String key, long start, long end) {
		return redisTemplate.opsForList().range(key, start, end);
	}

	@Override
	public Long lSize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	@Override
	public Object lIndex(String key, long index) {
		return redisTemplate.opsForList().index(key, index);
	}

	@Override
	public Long lPush(String key, Object value) {
		return redisTemplate.opsForList().rightPush(key, value);
	}

	@Override
	public Long lPush(String key, Object value, long expireTime) {
		Long index = redisTemplate.opsForList().rightPush(key, value);
		expire(key, expireTime);
		return index;
	}

	@Override
	public Long lPushAll(String key, Object[] values) {
		return redisTemplate.opsForList().rightPushAll(key, values);
	}

	@Override
	public Long lPushAll(String key, long expireTime, Object[] values) {
		Long count = redisTemplate.opsForList().rightPushAll(key, values);
		expire(key, expireTime);
		return count;
	}

	@Override
	public Long lRemove(String key, long count, Object value) {
		return redisTemplate.opsForList().remove(key, count, value);
	}

	/**
	 * @param luaFileName
	 * @param keyList
	 * @return String
	 * @description 执行Lua脚本，返回String字符串
	 * @author yida
	 * @date 2023-05-22 16:39:33
	 */
	public String runLuaScript(String luaFileName, List<String> keyList, Object[] args) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new CustomClassPathResource("/lua/" + luaFileName, this.getClass())));
		redisScript.setResultType(String.class);
		String result = "";
		try {
			result = stringRedisTemplate.execute(redisScript, keyList, args);
		} catch (Exception e) {
			throw new IllegalStateException("Lua running abnormally");
		}
		return result;
	}
}
