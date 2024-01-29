package projet.dev_web_advanced.Generation_image;

import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ImageDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void createImage(Image newImage) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(newImage);
    }

    public void modifyImage(Image i) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(i);
    }

    public void deleteImage(Image image) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(image);
    }

    public List<Image> getImage(String research) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image i WHERE i.prompt LIKE :research", Image.class);
        q.setParameter("research", research);
        return q.getResultList();
    }

    public List<Image> getImagesByCreator(Long userId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image i WHERE i.creator.id = :user_id", Image.class);
        q.setParameter("user_id", userId);
        return q.getResultList();
    }

    public List<Image> getImage(Collection c) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image i WHERE i.list_collections.id = :collection_id", Image.class);
        q.setParameter("collection_id", c.getId());
        return q.getResultList();
    }

    public List<Image> getAllImages() {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image", Image.class);
        return q.getResultList();
    }

    public Image getImage(Long Id) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Image> q = session.createQuery("FROM Image i WHERE i.id = :id", Image.class);
        q.setParameter("id", Id);
        return q.getResultList().get(0);
    }
}
