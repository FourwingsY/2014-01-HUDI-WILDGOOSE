package next.wildgoose.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import next.wildgoose.model.ArticleCard;
import next.wildgoose.model.DatabaseConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleCardDAO {
	
	static Logger logger = LoggerFactory.getLogger(ArticleCardDAO.class.getName());
	
	public List<ArticleCard> findArticlesById(int reporterId) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		List<ArticleCard> articleCards = null;
		ArticleCard articleCard = null;
		
		try {
			// getting database connection to MySQL server
			conn = DatabaseConnector.getConnection();
			
			StringBuffer query = new StringBuffer();
			query.append("SELECT article.URL as url, article.title as title, ");
			query.append("article.section_id as section, article.content as content, article.datetime as datetime ");
			query.append("FROM article_author JOIN article ON article.URL = article_author.article_URL ");
			query.append("WHERE article_author.author_id = ?;");
			
			psmt = conn.prepareStatement(query.toString());
			psmt.setInt(1, reporterId);
			
			// sql에 query 요청
			rs = psmt.executeQuery();
			
			articleCards = new ArrayList<ArticleCard>();			
			while (rs.next()) {
				articleCard = new ArticleCard();
				articleCard.setUrl(rs.getString("url"));
				articleCard.setTitle(rs.getString("title"));
				articleCard.setSectionId(rs.getInt("section"));
				articleCard.setContent(rs.getString("content"));
				articleCard.setDatetime(rs.getTimestamp("datetime").toString());
				articleCards.add(articleCard);
			}
		}
		catch (SQLException sqle) {
			logger.debug(sqle.getMessage(),sqle);
			articleCards = null;
		}
		finally {
			// DatabaseUtil 만들필요 있음.
			// rs.close();
			DatabaseConnector.close();
		}
		return articleCards;
	}
}