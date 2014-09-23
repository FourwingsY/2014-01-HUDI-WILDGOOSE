package next.wildgoose.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import next.wildgoose.dto.Reporter;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoriteDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteDAO.class.getName());
	@Autowired private SqlSessionFactory sqlSessionFactory;

	public boolean addFavorite(final int reporterId, final String email) {
		int pk;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("email", email);
			map.put("reporterId", reporterId);
			pk = session.insert(
					"next.wildgoose.dao.FavoriteDAO.addFavorite",
					map);
		}
		return pk > 0;
	}
	
	public boolean removeFavorite(final int reporterId, final String email) {
		int pk;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("email", email);
			map.put("reporterId", reporterId);
			pk = session.delete(
					"next.wildgoose.dao.FavoriteDAO.removeFavorite",
					map);
		}
		return pk > 0;
		
	}
	
	public List<Integer> getFavoriteIds(final String email) {
		List<Integer> articleIds;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			articleIds = session
					.selectList(
							"next.wildgoose.dao.FavoriteDAO.getFavoriteIds",
							email);
		}
		return articleIds;
	}
	
	public List<Reporter> findFavoriteReporters(final String email) {
		List<Reporter> reporters;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			reporters = session.selectList(
					"next.wildgoose.dao.FavoriteDAO.findFavoriteReporters",
					email);
		}
		return reporters;
	}


	public boolean isFavorite(final String userId, final int reporterId) {
		Reporter reporter;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("reporterId", reporterId);
			reporter = session.selectOne(
					"next.wildgoose.dao.FavoriteDAO.isFavorite",
					map);
		}
		return reporter != null;
	}
}
