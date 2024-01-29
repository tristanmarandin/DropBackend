package projet.dev_web_advanced.Generation_image;

public class AddImageToCollectionDTO {
    private Long collectionId;
    private Long imageId;

    // Getters and Setters
    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}
