package projet.dev_web_advanced.Generation_image;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.net.Webhook;
import com.stripe.model.Event;


@RestController
public class UserController {

    @Autowired
    private UserDAO dao = new UserDAO();
    @Autowired
    private CollectionDAO collection_dao = new CollectionDAO();
    @Autowired
    private ImageDAO image_dao = new ImageDAO();
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "api/user/createAccount")
    public ResponseEntity<User> createAccount(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO);
        System.out.println("user: " + userDTO.getUsername());
        System.out.println("pwd: " + userDTO.getPassword());
        
        User u = new User();
        u.setMail_adress("test");
        u.setName(userDTO.getUsername());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        try {
            dao.createUser(u);
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping(value = "api/user/connect")
    public ResponseEntity<?> connect(@RequestBody UserDTO userDTO) {
        try {
            System.out.println("test: " + userDTO.getUsername() + " & " + userDTO.getPassword());
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userDTO.getUsername(), userDTO.getPassword()
                )
            );
            System.out.println("test2" + userDTO.getUsername());

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
            final String jwtToken = jwtTokenUtil.generateToken(userDetails);

            // Prepare the response with the JWT token
            Map<String, Object> response = new HashMap<>();
            response.put("jwtToken", jwtToken);

            User user = dao.findByUsername(userDTO.getUsername());
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle exception and return an appropriate response
            // The exception could be due to invalid credentials or user not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @GetMapping(value = "api/user/disconnect")
    public void disconnect(@RequestBody Long id) {
        User u = dao.getUser(id);
        u.setConnected(false);
        dao.modifyUser(u);
    }

    @GetMapping(value = "api/user/getUserProfileInformation")
    public ResponseEntity<Map<String, String>> getUserProfileInformation(@RequestHeader("User-ID") Long userId) {
        User user = dao.getUser(userId);
        if (user != null) {
            Map<String, String> userProfileInfo = new HashMap<>();
            userProfileInfo.put("username", user.getName());
            userProfileInfo.put("profile_photo", user.getProfile_photo());
            return ResponseEntity.ok(userProfileInfo);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "api/user/getUserCollections")
    public ResponseEntity<List<Collection>> getUserCollections(@RequestBody Long id) {
        List<Collection> list_collections = collection_dao.findCollection(id);
        if (list_collections != null) {
            return ResponseEntity.ok(list_collections);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
/*
    @PostMapping(value = "api/user/getUserImages")
    public ResponseEntity<List<Image>> getUserImages(@RequestBody String userID) {
        User user = dao.getUser(Long.parseLong(userID));
        List<Image> list_images = image_dao.getImage(user);
        if (list_images != null) {
            return ResponseEntity.ok(list_images);
        } else {
            return ResponseEntity.noContent().build();
        }
    }*/


    @PostMapping(value = "/api/user/upgradeUser")
    public ResponseEntity<?> upgradeUser(@RequestBody String newRole) {
        // Configuration de Stripe
        Stripe.apiKey = "sk_test_51ObnG2GcdzOb3VR0DOPut9yOcbS4ZN7ktvZizODxTIC2a8uyQKioGAcBYljvuqkegcO97xbLbeG9iC3RAdeysIkV00njXU1VxO";

        // Création des paramètres de session
        SessionCreateParams.Builder sessionParamsBuilder = SessionCreateParams.builder()
                .setSuccessUrl("http://localhost:3000/payment-confirmation")
                .setCancelUrl("http://localhost:3000/payment-error");
                
                
                if ("PAYASYOUGO".equals(newRole)) {
                    // Configurer pour le rôle PAYASYOUGO
                    sessionParamsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount(499L) // Le prix en centimes pour PAYASYOUGO
                                    .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Upgrade to " + newRole)
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    ).setMode(SessionCreateParams.Mode.PAYMENT);
                } else if ("PREMIUM".equals(newRole)) {
                    // Configurer pour le rôle PREMIUM
                    sessionParamsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount(1999L) // Le prix en centimes pour PREMIUM
                                    .setRecurring(
                                        SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                            .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                            .build()
                                    )
                                    .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Upgrade to " + newRole)
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    ).setMode(SessionCreateParams.Mode.SUBSCRIPTION);
                }

        try {
            Session session = Session.create(sessionParamsBuilder.build());
            
            // Renvoyer l'URL de la session de paiement au client pour finaliser le paiement
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la session de paiement");
        }
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(
                payload, sigHeader, "votre_stripe_endpoint_secret"
            );

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();

                System.out.println(session);
                /* 
                Long userId = ... // Extraire l'ID de l'utilisateur depuis 'session'
                String newRole = ... // Déterminer le nouveau rôle

                // Mettre à jour le rôle de l'utilisateur dans la base de données
                User userToUpgrade = dao.getUser(userId);
                userToUpgrade.setRole(newRole);
                dao.modifyUser(userToUpgrade);
                */
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur dans le traitement du webhook");
        }
    }


    @PostMapping(value = "api/user/modifyUser")
    public ResponseEntity<User> modifyUser(@RequestBody User u) {
        dao.modifyUser(u);
        User userUpdated = dao.getUser(u.getId());
        if (userUpdated != null) {
            return ResponseEntity.ok(userUpdated);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "api/user/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody String id) {
        dao.deleteUser(dao.getUser(Long.parseLong(id)));
        User user = dao.getUser(Long.parseLong(id));
        if (user == null) {
            return ResponseEntity.ok("Account deleted with success");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }
}
