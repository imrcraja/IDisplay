# IDisplay

Minecraft Java 1.20.1 - Fabric

IDisplay is a lightweight client-side custom display-name mod by RC Empire.

What it changes:
- player nametag above the player
- TAB/player list
- chat messages containing the player's real username
- configured name persists in config/idisplay.json until cleared

Minecraft/Microsoft authentication, UUID, network identity and the actual account username are not changed.

Compatibility design:
IDisplay has no native libraries, no JNA, no JNI, no OSHI and no Unix-socket dependency. It does not install or replace JNA libraries and does not touch Essential authentication or Discord integration.

The mod does not modify, disable, replace, or block other mods. It only applies display-name rendering hooks.

The JNA/junixsocket errors shown by Android launchers can be dependencies of other software/mods. IDisplay does not load those native libraries and cannot repair a conflicting native library supplied by another mod.

Name persistence:
Once a display name is saved for the Minecraft UUID, it remains in the local IDisplay config until cleared. Because it is keyed by the account UUID, the same configured name is used after joining another world/server with that account.

Commands:
- /idisplay set <name>
- /idisplay clear
- /idisplay join <username>
- /idisplay leave <username>
- /idisplay reload
- /idisplay help

The join/leave commands are local chat simulations. They do not send fake server packets or alter another player's account.

Suggested usernames include Technoblade, Dream, MrBeast, DrDonut, Senpai, DreamXD, TommyInnit, GeorgeNotFound, Sapnap, Skeppy, BadBoyHalo, Grian, DanTDM, CaptainSparklez, Ranboo and Purpled. Custom names can always be typed.

Credit: RC Empire
