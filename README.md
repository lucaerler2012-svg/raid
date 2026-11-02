# Dual Raids (Fabric, Minecraft 26.3)

Zwei Raids gleichzeitig im selben Dorf. Die Orte, an denen die Raider spawnen, bleiben wie im Vanilla-Spiel.

## Wie es funktioniert

Laeuft schon ein Raid und ein Spieler mit Bad Omen betritt das Dorf, startet statt der Verlaengerung ein
zweiter Raid. Maximal 2 pro Dorf (`MAX_RAIDS_PER_VILLAGE` in `DualRaidsMod.java`). Ein dritter Bad Omen
verlaengert wieder wie normal einen der beiden Raids.

## Bauen mit GitHub

1. Neues GitHub-Repo anlegen und diesen Ordner hochladen (inkl. `.github/`).
2. Tab **Actions** -> Workflow **build** laeuft automatisch.
3. Fertige `.jar` unter dem Lauf bei **Artifacts** -> `dual-raids-jar` herunterladen
   (die Datei ohne `-sources`).

## Installieren

Modrinth App -> Fabric-Profil fuer 26.3 -> Fabric API + diese `.jar` in den `mods`-Ordner.
Braucht Java 25 und Fabric Loader 0.19.5 oder neuer.
