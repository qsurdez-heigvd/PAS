package Utils

/** Contains the dictionary of the application, which is used to validate and
  * normalize words entered by the user.
  */
object Dictionary:
  // This dictionary is a Map object that contains valid words as keys and their normalized equivalents as values (e.g.
  // we want to normalize the words "veux" and "aimerais" in one unique term: "vouloir").
  val dictionary: Map[String, String] = Map(
    // BONJOUR
    "bonjour" -> "bonjour",
    "hello" -> "bonjour",
    "yo" -> "bonjour",
    // JE
    "je" -> "je",
    "j" -> "je",
    // VOULOIR
    "veux" -> "vouloir",
    "aimerais" -> "vouloir",
    "voudrais" -> "vouloir",
    // COMMANDER
    "commander" -> "commander",
    // CONNAITRE
    "connaître" -> "connaitre",
    "connaitre" -> "connaitre",
    // MON / SOLDE
    "mon" -> "mon",
    "solde" -> "solde",
    // COMBIEN
    "combien" -> "combien",
    // COUTER
    "coûter" -> "couter",
    "couter" -> "couter",
    "coûte" -> "couter",
    "coûtent" -> "couter",
    "coute" -> "couter",
    "coutent" -> "couter",
    // QUEL / ETRE / LE / PRIX / DE
    // TODO think about having one token for question solde ?
    "quel" -> "quel",
    "est" -> "etre",
    "suis" -> "etre",
    "le" -> "le",
    "l" -> "le",
    "prix" -> "prix",
    "de" -> "de",
    "d" -> "de",
    // PRODUIT
    "biere" -> "biere",
    "bieres" -> "biere",
    "bière" -> "biere",
    "bières" -> "biere",
    "croissant" -> "croissant",
    "croissants" -> "croissant",
    // MARQUE
    "maison" -> "maison",
    "cailler" -> "cailler",
    "farmer" -> "farmer",
    "wittekop" -> "wittekop",
    "punkipa" -> "punkipa",
    "punkipas" -> "punkipa",
    "jackhammer" -> "jackhammer",
    "ténébreuse" -> "tenebreuse",
    "tenebreuse" -> "tenebreuse",
    // ET / OU
    "et" -> "et",
    "ou" -> "ou",
  )
end Dictionary
