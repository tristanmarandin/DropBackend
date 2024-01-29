package projet.dev_web_advanced.Generation_image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;

import org.apache.commons.text.similarity.LevenshteinDistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @Autowired
    private ImageDAO dao = new ImageDAO();
    @Autowired
    private UserDAO userDAO = new UserDAO();
    @Autowired
    private CollectionDAO collection_DAO = new CollectionDAO();

    public record FormulaireGetImage(Number id){}

    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    @PostMapping("/api/image/getImage")
    public ResponseEntity<Image> getImage(@RequestBody FormulaireGetImage form) {
        Image i = dao.getImage(Long.parseLong(form.id.toString()));

        if (i != null) {
            return ResponseEntity.ok(i);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/image/getImagesCommunity")
    public ResponseEntity<List<Image>> getImagesCommunity() {
        List<Image> list_images = new ArrayList<Image>();
        for (Image i : dao.getAllImages()) {
            if (i.isVisible()) {
                list_images.add(i);
                System.out.println(i.getUrl_image());
            }
        }
        return ResponseEntity.ok(list_images);
    }

    public record FormulaireSet(Number id, boolean isVisible){}

    @PostMapping(value = "/api/image/setImage")
    public ResponseEntity<Image> setImage(@RequestBody FormulaireSet form) {
        Image i = dao.getImage(Long.parseLong(form.id.toString()));
        i.setVisible(form.isVisible);
        dao.modifyImage(i);
        Image imageUpdated = dao.getImage(i.getId());
        if (imageUpdated != null) {
            return ResponseEntity.ok(imageUpdated);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/api/image/addImageToCollection")
    public ResponseEntity<String> addImageToCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.add(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size() - 1) {
            return ResponseEntity.ok("Image added");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value = "/api/image/removeImageFromCollection")
    public ResponseEntity<String> removeImageFromCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.remove(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size() + 1) {
            return ResponseEntity.ok("Image removed");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    public record FormulaireEnvoie(
        String userID,
        int numberOfGeneratedImage,
        String instruction,
        ArrayList<String> selectedButtons,
        Number imageWidth,
        Number imageHeight,
        String seed,
        Number generationSteps,
        Number guidanceScale
        ) {}

    @PostMapping(value = "/api/image/generate")
    public ResponseEntity<?> generateImages(@RequestBody FormulaireEnvoie formulaireEnvoi) {
        User user = userDAO.getUser(Long.parseLong(formulaireEnvoi.userID));

        if ("PREMIUM_USER".equals(user.getRole()) || formulaireEnvoi.numberOfGeneratedImage() < 20) {
            
            JsonObject requestGson = new JsonObject();
            String engineeredPrompt = formulaireEnvoi.instruction + "((high quality,  best quality, masterpiece, beautiful and aesthetic:1.2))";

            requestGson.addProperty("key", "");
            requestGson.addProperty("model_id", "base-model");
            if (formulaireEnvoi.selectedButtons.contains("detailed")) {
                engineeredPrompt += ", ((intricate details, extremely detailed, highly detailed))";
            }
            if (formulaireEnvoi.selectedButtons.contains("colorful")) {
                engineeredPrompt += ", ((colorful, color))";
            }
            if (formulaireEnvoi.selectedButtons.contains("bw")) {
                engineeredPrompt += ", ((black and white, no color, colorless))";
            }
            if (formulaireEnvoi.selectedButtons.contains("highcontrast")) {
                engineeredPrompt += ", ((high contrast, dynamic lighting, faint light, contrasted))";
            }
            if (formulaireEnvoi.selectedButtons.contains("realistic")) {
                engineeredPrompt += ", ((hyper realistic, high resolution, top quality, 4K, 8K))";
            }
            if (formulaireEnvoi.selectedButtons.contains("myasaki")) {
                engineeredPrompt += ", studio ghibli style";
            }
            if (formulaireEnvoi.selectedButtons.contains("steampunk")) {
                engineeredPrompt += ", steampunk";
            }
            if (formulaireEnvoi.selectedButtons.contains("japanesestyle")) {
                engineeredPrompt += ", japanese style, japanese pattern,totem";
            }
            if (formulaireEnvoi.selectedButtons.contains("comics")) {
                engineeredPrompt += ", jim lee";
            }
            if (formulaireEnvoi.selectedButtons.contains("landscape")) {
                engineeredPrompt += ", scenery, ink, landscape";
            }
            requestGson.addProperty("prompt", engineeredPrompt);
            requestGson.addProperty("negative_prompt", "(((out of frame))), (worst quality, low quality:2), zombie,overexposure, monochromatic, watermark,text,bad anatomy,bad hand,extra hands,extra fingers,too many fingers,fused fingers,bad arm,distorted arm,extra arms,fused arms,extra legs,missing leg,disembodied leg,extra nipples, detached arm, liquid hand,inverted hand,disembodied limb, small breasts, loli, oversized head,extra body,completely nude, extra navel,easynegative,(hair between eyes),sketch, duplicate, ugly, huge eyes, text, logo, worst face, (bad and mutated hands:1.3), (blurry:2.0), horror, geometry, bad_prompt, (bad hands), (missing fingers), multiple limbs, bad anatomy, (interlocked fingers:1.2), Ugly Fingers, (extra digit and hands and fingers and legs and arms:1.4), ((2girl)), (deformed fingers:1.2), (long fingers:1.2),(bad-artist-anime), bad-artist, bad hand, extra legs, woman, girl");
            requestGson.addProperty("width", formulaireEnvoi.imageWidth.toString());
            requestGson.addProperty("height", formulaireEnvoi.imageHeight.toString());
            requestGson.addProperty("samples", "4");
            requestGson.addProperty("num_inference_steps", formulaireEnvoi.generationSteps.toString());
            requestGson.addProperty("safety_checker", "no");
            requestGson.addProperty("enhance_prompt", "yes");
            if (formulaireEnvoi.seed == null) {
                requestGson.add("seed", null);
            } else {
                requestGson.addProperty("seed", formulaireEnvoi.seed);
            }
            requestGson.addProperty("guidance_scale", formulaireEnvoi.guidanceScale);
            requestGson.addProperty("multi_lingual", "no");
            requestGson.addProperty("panorama", "no");
            requestGson.addProperty("self_attention", "no");
            requestGson.addProperty("upscale", "no");
            requestGson.add("embeddings_model", null);
            if (formulaireEnvoi.selectedButtons.contains("myasaki")) {
                requestGson.addProperty("lora_model", "miyazaki");
                System.out.println(formulaireEnvoi.selectedButtons);
            } else if (formulaireEnvoi.selectedButtons.contains("steampunk")) {
                requestGson.addProperty("lora_model", "steampunk");
            } else if (formulaireEnvoi.selectedButtons.contains("japanesestyle")) {
                requestGson.addProperty("lora_model", "japanese-style");
            } else if (formulaireEnvoi.selectedButtons.contains("comics")) {
                requestGson.addProperty("lora_model", "marveldc");
            } else if (formulaireEnvoi.selectedButtons.contains("landscape")) {
                requestGson.addProperty("lora_model", "landscape");
            }
            requestGson.addProperty("tomesd", "yes");
            requestGson.addProperty("clip_skip", "2");
            requestGson.addProperty("use_karras_sigmas", "yes");
            requestGson.add("vae", null);
            requestGson.add("lora_strength", null);
            requestGson.addProperty("scheduler", "UniPCMultistepScheduler");
            requestGson.add("webhook", null);
            requestGson.add("track_id", null);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            okhttp3.RequestBody body = okhttp3.RequestBody.create(requestGson.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url("https://stablediffusionapi.com/api/v4/dreambooth")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                JsonObject responseJson = JsonParser.parseString(resp).getAsJsonObject();

                List<Image> images = new ArrayList<Image>();

                JsonArray respUrls;
                String status = responseJson.get("status").toString();
                if (status.contains("processing")) {
                    respUrls = responseJson.getAsJsonArray("future_links");
                } else if (status.contains("success")) {
                    respUrls = responseJson.getAsJsonArray("output");
                } else {
                    throw (new Exception("Error in API call"));
                }

                for (JsonElement respUrl : respUrls) {
                    Image newImage = new Image();
                    newImage.setCreator(userDAO.getUser(Long.parseLong(formulaireEnvoi.userID)));
                    newImage.setPrompt(formulaireEnvoi.instruction);
                    newImage.setNegative_prompt("");
                    newImage.setModel("");
                    newImage.setSeed(formulaireEnvoi.seed);
                    newImage.setStep(formulaireEnvoi.generationSteps.toString());
                    newImage.setCfg_scale(formulaireEnvoi.guidanceScale.toString());
                    newImage.setUrl_image(respUrl.getAsString());
                    newImage.setNote(null);
                    newImage.setHeight(formulaireEnvoi.imageHeight.intValue());
                    newImage.setWidth(formulaireEnvoi.imageWidth.intValue());
                    newImage.setVisible(true);
                    dao.createImage(newImage);
                    images.add(newImage);
                }
                return ResponseEntity.ok(images);

            } catch (

            Exception e) {
                e.printStackTrace();
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok().body(new HashMap<String, String>() {{
                put("message", "The User needs to upgrade");
            }});
        }

    }

    @GetMapping("/api/image/getUserHistoric")
    public ResponseEntity<List<Image>> getImagesByUser(@RequestHeader("User-ID") Long userId) {
        List<Image> userImages = dao.getImagesByCreator(userId);

        if (userImages != null && !userImages.isEmpty()) {
            return ResponseEntity.ok(userImages);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public record ResearchForm(String research) {}

    @PostMapping("/api/image/getImagesCommunityByResearch")
    public ResponseEntity<List<Image>> getImagesByResearch(@RequestBody ResearchForm form) {
        String researchQuery = form.research.toLowerCase();
        List<Image> allImages = dao.getAllImages();
        List<Image> filteredImages = new ArrayList<>();

        for (Image image : allImages) {
            String imageDescription = image.getPrompt().toLowerCase();

            // Log the descriptions and distances
            int distance = levenshteinDistance.apply(imageDescription, researchQuery);
            System.out.println("Description: " + imageDescription + " | Query: " + researchQuery + " | Distance: " + distance);

            // Check for exact or close match before applying fuzzy logic
            if (imageDescription.contains(researchQuery) || distance <= 3) {
                if (image.isVisible()) {
                    filteredImages.add(image);
                }
            }
        }

        return ResponseEntity.ok(filteredImages);
    }
}
