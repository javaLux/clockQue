
## ClockQue Version 1.0.0



### Allgemeine Informationen

* Dies ist eine Open-Source Software und steht unter der "Apache License Version 2.0, January 2004"
* Es handelt sich um eine JavaFX Applikation und wurde durch eine Zusammenarbeit mit der openjfx.io Community entwickelt / gepflegt.
*
* ClockQue vereint eine Weltuhr und eine Wetterdaten App in einem.
* Die Uhrzeit wie auch die Wetterdaten, beziehen sich immer auf den aktuell ausgwählten Ort.
* Die Zeitverschiebung wird ausgehend von der aktuellen Zeitzone berechnet. Das heißt wird die App innerhalb der Zeitzone "Europe/Berlin"
* ausgeführt, wird die Zeitveschiebung anhand dieser Zeitzone berechnet und dem Nutzer angezeigt.



### Technologien

* [JavaFX]	(https://openjfx.io/)       Version:	18.0.1
* [OpenJDK]	(https://openjdk.java.net/) Version:	18.0.1.1



### Installation

* Es ist keine gesonderte Installation für die Nutzung erforderlich
* Alle notwendigen Voraussetzungen sind im Applikation Verzeichnis enthalten



### Untertützte Platformen

* Windows 10 x86-64



### Nutzung

* Um eine volle Nutzung der Applikation zu gewährleisten, ist eine Internetverbindung notwendig.
* Starten der Anwendung via Doopelklick auf die ClockQue.exe.
* Bei erstem Start der Applikation wird diese mit den Standard-Werten ausgeführt.
** Standartwerte sind: Ort -> Berlin, Temperatureinheit -> Celcius, Proxy-Einstellungen sind nicht gesetzt
*
* Falls ein Proxy-Server verwendet wird um eine Internetverbindung herzustellen, kann dieser unter Einstellungen (Zahnrad Symbol) hinzugefügt werden.
* Weiterhin kann die bevorzugte Temperatur Einheit (Celcius oder Fahrenheit) in den Einstellungen gewählt werden.
* Neue Orte können über die Combo-Box (blauer Pfeil) und einem Mausklick auf den Reiter "Ort hinzufügen" hinzugefügt werden.
* Im Textfeld kann nach einem beliebigen Ort gesucht werden.
* War die Suche erfolgreich, kann der bevorzugte Ort nun per Mausklick aus der Liste ausgewählt werden
* Falls die Suche ergebnislos bleibt, wird dies in einem Tooltip oberhalb des Textfeldes angezeigt.
* Ist ein neuer Ort ausgewählt, erscheint dieser als aktuell ausgewählt und wird in der Combo-Box angezeigt.
* Um einen Ort zu entfernen, muss die Combo-Box (blauer Pfeil) geöffnet werden und das rote Kreuz neben dem Ortsnamen betätigt werden.
*
* Detailiertere Informationen abrufen:
* Zu den aktuellen Wetterdaten, diese erhält man wenn man mit der Maus auf dem Wetter Icon oder der Temperatur Anzeige stehen bleibt.
* Die aktuelle Zeitverschiebung wird angezeigt wenn die Maus auf der Digitalen Uhr steht.



### Einschränkungn
* Es ist möglich diese Applikation mehrfach parallel auszuführen, dies kann aber dazu führen das eventuell keine Wetterdaten mehr abgerufen werden können
* Dies liegt daran das die maximale Anzahl von Wetterabfragen auf 2000 pro Tag begrenzt sind
* Falls keine Wetterdaten abgerufen werden können, wird dies mit einem Warnhinweis anstelle der Wetterdaten sichtbar gemacht
* Eine größere Anzahl von Wetterabfragen ist für spätere Versionen geplant
* Für erweiterte Fehlerinformationen kann das LOG-File unter "log/app.log" eingesehen werden


