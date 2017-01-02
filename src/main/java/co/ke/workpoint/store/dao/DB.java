package co.ke.workpoint.store.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.omg.CORBA.Environment;

/**
 * <p>
 * This class provides utility methods for beginning/committing & rolling back
 * user transactions
 * 
 * <p>
 * Further it provides utility methods for retrieving
 * {@link EntityManagerFactory} and the {@link EntityManager}
 * 
 * <P>
 * Whenever an entity manager is requested, a corresponding
 * {@link UserTransaction} has to have been started/ began
 * 
 * <p>
 * A problem scenario that arises from this is one where {@link JBPMHelper}
 * which initializes an {@link Environment} variable and {@link TaskHandler}
 * using a {@link EntityManagerFactory} generates an {@link EntityManager}
 * without a {@link UserTransaction} - since the UserTransaction is application
 * managed. In this case, an exception is thrown with a 'no active transaction'
 * message.
 * 
 * <p>
 * To mitigate the above error, a {@link UserTransaction} transaction will be
 * started for every request and committed at the end of the request -see
 * {@link AbstractActionHandler}
 * 
 * @author duggan
 *
 */
public class DB {

	private static Logger log = Logger.getLogger(DB.class);

	private static ThreadLocal<DaoFactory> daoFactory = new ThreadLocal<>();
	private static ThreadLocal<JDBCConnection> jdbcConnectionBot = new ThreadLocal<>();

	private DB() {
	}

	public static void closeSession() {
		
	}

	/**
	 * Close the single hibernate em instance.
	 *
	 * @throws HibernateException
	 */
	public static void clearSession() {
		jdbcConnectionBot.get().dispose();
	}

	/**
	 * Begin a {@link UserTransaction}
	 * 
	 * <p>
	 * This is called whenever a new entity manager is requested
	 */
	public static void beginTransaction() {
		try {
			getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * This method commits a {@link UserTransaction}
	 * <p>
	 * A transaction is always generated whenever an entity manager is requested
	 */
	public static void commitTransaction() {
		try {
			getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Rollback a {@link UserTransaction}
	 */
	public static void rollback() {
		try {
			getConnection().rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static DaoFactory factory() {

		DaoFactory factory = daoFactory.get();

		if (factory == null) {
			factory = new DaoFactory();
			daoFactory.set(factory);
		}

		return factory;
	}

	private static void closeFactory() {
		if (daoFactory.get() == null)
			return;

		daoFactory.set(null);
	}

	public static Connection getConnection() {

		JDBCConnection bot = getJDBCBot();
		Connection conn = bot.getConnection();
		assert conn != null;

		return conn;

	}

	private static JDBCConnection getJDBCBot() {

		JDBCConnection connection = jdbcConnectionBot.get();

		if (connection == null) {
			synchronized (jdbcConnectionBot) {
				if ((connection = jdbcConnectionBot.get()) == null) {
					connection = new JDBCConnection();
					jdbcConnectionBot.set(connection);
				}
			}
		}
		return connection;
	}


}
