package net.gichain.rechargeChannel.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DependsOn("SpringUtils")
public class BaseDao<T> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 增加
	 * @param sql
	 * @param args
	 * @throws Exception
	 */
	public int create(String sql, Object[] args) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 批量增加
	 * @param sql
	 * @param batchArgs
	 * @throws Exception
	 */
	public int[] batchCreate(String sql, List<Object[]> batchArgs) {
		if (batchArgs == null) {
			batchArgs = new ArrayList<Object[]>();
		}
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	/**
	 * 查询list
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryForList(String sql, Object[] args) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.queryForList(sql, args);
	}

	/**
	 * 查询list
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryForList(String sql) {

		return jdbcTemplate.queryForList(sql);
	}

	/**
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryForList(String sql, Map params) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =  new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(params));

	}

	/**
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<T> queryForList(String sql, Map<String, List<String>> params, Class<T> clazz) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =  new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(clazz));
	}

	/**
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public List<T> queryForList(String sql, Object[] args,Class<T> clazz) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.query(sql,args,BeanPropertyRowMapper.newInstance(clazz));
	}

	/**
	 * 查询数量
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Long queryForLong(String sql, Object[] args) {
		if (args == null) {
			args = new Object[]{};
		}

		Long v=jdbcTemplate.queryForObject(sql, args, Long.class);
		if(v==null)
			return 0L;
		return v.longValue();
	}

	/**
	 * 查询数量
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int queryForInt(String sql, Map<String, Object> params) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =  new NamedParameterJdbcTemplate(jdbcTemplate);
		Integer i = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
		if(i == null){
			i = 0;
		}
		return i.intValue();
	}

	/**
	 * 查询List对象
	 * @param sql
	 * @param args
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List<T> queryForObject(String sql, Object[] args, Class<T> clazz) {
		if (args == null) {
			args = new Object[]{};
		}

		return jdbcTemplate.query(sql, args, BeanPropertyRowMapper.newInstance(clazz));
	}

	/**
	 * 查询List对象
	 * @param sql
	 * @param args
	 * @param clazz
	 * @return list
	 */
	public List queryForObjectList(String sql, Object[] args, Class clazz) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.query(sql, args, BeanPropertyRowMapper.newInstance(clazz));
	}

	/**
	 * 修改
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int update(String sql, Object[] args) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 修改
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql, Map<String, Object> params) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =  new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate.update(sql, params);
	}

	/**
	 * 批量修改
	 * @param sql
	 * @param batchArgs
	 * @return
	 * @throws Exception
	 */
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
		if (batchArgs == null) {
			batchArgs = new ArrayList<Object[]>();
		}
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	/**
	 * 删除
	 * @param sql
	 * @param args
	 * @throws Exception
	 */
	public int delete(String sql, Object[] args) {
		if (args == null) {
			args = new Object[]{};
		}
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 插入数据，返回主键
	 * @param creator
	 * @return
	 */
	public long create(PreparedStatementCreator creator){
		if(creator==null)
			return -1L;
		KeyHolder keyholder = new GeneratedKeyHolder();
		jdbcTemplate.update(creator,keyholder);
		Number key=keyholder.getKey();
		if(key!=null)
			return key.longValue();//intValue();
		else
			return 0L;

	}

	/**
	 * 插入数据，不返回主键
	 * @param creator
	 * @return
	 */
	public void update(PreparedStatementCreator creator){
		if(creator==null)
			return;
		jdbcTemplate.update(creator);
	}
	
}
