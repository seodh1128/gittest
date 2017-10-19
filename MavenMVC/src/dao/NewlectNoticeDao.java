package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import vo.Notice;

public class NewlectNoticeDao extends JdbcDaoSupport implements NoticeDao{
	
	private TransactionTemplate transactionTemplate;

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	//��ü�� �����ϰ� �����ϴ°� ���Ǳ׿��� �����ϴ�.
	private PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	// jdbc 1.5 �̻���� ��� ������ JdbcTemplate
	// private JdbcTemplate template;
	// �ֿ� ��� : ���� �Ķ���͸� ��� ������ Ư¡�� �ִ�.
	
	/*
	// JdbcDaoSupport��� Support Class�� ��ӹ޾Ƽ� �����ϸ� XML�� �����ص��� �ʾƵ� �̿� ����
	private JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}*/
	
	
	/*JdbcTemplate template = new JdbcTemplate();
	
	template.setDataSource(dataSource);*/
	
	
	
	public int getCount(String field, String query) throws ClassNotFoundException, SQLException
	{
		String sql = "SELECT COUNT(*) CNT FROM NOTICES WHERE "+field+" LIKE ?";
		
		//���� ���̱� ������ queryForInt ���, Ÿ�� ��ȯ�� �ʿ� �����Ƿ�(int) ����° �÷��� �ʿ���� Sql,?�� ������� ���븸 �߰����� �ָ� �ȴ�. 
		String serch = "%"+query+"%";
		
		return getJdbcTemplate().queryForObject(sql,new Object[]{serch},Integer.class);
		//return getJdbcTemplate().queryForObject(sql,Integer.class,serch);
		
		/* JSP(����) ���
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, "%"+query+"%");
		
		// 3. ���
		ResultSet rs = st.executeQuery();
		rs.next();
		
		int cnt = rs.getInt("cnt");
		
		rs.close();
		st.close();
		con.close();
		
		return cnt;*/
	}
	
