package projet.dev_web_advanced.Generation_image;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hibernate.Hibernate;

@RestController
public class CollectionController {
    
    @Autowired
    private CollectionDAO dao = new CollectionDAO();
    @Autowired
    private UserDAO user_DAO = new UserDAO();
    @Autowired
    private ImageDAO image_DAO = new ImageDAO();

    @PostMapping(value="api/collection/getCollection")
    public ResponseEntity<Collection> getCollection(@RequestBody Long Id_collection) {
        Collection col = dao.getCollection(Id_collection);
        if (col != null) {
            return ResponseEntity.ok(col);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/image/getUserCollection")
    public ResponseEntity<List<FindCollectionDTO>> getCollectionsByUser(@RequestHeader("User-ID") Long userId) {
        List<Collection> userCollection = dao.findCollection(userId);
        if (userCollection != null && !userCollection.isEmpty()) {
            List<FindCollectionDTO> collectionDTOs = userCollection.stream()
                    .map(col -> new FindCollectionDTO(col.getId(), col.getName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(collectionDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="api/collection/setCollection")
    public ResponseEntity<Collection> setCollection(@RequestBody Collection col) {
        if (col != null) {
            dao.modifyCollection(col);
            Collection new_col = dao.getCollection(col.getId());
            return ResponseEntity.ok(new_col);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="api/collection/createCollection")
    public ResponseEntity<Collection> createCollection(@RequestBody CollectionDTO collectionDTO) {
        Collection col = new Collection();
        col.setName(collectionDTO.getName());

        Long idCreator = Long.parseLong(collectionDTO.getIdCreator());
        col.setCreator(user_DAO.getUser(idCreator));
        System.out.println("user : " + user_DAO.getUser(idCreator));
        try {
            dao.createCollection(col);
            Collection new_col = dao.getCollection(col.getId());

            // Initialize the list_images collection
            Hibernate.initialize(new_col.getList_images());

            return ResponseEntity.ok(new_col);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value="api/collection/deleteCollection")
    public ResponseEntity<String> deleteCollection(@RequestBody Long id) {
        dao.deleteCollection(dao.getCollection(id));
        Collection deleted = dao.getCollection(id);
        if (deleted == null) {
            return ResponseEntity.ok("Collection successfully deleted");
        } else {
            return ResponseEntity.status(500).body("Connection not deleted");
        }
    }

    @PostMapping(value="api/collection/getImagesOfCollection2")
    public ResponseEntity<List<Image>> getImagesOfCollection(@RequestBody Long Id_collection) {
        List<Image> list = image_DAO.getImage(dao.getCollection(Id_collection));
        if (list != null) {
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="api/collection/addImageToCollection")
    public ResponseEntity<?> addImageToCollection(@RequestBody AddImageToCollectionDTO dto) {
        try {
            dao.addImageToCollection(dto.getCollectionId(), dto.getImageId());
            return ResponseEntity.ok("Image added to collection successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding image to collection");
        }
    }

    @PostMapping("/api/collection/removeImageFromCollection")
    public ResponseEntity<?> removeImageFromCollection(@RequestBody RemoveImageFromCollectionDTO dto) {
        try {
            dao.removeImageFromCollection(dto.getCollectionId(), dto.getImageId());
            return ResponseEntity.ok("Image removed from collection successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing image from collection");
        }
    }

    @PostMapping("/api/collection/getImagesOfCollection")
    public ResponseEntity<List<Image>> getImagesOfCollection(@RequestBody CollectionIdDTO collectionIdDTO) {
        Long collectionId = collectionIdDTO.getCollectionId();
        List<Image> images = dao.getImagesOfCollection(collectionId);
        if (images != null && !images.isEmpty()) {
            return ResponseEntity.ok(images);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
