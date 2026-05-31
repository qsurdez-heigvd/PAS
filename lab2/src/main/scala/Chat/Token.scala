package Chat

enum Token:
  case // Terms
    BONJOUR,
    JE,
    // Actions
    ETRE,
    VOULOIR,
    COMMANDER,
    CONNAITRE,
    COUTER,
    // Account related
    MON,
    SOLDE,
    // Price specific
    QUEL,
    LE,
    PRIX,
    DE,
    COMBIEN,
    // Logic Operators
    ET,
    OU,
    // Products
    PRODUIT,
    MARQUE,
    // Util
    NUM,
    EOL, // Use EOL to indicate the end of the line (i.e. there are no more tokens for this input)
    UNKNOWN // Use UNKNOWN if there is no match with any other tokens or words
end Token