	public List<Notice> getNotices(int page, String field, String query) throws ClassNotFoundException, SQLException
	{					
	//16�� - ������ (����Ʈ������) �����͸� �̾Ƴ��� ���ؼ�
	// ��� �޼ҵ忡�� ���̰� �ִ� ���׵�
	//1. ������
		String sql = "SELECT * FROM";
		sql += "(SELECT ROWNUM NUM, N.* FROM (SELECT * FROM NOTICES WHERE "+field+" LIKE ? ORDER BY REGDATE DESC) N)";
		sql += "WHERE NUM BETWEEN ? AND ?";	
	// '?' ���� : like��, BETWEEN�� , AND�� 
		
		int srow = 1 + (page-1)*15; // 1, 16, 31, 46, 61, ... an = a1 + (n-1)*d
		int erow = 15 + (page-1)*15; //15, 30, 45, 60, 75, ...
	// 2.	
		//case 1 : ���� ������ ������ ���� + �ڵ����� ä���ִ� ���
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<Notice>(Notice.class),new Object[]{"%"+query+"%", srow, erow});
		// ��� ���� �ߴ� ���� : BeanPropertyRowMapper - ���׸� Ÿ��
		//case 2 : ���� ������ ������ ���� �Լ� + �������� ä���ִ� ���
		/*return template.query(sql, new Object[]{"%"+query+"%", srow, erow}, new RowMapper<Notice>(){
			
			@Override
			public Notice mapRow(ResultSet rs, int rowNum) 
					throws SQLException{
				Notice n = new Notice();
				while(rs.next()){
					
					n.setSeq(rs.getString("seq"));
					n.setTitle(rs.getString("title"));
					n.setWriter(rs.getString("writer"));
					n.setRegdate(rs.getDate("regdate"));
					n.setHit(rs.getInt("hit"));
					n.setContent(rs.getString("content"));
					n.setFileSrc(rs.getString("fileSrc"));
					
				}
				return n;
				}
			}
		);*/
		
		
		/*String sql = "SELECT * FROM";
		sql += "(SELECT ROWNUM NUM, N.* FROM (SELECT * FROM NOTICES WHERE "+field+" LIKE ? ORDER BY REGDATE DESC) N)";
		sql += "WHERE NUM BETWEEN ? AND ?";
		
		//���� dao - �� �Լ����� ����� �ϹǷ� ��ȿ�����̴�.
		int srow = 1 + (page-1)*15; // 1, 16, 31, 46, 61, ... an = a1 + (n-1)*d
		int erow = 15 + (page-1)*15; //15, 30, 45, 60, 75, ...
		
		
		// 0. ����̹� �ε�
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, "%"+query+"%");
		st.setInt(2, srow);
		st.setInt(3, erow);
		// 3. ���
		ResultSet rs = st.executeQuery();
		
		List<Notice> list = new ArrayList<Notice>();
		
		while(rs.next()){
			Notice n = new Notice();
			n.setSeq(rs.getString("seq"));
			n.setTitle(rs.getString("title"));
			n.setWriter(rs.getString("writer"));
			n.setRegdate(rs.getDate("regdate"));
			n.setHit(rs.getInt("hit"));
			n.setContent(rs.getString("content"));
			n.setFileSrc(rs.getString("fileSrc"));
			
			list.add(n);
		}
		
		rs.close();
		st.close();
		con.close();
		
		return list;*/
	}
	
	public int delete(String seq) throws ClassNotFoundException, SQLException
	{
		String sql = "DELETE NOTICES WHERE SEQ=?";
		
		return getJdbcTemplate().update(sql, seq);
		/*
		// 2. ������ ���̽� ������ ���� ������ ���� �ڵ� �ۼ�
		String sql = "DELETE NOTICES WHERE SEQ=?";
		// 0. ����̹� �ε�
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		PreparedStatement st = con.prepareStatement(sql);	
		st.setString(1, seq);
		
		int af = st.executeUpdate();
		
		return af;
		*/
	}
	
	public int update(Notice notice) throws ClassNotFoundException, SQLException{
		
		String sql = "UPDATE NOTICES SET TITLE=?, CONTENT=?, FILESRC=? WHERE SEQ=?";
		
		return getJdbcTemplate().update(sql,
				notice.getTitle(),
				notice.getContent(),
				notice.getFileSrc(),
				notice.getSeq());
		/*
		// 2. ������ ���̽��� �����ϱ� ���� ������ �����ͺ��̽� ������ ���� �ڵ带 �ۼ�
		String sql = "UPDATE NOTICES SET TITLE=?, CONTENT=?, FILESRC=? WHERE SEQ=?";
		// 0. ����̹� �ε�
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, notice.getTitle());
		st.setString(2, notice.getContent());
		st.setString(3, notice.getFileSrc());
		st.setString(4, notice.getSeq());		
		
		int af = st.executeUpdate();
		
		return af;*/
	}
	
	public Notice getNotice(String seq) throws ClassNotFoundException, SQLException
	{
		//�����丵
		String sql = "SELECT * FROM NOTICES WHERE SEQ=?";
				
		/* case 1 : dataSource�� Ȱ���ϴ� ���
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
		dataSource.setUsername("seo");
		dataSource.setPassword("seo2017");*/
		
		// dispatcher�� Ȱ���Ͽ� XML���Ͽ� DataSource ������ ����صΰ� �ҷ��ͼ� Ȱ���ϴ� ���
		
		// Notice �ϳ��� �������Ƿ� QueryForObject�� ����.
		/*
		// ������ ������ ��� (���� ���� ������ ���� �̸��� ���� ��� ���)
		// QueryForObject - ����, RowMapper�� ���� NoticeŬ���� ������ �޾Ƽ� �Ķ���͸� ���� �� ������Ƽ�� �ѱ��.
		return template.queryForObject(sql,
				ParameterizedBeanPropertyRowMapper.newInstance(Notice.class));*/
		// QueryForObject - ����, �迭 ������Ʈ, ���۹�� 
		//return template.queryForObject(sql, new Object[]{seq} ,
		//		ParameterizedBeanPropertyRowMapper.newInstance(Notice.class));
		//ParameterizedBeanPropertyRowMapper.newInstance(Notice.class));
		
		
		// ������ �ڵ�ȭ���� �ʴ� ������ ���ϰ� ���� ���
		//
		/*return getJdbcTemplate().queryForObject(sql, 
				new RowMapper<Notice>(){
					
					@Override
					public Notice mapRow(ResultSet rs, int rowNum) 
							throws SQLException{
						
						Notice n = new Notice();
						n.setSeq(rs.getString("seq"));
						n.setTitle(rs.getString("title"));
						n.setWriter(rs.getString("writer"));
						n.setRegdate(rs.getDate("regdate"));
						n.setHit(rs.getInt("hit"));
						n.setContent(rs.getString("content"));
						n.setFileSrc(rs.getString("fileSrc"));
					
						return n;
					}
				}
			,new Object[]{seq} 
			);*/
		// JdbcTemplate() : queryForObject - (2,3) ������ �� �ٲ���� 
		return getJdbcTemplate().queryForObject(sql, 
				new RowMapper<Notice>(){
					
					@Override
					public Notice mapRow(ResultSet rs, int rowNum) 
							throws SQLException{
						
						Notice n = new Notice();
						n.setSeq(rs.getString("seq"));
						n.setTitle(rs.getString("title"));
						n.setWriter(rs.getString("writer"));
						n.setRegdate(rs.getDate("regdate"));
						n.setHit(rs.getInt("hit"));
						n.setContent(rs.getString("content"));
						n.setFileSrc(rs.getString("fileSrc"));
					
						return n;
					}
				}
			,new Object[]{seq} 
			);
		
		/*
		// 0. ����̹� �ε�
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		Statement st = con.createStatement();
		// 3. ���
		ResultSet rs = st.executeQuery(sql);
		rs.next();
		
		Notice n = new Notice();
		n.setSeq(rs.getString("seq"));
		n.setTitle(rs.getString("title"));
		n.setWriter(rs.getString("writer"));
		n.setRegdate(rs.getDate("regdate"));
		n.setHit(rs.getInt("hit"));
		n.setContent(rs.getString("content"));
		n.setFileSrc(rs.getString("fileSrc"));
		
		rs.close();
		st.close();
		con.close();
		
		return n;*/
	}

	public int insert(final Notice n) throws ClassNotFoundException, SQLException {

		//String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES( (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), ?, ?, 'newlec', SYSDATE, 0, ?)";
		
		//1. �ڵ��۾���
		//return template.update(sql, new Object[]{n.getTitle(),n.getContent(),n.getFileSrc()});
		//2. �����۾��� PreparedStatementSetter() ���
		/*return template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement st) throws SQLException {
				// TODO Auto-generated method stub
				// n�� ���������̹Ƿ� ���ο��� ����߿��� �ٸ��ʿ��� ������� ���ϰ� Final�� ����ؾ� �Ѵ�.  
				st.setString(1, n.getTitle());
				st.setString(2, n.getContent());
				st.setString(3, n.getFileSrc());
				}
			}
		);	*/	
		
		// 3. �������� �������� �ִ� ��� : ��쿡 ���� �������� �����
		// JdbcTemplate�� update������ PreparedStatementCreator�� �������� �ʴ´�.
		/*return getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES( (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), ?, ?, 'newlec', SYSDATE, 0, ?)";
				
				
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, n.getTitle());
				st.setString(2, n.getContent());
				st.setString(3, n.getFileSrc());
				return st;
				}
			}
		);*/
		
		// ?�� �����ʰ� ���� �����͸� ������ �� �� �ִ�.
		// ���� ���(?)�� ��쿣 ������ �������� ������, ���ÿ� ���� ������� �����͸� ���� �����ϴ�.
		String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES("
				+ " (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), :title, :content, 'newlec', SYSDATE, 0, :fileSource)";
		
		//BeanPropertySqlParameterSource �˾Ƽ� �Ķ���͸� �������ش�. - ?�δ� �ȵǰ� ���Ǿ� �ִ� ��쿡�� ��� ����
		return getJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(n));
		
		
		// ���� JSP ���
		
		/*Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contents", n.getContent());
		parameters.put("title", n.getTitle());
		parameters.put("fileSrc", n.getFileSrc());
		
		//return getJdbcTemplate().update(sql, parameters);
		//return getJdbcTemplate().update(sql, n.getContent(),n.getTitle(),n.getFileSrc());
		return getJdbcTemplate().update(sql, new MapSqlParameterSource());
		*/
		
		/*
		// 0. ����̹� �ε�
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// 1. ����
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
				"seo", "seo2017");
		// 2. ����
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, n.getTitle());
		st.setString(2, n.getContent());
		st.setString(3, n.getFileSrc());
		
		int af = st.executeUpdate();
		
		st.close();
		con.close();
		
		return af;*/
	}

	@Override
	@Transactional	//annotation�� Ȱ���� Ʈ������ 
	public void insertAndPointUpOfMember(Notice n, String uid) {
		
		// Ʈ������ ó����� 1 - PlatformTransactionManager�� Ȱ���� ���
		
		/*TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		// ù��° ���� - �Խñ� ���(insert�޼ҵ�� ����)�� ����
		String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES("
				+ " (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), :title, :content, 'newlec', SYSDATE, 0, :fileSrc)";
		// �ι�° ���� - ȸ�� ����Ʈ ���� ����
		String sqlPoint = "UPDATE \"MEMBER\" SET POINT = POINT + 1 WHERE \"UID\"=?";
		
		try{
		// JdbcTemplate�� �⺻������ AutoCommit�� �ǹǷ� ������ ���� �ѹ��ϰ� ���������� ����Ǹ� Ŀ���ϰ� ó���Ѵ� - Ʈ������ ����
		getJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(n));
		getJdbcTemplate().update(sqlPoint, uid);
		
		transactionManager.commit(status);
		}catch(DataAccessException exception){
			transactionManager.rollback(status);
			throw exception;
		}*/
		
		// Ʈ������ ���� 2 - Ʈ������ ���ø��� Ȱ���� 
		/*
		// ù��° ���� - �Խñ� ���(insert�޼ҵ�� ����)�� ����
		final String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES("
						+ " (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), :title, :content, 'newlec', SYSDATE, 0, :fileSrc)";
		// �ι�° ���� - ȸ�� ����Ʈ ���� ����
		final String sqlPoint = "UPDATE \"MEMBER\" SET POINT = POINT + 1 WHERE \"UID\"=?";
				
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(n));
				getJdbcTemplate().update(sqlPoint, uid);
				}
			}
		);
		*/
		// Ʈ������ ���� 3,4 - AOP/annotation�� Ȱ����  Ʈ������ ����
		// ù��° ���� - �Խñ� ���(insert�޼ҵ�� ����)�� ����
		String sql = "INSERT INTO NOTICES(SEQ, TITLE, CONTENT, WRITER, REGDATE, HIT, FILESRC) VALUES("
								+ " (SELECT MAX(TO_NUMBER(SEQ))+1 FROM NOTICES), :title, :content, 'newlec', SYSDATE, 0, :fileSrc)";
		// �ι�° ���� - ȸ�� ����Ʈ ���� ����
		String sqlPoint = "UPDATE \"MEMBER\" SET POINT = POINT + 1 WHERE \"UID\"=?";
						
		getJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(n));
		getJdbcTemplate().update(sqlPoint, uid);
	
	}
}
