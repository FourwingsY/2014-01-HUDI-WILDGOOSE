package next.wildgoose.framework.dao.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcTemplate {
	
	private BasicDataSource dataSource;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplate.class.getName());
	
	@Autowired
	public JdbcTemplate(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Object execute (String query, PreparedStatementSetter pss) {
		return execute(query, pss, null);
	}
	
	public Object execute (String query, PreparedStatementSetter pss, RowMapper rm) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Object result = null;
		
		try {
			conn = dataSource.getConnection();
			psmt = conn.prepareStatement(query);
			pss.setValues(psmt);
			
			if (rm == null) {
				result = false;
				psmt.execute();
				result = true;
			} else {
				rs = psmt.executeQuery();
				result = rm.mapRow(rs);
			}
			
		} catch (SQLException sqle) {
			LOGGER.debug(sqle.getMessage(), sqle);
			
		} finally {
			SqlUtil.closeResultSet(rs);
			SqlUtil.closePrepStatement(psmt);
			SqlUtil.closeConnection(conn);
		}
				
		return result;
	}
	
}
