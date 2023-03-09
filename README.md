# SimpleChat (fork)

![Modrinth downloads badge](https://img.shields.io/modrinth/dt/O2DN3m2D)
![Modrinth versions badge](https://img.shields.io/modrinth/game-versions/O2DN3m2D)
![issues badge](https://img.shields.io/github/issues/stanlystark/SimpleChat)
![java 17 badge](https://img.shields.io/badge/java-17+-orange?logo=java)

### This fork was created because the main project is [no longer maintained](https://github.com/cayennemc/SimpleChat/issues/11). Also added integration with FTB Teams.

_A simple chat mod for your server._

Works even in a single player game.

Just use `!<message>` for global chat or `#<message>` for world chat!

![Example image showing formatted chat messages](https://i.imgur.com/0DPfadW.png)
## Features
- FTB Teams integration _(tested 1802.2.10)_
- LuckPerms integration _(tested 5.4.25)_
- Global, world and local chat (you can turn it off)
- Color chat (you can turn it off)
- Reloading the configuration with the command
- For developers: Player chat event

## Configuration
The configuration is located in `<game or server directory>/config/simplechat.json`.
| Name | Description | Type |
|-|-|-|
| enable_chat_mod | Enables (true) or disables (false) chat handling by the mod. | boolean |
| enable_global_chat | Enables (true) or disables (false) the global chat. | boolean |
| enable_world_chat | Enables (true) or disables (false) the world chat. | boolean |
| enable_chat_colors | Enables (true) or disables (false) the use of color codes in the chat. | boolean |
| local_chat_format | Defines the appearance of the local chat. | String |
| global_chat_format | Defines the appearance of the global chat. | String |
| no_players_nearby_text | Defines a message for local chat when there are no players nearby. | String |
| no_players_nearby_action_bar | Enables (true) or disables (false) action bar message. | boolean |
| chat_range | Specifies the distance after which local chat messages will not be visible (if global chat is enabled). | int |

```json
{
  "enable_chat_mod": true,
  "enable_global_chat": true,
  "enable_world_chat": false,
  "enable_chat_colors": false,
  "local_chat_format": "%player%&7:&r &7%message%",
  "global_chat_format": "&8[&2G&8] &r%player%&7:&r &e%message%",
  "world_chat_format": "&8[&9W&8] &r%player%&7:&r &e%message%",
  "no_players_nearby_text": "&fNo players nearby. Please use &e!<message> &ffor global chat.",
  "no_players_nearby_action_bar": true,
  "chat_range": 100
}
```
You can use the placeholder `%player%` to specify the player's nickname and the placeholder `%message%` to specify their message in the chat.

- `%ftbteam%` FTB Team integration - display your party in chat.
- `%lp_group%` LuckPerms - display player group.
- `%lp_prefix%` LuckPerms - display player prefix.
- `%lp_suffix%` LuckPerms - display player suffix.

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
