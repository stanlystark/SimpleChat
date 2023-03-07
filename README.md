# SimpleChat
![modrinth_downloads](https://modrinth-utils.vercel.app/api/badge/downloads?id=O2DN3m2D&logo=true)
![modrinth_versions](https://modrinth-utils.vercel.app/api/badge/versions?id=O2DN3m2D&logo=true)
![java_17](https://img.shields.io/badge/java-17+-orange?logo=java)

_A simple chat mod for your server._

Works even in a single player game.

![Imgur](https://i.imgur.com/orpafbR.jpg)
## Features
- FTB Teams integration
- Global and local chat (you can turn it off)
- Color chat (you can turn it off)
- Reloading the configuration with the command
- For developers: Player chat event

## Configuration
The configuration is located in `<game or server directory>/config/simplechat.json`.
| Name | Description | Type |
|-|-|-|
| enable_chat_mod | Enables (true) or disables (false) chat handling by the mod. | boolean |
| enable_global_chat | Enables (true) or disables (false) the global chat. | boolean |
| enable_chat_colors | Enables (true) or disables (false) the use of color codes in the chat. | boolean |
| local_chat_format | Defines the appearance of the local chat. | String |
| global_chat_format | Defines the appearance of the global chat. | String |
| chat_range | Specifies the distance after which local chat messages will not be visible (if global chat is enabled). | int |

```json
{
  "enable_chat_mod": true,
  "enable_global_chat": false,
  "enable_chat_colors": false,
  "local_chat_format": "&7%ftbteam%&r%player%&7:&r &7%message%",
  "global_chat_format": "&8[&bG&8] &7%ftbteam%&r%player%&7:&r &e%message%",
  "chat_range": 100
}
```
You can use the placeholder `%player%` to specify the player's nickname and the placeholder `%message%` to specify their message in the chat.

You can reload the configuration without restarting the server or the game using the `/simplechat` command (requires [permission level](https://minecraft.fandom.com/wiki/Server.properties#op-permission-level) 1 or more).

## API
If you are a developer, you can use an event called when a player writes something to the chat.

Look [`me.vetustus.server.simplechat.api.event.PlayerChatCallback`](src/main/java/me/vetustus/server/simplechat/api/event/PlayerChatCallback.java). 
To control the behavior, use the [ChatMessage](src/main/java/me/vetustus/server/simplechat/api/event/PlayerChatCallback.java#L47) subclass, which can be used to cancel sending a message or change it.

*Example:*
```java
/**
 * Prohibits players from writing messages by canceling an event.
 */
PlayerChatCallback.EVENT.register((player, message) -> {
  PlayerChatCallback.ChatMessage chatMessage = new PlayerChatCallback.ChatMessage(player, message);
  chatMessage.setCancelled(true);
  return chatMessage;
});
```
## License
The MIT license is used.
