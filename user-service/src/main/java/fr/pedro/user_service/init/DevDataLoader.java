package fr.pedro.user_service.init;

import fr.pedro.user_service.entity.User;
import fr.pedro.user_service.dto.UserDTO;
import fr.pedro.user_service.repository.UserRepository;
import fr.pedro.user_service.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevDataLoader {



    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.init.dev-dataset:false}")
    private boolean devDatasetEnabled;

    @PostConstruct
    public void initUsers() {
        System.out.println("JE SUIS DANS LE FICHIER DevDataLoader ");
        System.out.println("devDatasetEnabled =  " + devDatasetEnabled);

        if (!devDatasetEnabled) return;

        System.out.println("⏳ Attente de la base de données pour insérer les utilisateurs...");

        int retries = 30;
        while (retries > 0) {
            try {
                if (userRepository.count() == 0) {
                    System.out.println("👤 Insertion des utilisateurs de développement...");

                    userService.save(UserDTO.builder()
                        .firstname("Pierre")
                        .lastname("Lambri Souvlaki")
                        .email("Pierre@Souvlaki.com")
                        .build());

                    userService.save(UserDTO.builder()
                        .firstname("Elisa")
                        .lastname("Austit Souvlaki")
                        .email("elisa@Souvlaki.com")
                        .build());

                    userService.save(UserDTO.builder()
                        .firstname("Idriss")
                        .lastname("Super Corpo")
                        .email("Idriss@corpo.com")
                        .build());

                    System.out.println("✅ Utilisateurs insérés !");
                } else {
                    System.out.println("📦 Données utilisateur déjà présentes, aucune insertion nécessaire.");
                }
                break;
            } catch (Exception e) {
                retries--;
                System.err.println("❌ Base de données indisponible, nouvelle tentative dans 3s... (" + retries + " restantes)");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }

        if (retries == 0) {
            System.err.println("❌ Impossible d’insérer les utilisateurs : la base est restée indisponible.");
        }
    }

}
