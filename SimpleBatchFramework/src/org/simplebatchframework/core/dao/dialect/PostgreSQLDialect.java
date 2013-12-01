package org.simplebatchframework.core.dao.dialect;

import static java.sql.Types.BIGINT;
import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simplebatchframework.core.Message;
import org.simplebatchframework.core.StaticString;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.annotation.Bind;
import org.simplebatchframework.core.bean.annotation.InputDB;
import org.simplebatchframework.core.dao.IDao;
import org.simplebatchframework.core.exception.BatchDataBaseRuntimeException;
import org.simplebatchframework.core.exception.BatchFileIORuntimeException;
import org.simplebatchframework.core.exception.BatchFrameworkRuntimeException;

public class PostgreSQLDialect extends AbstractDialect {

	@Override
	public Connection createConnection() {
		Connection connection = null;
		try {
			Class.forName(context.getConfiguration().getProperty(DRIVER_NAME));
			String url = context.getConfiguration().getProperty(URL);
			String user = context.getConfiguration().getProperty(USER);
			String pass = context.getConfiguration().getProperty(PASS);
			connection = DriverManager.getConnection(url, user, pass);
			if(connection == null) throw new BatchDataBaseRuntimeException(Message.ERROR_FAILED_CREATE_CONNECTION.toString());
			connection.setAutoCommit(Boolean.valueOf(context.getConfiguration().getProperty(AUTO_COMMIT)));
		} catch(ClassNotFoundException e) {
			throw new BatchDataBaseRuntimeException(e);
		} catch(SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
		return connection;
	}

	@Override
	public <T extends IBean, T2 extends IDao> ResultSet executeQuery(T bean, Class<T2> daoClass) {
		PreparedStatement pstmt = prepareStatement(bean, daoClass);
		try {
			return pstmt.executeQuery();
		} catch (SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
	}
	
	@Override
	public <T extends IBean, T2 extends IDao> int executeUpdate(T bean, Class<T2> daoClass) {
		PreparedStatement pstmt = prepareStatement(bean, daoClass);
		try {
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
	}
	
	private <T extends IBean, T2 extends IDao> PreparedStatement prepareStatement(T bean, Class<T2> daoClass) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(StaticString.SQL_FILE_DIRECTORY + daoClass.getSimpleName() + ".sql"));
			StringBuilder sql = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				sql.append(line + " ");
			}
			PreparedStatement pstmt = context.getConnection().prepareStatement(sql.toString());
			Field[] fields = bean.getClass().getDeclaredFields();
			for(Field field : fields) {
				Bind bind = field.getAnnotation(Bind.class);
				if(bind != null) {
					Class<?> clazz = field.getType();
					if(clazz != null) {
						int index = bind.value();
						field.setAccessible(true);
						if("Integer".equals(clazz.getSimpleName())) {
							pstmt.setInt(index, (Integer) field.get(bean));
						} else if("Long".equals(clazz.getSimpleName())) {
							pstmt.setLong(index, (Long) field.get(bean));
						} else if("BigDecimal".equals(clazz.getSimpleName())) {
							pstmt.setBigDecimal(index, (BigDecimal) field.get(bean));
						} else if("Date".equals(clazz.getSimpleName())) {
							pstmt.setDate(index, (Date) field.get(bean));
						} else if("Timestamp".equals(clazz.getSimpleName())) {
							pstmt.setTimestamp(index, (Timestamp) field.get(bean));
						} else if("String".equals(clazz.getSimpleName())) {
							pstmt.setString(index, (String) field.get(bean));
						}
					}
				}
			}
			if(Boolean.valueOf(context.getConfiguration().getProperty(RAW_SQL_DUMP).toLowerCase())) logger.debug(sql);
			if(Boolean.valueOf(context.getConfiguration().getProperty(BINDED_SQL_DUMP).toLowerCase())) logger.debug(pstmt);
			return pstmt;
		} catch(SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		} catch(IOException e) {
			throw new BatchFileIORuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public <T extends IBean, T2 extends IDao> List<T> resultSetToBean(ResultSet rs, Class<T> clazz, Class<T2> daoClass) {
		List<T> result = new ArrayList<T>();
		try {
			ResultSetMetaData md = rs.getMetaData();
			Set<String> columnNames = new HashSet<String>();
			for(int i = 0; i < md.getColumnCount(); i++) {
				int n = i + 1;
				columnNames.add(md.getColumnName(n).toLowerCase());
			}
			if(md.getColumnCount() != columnNames.size()) throw new BatchDataBaseRuntimeException(Message.ERROR_INVALID_COLUMN_NAME + daoClass.getSimpleName() + ".sql");
			while(rs.next()) {
				T bean = (T) clazz.newInstance();
				for(int i = 0; i < md.getColumnCount(); i++) {
					int n = i + 1;
					for(Field field : clazz.getDeclaredFields()) {
						InputDB annotation = field.getAnnotation(InputDB.class);
						if(annotation != null && md.getColumnName(n).toLowerCase().equals(annotation.value().toLowerCase())) {
							field.setAccessible(true);
							int columnType = md.getColumnType(n);
							switch(columnType) {
							case INTEGER:
								field.set(bean, rs.getInt(n));
								break;
							case BIGINT:
								field.set(bean, rs.getInt(n));
								break;
							case NUMERIC:
								field.set(bean, rs.getBigDecimal(n));
								break;
							case DATE:
								field.set(bean, rs.getDate(n));
								break;
							case TIMESTAMP:
								field.set(bean, rs.getTimestamp(n));
								break;
							case CHAR:
								field.set(bean, rs.getString(n));
								break;
							case VARCHAR:
								field.set(bean, rs.getString(n));
								break;
							}
						}
					}		
				}
				result.add(bean);
			}
		} catch(SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(InstantiationException e) {
			throw new BatchFrameworkRuntimeException(e);
		}
		return result;
	}
}
