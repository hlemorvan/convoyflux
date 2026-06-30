Feature: Gestion de la backpressure au pont MQTT→Reactor

  Scenario: Deux positions reçues coup sur coup - la plus récente est conservée
    Given deux positions coup sur coup pour le même véhicule
    When les deux positions sont publiées dans le broadcast
    Then au moins la position la plus récente est diffusée
