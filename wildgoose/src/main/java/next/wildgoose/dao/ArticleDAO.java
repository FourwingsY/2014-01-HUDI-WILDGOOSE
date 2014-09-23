package next.wildgoose.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import next.wildgoose.dto.Article;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticleDAO {

	@Autowired private SqlSessionFactory sqlSessionFactory;

	public List<Article> findArticlesById(final int reporterId) {
		List<Article> articles = null;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			articles = session.selectList(
					"next.wildgoose.dao.ArticleDAO.findArticlesById",
					reporterId);
		}
		return articles;
	}

	public List<Article> findArticlesByFavorite(final String email) {
		return findArticlesByFavorite(email, 0, 24);
	}

	public List<Article> findArticlesByFavorite(final String email,
			final int start, final int howMany) {
		List<Article> articles = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("start", start);
		map.put("howMany", howMany);
		try (SqlSession session = sqlSessionFactory.openSession()) {
			articles = session
					.selectList(
							"next.wildgoose.dao.ArticleDAO.findArticlesByFavorite",
							map);
		}
		return articles;
	}

	public int findNumberOfArticlesByFavorite(final String email) {
		int number = 0;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			number = session
					.selectOne(
							"next.wildgoose.dao.ArticleDAO.findNumberOfArticlesByFavorite",
							email);
		}
		return number;
	}

}
