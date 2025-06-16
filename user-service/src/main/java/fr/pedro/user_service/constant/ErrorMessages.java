package fr.pedro.user_service.constant;

public class ErrorMessages {
  private ErrorMessages() {

  }

      // === Validation errors ===
      public static final String FIRSTNAME_REQUIRED = "Le prénom est obligatoire";
      public static final String EMAIL_INVALID = "L'email doit être valide";
      public static final String EMAIL_REQUIRED = "L'email est obligatoire";
      public static final String LASTNAME_REQUIRED = "Le nom est obligatoire";

      // === Generic errors ===
      public static final String USER_NOT_FOUND = "Utilisateur introuvable";
      public static final String INTERNAL_ERROR = "Une erreur interne est survenue";


}
