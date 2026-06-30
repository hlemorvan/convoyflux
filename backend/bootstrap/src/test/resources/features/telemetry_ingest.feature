Feature: Ingestion et diffusion de télémétrie

  Scenario: Un véhicule publie une position qui est persistée et diffusée
    Given un véhicule "bdd-v001" prêt à publier une position
    When le backend reçoit la position
    Then la position est persistée en base
    And la position est diffusée aux abonnés
