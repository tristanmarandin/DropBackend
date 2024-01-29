package projet.dev_web_advanced.Generation_image;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CollectionDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void createCollection(Collection newCollection) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(newCollection);
    }

    public void modifyCollection(Collection c) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(c);
    }

    public void deleteCollection(Collection c) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(c);
    }

    public Collection getCollection(Long id) {
        Session session = this.sessionFactory.getCurrentSession();
        Collection c = session.find(Collection.class, id);
        return c;
    }

    public List<Collection> findCollection(Long userID) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Collection> query = session.createQuery("FROM Collection c WHERE c.creator.id LIKE :userID", Collection.class);
        query.setParameter("userID", userID);
        return query.getResultList();
    }

    public void addImageToCollection(Long collectionId, Long imageId) {
        Session session = this.sessionFactory.getCurrentSession();

        Collection collection = session.find(Collection.class, collectionId);
        Image image = session.find(Image.class, imageId);

        if (collection != null && image != null) {
            List<Image> images = collection.getList_images();
            if (!images.contains(image)) {
                images.add(image);
                collection.setList_images(images);
                session.merge(collection); // Persist the updated collection
            }
        }
    }

    public void removeImageFromCollection(Long collectionId, Long imageId) {
        Session session = this.sessionFactory.getCurrentSession();

        Collection collection = session.find(Collection.class, collectionId);
        Image image = session.find(Image.class, imageId);

        if (collection != null && image != null) {
            List<Image> images = collection.getList_images();
            if (images.contains(image)) {
                images.remove(image);
                session.merge(collection); // Persist the updated collection
            }
        }
    }

    public List<Image> getImagesOfCollection(Long collectionId) {
        Session session = this.sessionFactory.getCurrentSession();
        Collection collection = session.find(Collection.class, collectionId);

        if (collection != null) {
            // Initialize the list_images collection if it's lazily loaded
            Hibernate.initialize(collection.getList_images());
            return collection.getList_images();
        } else {
            // Return an empty list or null based on your design decision
            return null;
        }
    }
}
