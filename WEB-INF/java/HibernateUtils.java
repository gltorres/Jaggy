import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import Entities.*;


public class HibernateUtils {


	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal();
	private static String path;

	public HibernateUtils(String path)
	{
		HibernateUtils.path = path;

		if (getSessionFactory() == null)
			this.newSessionFactory(path);
	}

	public static SessionFactory getSessionFactory()
	{
		return HibernateUtils.sessionFactory;
	}

	private static void setSessionFactory(SessionFactory sessionFactory)
	{
		HibernateUtils.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("deprecation")
	private static void newSessionFactory(String path)
	{

		Configuration config = new Configuration();
		File file = new File(path + "/java/hibernate.cfg.xml");
		System.out.println(file.getAbsolutePath());
		config.configure(file);
		HibernateUtils.setSessionFactory(config.buildSessionFactory());
	}

	public UsersEntity getUser(int id)
	{
		return (UsersEntity)this.findById(UsersEntity.class, id);
	}

	public UsersEntity getUser(String username)
	{
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		UsersEntity usr = null;

		try
		{
			tx = session.beginTransaction();

			@SuppressWarnings("unchecked")
			List <UsersEntity> usrList = (List <UsersEntity>)session.createCriteria( UsersEntity.class ).
			add( Restrictions.eq("name", username) ).list();

			tx.commit();
			tx = null;

			if (usrList != null)
				usr = usrList.get(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}


		return usr;
	}

	private HashtagsEntity getHashtag(String hashtag)
	{
		HashtagsEntity ht = null;

		Session session = HibernateUtils.getSession();
		Transaction tx = null;

		try
		{
			tx = session.beginTransaction();
			
			@SuppressWarnings("unchecked")
			List <HashtagsEntity> htList = (List <HashtagsEntity>)session.createCriteria( HashtagsEntity.class ).
			add( Restrictions.eq("hashtag", hashtag) ).list();
	
			tx.commit();
			tx = null;
	
			if (htList != null && htList.size() > 0)
			{
				ht = htList.get(0);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return ht;
	}

	public ArrayList<MessagesEntity> getTimelineMsgs(int userId)
	{
		return getTimelineMsgs(userId, 0);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MessagesEntity> getTimelineMsgs(int userId, int minMsgId)
	{
		ArrayList<MessagesEntity> msgs = new ArrayList<MessagesEntity>();
		Session session = HibernateUtils.getSession();
		Transaction tx = null;

		try
		{
			tx = session.beginTransaction();

			String strQry = "(SELECT m.* " + 
					"FROM Messages AS m " +
					"INNER JOIN Users AS u ON m.author_id = u.id " +
					"WHERE u.id = :id AND m.id > :minMsgId) " +
					"UNION DISTINCT " +
					"(SELECT m.* " +
					"FROM Messages AS m " +
					"INNER JOIN Followers AS f ON m.author_id = f.user_id " +
					"WHERE f.followed_id = :id AND m.id > :minMsgId) " +
					"UNION DISTINCT " +
					"(SELECT m.* " +
					"FROM Messages AS m " +
					"INNER JOIN Forwards AS fr ON m.id = fr.message_id " +
					"INNER JOIN Followers AS fol ON fol.user_id = fr.forwarder_id " +
					"WHERE fol.followed_id = :id AND fr.author_id != fol.followed_id AND m.id > :minMsgId) " +
					"ORDER BY publish_date DESC " +
					"LIMIT 50;";


			SQLQuery query = (SQLQuery) session.createSQLQuery(strQry).setInteger("id", userId).setInteger("minMsgId", minMsgId);
			query.addEntity(MessagesEntity.class);
			msgs = (ArrayList<MessagesEntity>) query.list();
			tx.commit();
			tx = null;

			for (MessagesEntity msg : msgs)
			{
				ArrayList<ForwardsEntity> fwds = new ArrayList<ForwardsEntity>();

				msg.setUser(getUser(msg.getAuthorId()));

				strQry = "SELECT fr.* " +
						"FROM Forwards as fr " +
						"INNER JOIN Followers AS fol ON fr.forwarder_id = fol.user_id " +
						"WHERE fr.message_id = :msgId AND fol.followed_id = :userId AND fr.author_id != fol.user_id AND fr.author_id != :userId ;";

				session = HibernateUtils.getSession();
				tx = session.beginTransaction();
				query = (SQLQuery) session.createSQLQuery(strQry).setInteger("msgId", msg.getId()).setInteger("userId", userId);
				query.addEntity(ForwardsEntity.class);
				fwds = (ArrayList<ForwardsEntity>)query.list();
				tx.commit();
				tx = null;

				for (ForwardsEntity f : fwds)
				{
					UsersEntity forwUsr = getUser(f.getId().getForwarderId());
					msg.setForwarderName(forwUsr.getName());
					msg.setForwarderAlias(forwUsr.getAlias());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return msgs;
	}

	public ArrayList<MessagesEntity> getProfileMsgs(int userId)
	{
		return getProfileMsgs(userId, 0);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MessagesEntity> getProfileMsgs(int userId, int minMsgId)
	{
		ArrayList<MessagesEntity> msgs = new ArrayList<MessagesEntity>();
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		try
		{
			UsersEntity myuser = getUser(userId);

			String strQry = "SELECT DISTINCT m.* " + 
					"FROM Messages AS m " +
					"INNER JOIN Users AS u ON m.author_id = u.id " +
					"LEFT JOIN Forwards AS f ON m.id = f.message_id " +
					"WHERE (u.id = :id OR f.forwarder_id = :id ) AND m.id > :minMsgId " +
					"ORDER BY m.publish_date DESC " +
					"LIMIT 50;";
			
			session = HibernateUtils.getSession();
			tx = session.beginTransaction();
			
			SQLQuery query = (SQLQuery) session.createSQLQuery(strQry).setInteger("id", userId).setInteger("minMsgId", minMsgId);
			query.addEntity(MessagesEntity.class);
			msgs = (ArrayList<MessagesEntity>) query.list();
			tx.commit();
			tx = null;

			for (MessagesEntity msg : msgs)
			{		
				UsersEntity usr = getUser(msg.getAuthorId());
				msg.setUser(usr);

				if (usr.getId() != userId)
				{
					msg.setForwarderName(myuser.getName());
					msg.setForwarderAlias(myuser.getAlias());
				}

				msg.setUser(usr);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return msgs;
	}

	public void forwardMessage(int forwardUserId, int msgId)
	{
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		try
		{

			tx = session.beginTransaction();

			ForwardsId fwdId = new ForwardsId(forwardUserId, msgId);

			@SuppressWarnings("unchecked")
			List <ForwardsEntity> list = (List <ForwardsEntity>)session.createCriteria( ForwardsEntity.class ).
			add( Restrictions.eq("id", fwdId) ).list();

			tx.commit();
			tx = null;

			// Si no se ha reenviado
			if (list.size() == 0)
			{
				MessagesEntity msg = (MessagesEntity)findById(MessagesEntity.class, msgId);

				if (msg != null)
				{   int author_id = msg.getAuthorId();

				System.out.println("Fwd: " + forwardUserId + ", author_id: " + author_id);



				ForwardsEntity fwd = new ForwardsEntity();                    
				fwd.setId(fwdId);
				fwd.setAuthorId(author_id);
				fwd.setForwardDate(new Date());

				persist(fwd);

				}

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}
	}

	public void postMessage(MessagesEntity msg)
	{
		this.persist(msg);

		for (String ht:msg.getHashtags())
		{
			HashtagsEntity hashtag = getHashtag(ht);

			if (hashtag == null)
			{
				hashtag = new HashtagsEntity();
				hashtag.setHashtag(ht);
				hashtag.setAuthorId(msg.getAuthorId());
				hashtag.setCreationDate(new Date());

				persist(hashtag);
			}

			MessageHashtagsEntity msght = new MessageHashtagsEntity(new MessageHashtagsId(msg.getId(), hashtag.getId()));

			persist(msght);
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MessagesEntity> getConversation(int msgId)
	{
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		ArrayList<MessagesEntity> msgs = new ArrayList<MessagesEntity>();

		try
		{
			String strQry = "(SELECT * FROM Messages as m WHERE m.id = :id) " + 
					"UNION DISTINCT " +
					"(SELECT * FROM Messages as m WHERE m.original_id = :id) " +
					"ORDER BY publish_date " + 
					"LIMIT 50;";

			session = HibernateUtils.getSession();
			tx = session.beginTransaction();
			SQLQuery query = (SQLQuery) session.createSQLQuery(strQry).setInteger("id", msgId);
			query.addEntity(MessagesEntity.class);
			msgs = (ArrayList<MessagesEntity>) query.list();
			tx.commit();
			tx = null;

			for (MessagesEntity m : msgs)
			{				
				m.setUser(getUser(m.getAuthorId()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return msgs;

	}

	public int numFollowers(int userId)
	{
		return getNumber(FollowersEntity.class, "id.userId", userId);
	}

	public int numFollowing(int userId)
	{
		return getNumber(FollowersEntity.class, "id.followedId", userId);
	}

	public int numMessages(int userId)
	{
		return getNumber(MessagesEntity.class, "authorId", userId);
	}

	private int getNumber(@SuppressWarnings("rawtypes") Class objClass, String row, int userId)
	{
		int num = 0;
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;

		try
		{
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria( objClass ).add( Restrictions.eq(row, userId));
			criteria.setProjection(Projections.rowCount());
			num = ((Long)criteria.uniqueResult()).intValue();

			tx.commit();
			tx = null;

		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return num;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MessagesEntity> getMessages(int userId)
	{		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		ArrayList<MessagesEntity> msgs = new ArrayList<MessagesEntity>();

		try
		{
			String strQry = "FROM MessagesEntity m WHERE m.authorId = :userid"; 

			tx = session.beginTransaction();
			Query query = session.createQuery(strQry).setInteger("userid", userId);
			msgs = (ArrayList<MessagesEntity>) query.list();
			tx.commit();

			for (MessagesEntity messagesEntity : msgs)
			{
				messagesEntity.setUser(getUser(userId));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return msgs;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MessagesEntity> getMessagesByHashtag(String hashtag)
	{
		ArrayList<MessagesEntity> msgs = new ArrayList<MessagesEntity>();
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;

		try
		{
			HashtagsEntity ht = getHashtag(hashtag);

			if (ht == null)
				return msgs;

			String strQry = "SELECT * FROM Messages as m " +
					"INNER JOIN MessageHashtags as mh ON m.id = mh.message_id " +
					"WHERE mh.hashtag_id = :hashtagid ORDER BY m.publish_date DESC " +
					"LIMIT 50;";

			session = HibernateUtils.getSession();
			tx = session.beginTransaction();
			SQLQuery query = (SQLQuery) session.createSQLQuery(strQry).setInteger("hashtagid", ht.getId());
			query.addEntity(MessagesEntity.class);
			msgs = (ArrayList<MessagesEntity>) query.list();
			tx.commit();
			tx = null;

			for (MessagesEntity m:msgs)
			{
				//System.out.println(m.getText());

				UsersEntity usr = getUser(m.getAuthorId());

				m.setUser(usr);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}

		return msgs;
	}

	public void persist(Object transientInstance) {
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.persist(transientInstance);
			tx.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}
	}

	public void attach(Object instance) {
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(instance);
			tx.commit();
			tx = null;
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}
	}

	public void delete(Object persistentInstance) {
		
		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.delete(persistentInstance);
			tx.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}
	}

	private Object findById(@SuppressWarnings("rawtypes") Class objClass, java.lang.Integer id) {

		Session session = HibernateUtils.getSession();
		Transaction tx = null;
		
		Object instance = null;
		
		try {
			tx = session.beginTransaction();
			instance = session.get(objClass, id);
			tx.commit();
			tx = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		}
		finally
		{
			HibernateUtils.closeSession();
		}
		
		return instance;
	}

	public static Session getSession() 
	{
		Session session = threadLocal.get();
		try
		{
			if (session == null || !session.isOpen()) 
			{
				if (sessionFactory == null) 
				{
					newSessionFactory(HibernateUtils.path);
				}
				session = (sessionFactory != null) ? sessionFactory.openSession() : null;
				threadLocal.set(session);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return session;
	}

	public static void closeSession() 
	{
		Session session = (Session) threadLocal.get();
		try
		{
			threadLocal.set(null);
			if (session != null) 
			{
				session.close();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
