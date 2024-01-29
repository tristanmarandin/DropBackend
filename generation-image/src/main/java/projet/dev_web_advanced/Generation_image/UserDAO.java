package projet.dev_web_advanced.Generation_image;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public Long createUser(User newUser) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(newUser);
        return newUser.getId();
    }

    public void modifyUser(User u) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(u);
    }

    public void deleteUser(User u) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image i WHERE i.creator.id = :user_id", Image.class);
        q.setParameter("user_id", u.getId());
        List<Image> list_image = q.getResultList();
        for( Image image : list_image) {
            if(image.isVisible()){
                image.setCreator(getUser(1L));
                session.merge(image);
            } else {
                session.delete(image);
            }
        }
        Query<Collection> query = session.createQuery("FROM Collection c WHERE c.creator.id = :user_id", Collection.class);
        query.setParameter("user_id", u.getId());
        for(Collection c : query.getResultList()) {
            session.delete(c);
        }
        session.delete(u);
    }

    public User getUser(Long Id) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<User> q = session.createQuery("FROM User u WHERE u.id = :id", User.class);
        q.setParameter("id", Id);
        List<User> users = q.getResultList();
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;  // or throw a custom exception
        }
    }

    public User getUser(String username, String password) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<User> q = session.createQuery("FROM User u WHERE u.name = :name AND u.password = :password", User.class);
        q.setParameter("name", username);
        q.setParameter("password", password);
        List<User> users = q.getResultList();
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;  // or throw a custom exception
        }
    }

    // For the CustomUserDetailsService 
    public User findByUsername(String username) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<User> q = session.createQuery("FROM User u WHERE u.name = :name", User.class);
        q.setParameter("name", username);
        List<User> users = q.getResultList();
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;  // or throw a custom exception
        }
    }

}
