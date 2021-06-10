# Minerino | v.0.1
A Twitch Chat Integration for Minecraft!
## About
This mod aims to bring a lot of features from the standalone twitch chat software chatterino (https://chatterino.com/) to Minecraft.

Originally, this mod was planned as a fork of this modmod (https://github.com/PabloPerezRodriguez/twitch-chat) 
but I decided I wanted to be independent and started from scratch.

## Features
- Join multiple channels at once
- Switch between channels you want to read
- Messages are temporarily stored so you don't miss out on any conversation
- Type in the channel you want without any prefixes!
- Custom notifications / alert
- Ignore chatters

## Installation
This mod uses fabric (https://fabricmc.net/use/) as well as the fabric api (https://www.curseforge.com/minecraft/mc-mods/fabric-api) 
and is currently compatible with ```Minecraft 1.16.X```. After installing both of these, 
put the minerino.jar in your minecraft mods folder (https://github.com/Mooosik/minerino/releases).

## Getting Started
This mod is client-side only and should be compatible with most modpacks.
It currently does not feature an interface, all the basic setup is done via commands.

### Logging in

Use ```/minerino login <twitch username> <oAuth>``` to get started. If you don't have an authentication token already, you can generate one on this website: https://twitchapps.com/tmi/ . Keep this token secret, do not show it to anyone!
(This is only required once. Minerino will store your token in a config file. Use ```/minerino login``` from now on)

(Use ```/minerino logout``` to close the connection to twitch)
### Joining channels
To join a channel, use ```/minerino join <channel>```. You can join as many channels as you want. 
For better readability, you only see one channel at a time.

**Important!** You will send messages to the channel that is currently active. There is no prefix required!

(Use ```/minerino leave <channel>``` to leave the channel)
### Switching channels
To switch to a different channel (or the default Minecraft Chat), use ```/minerino switch <channel>```.
Minerino will still receive messages from other channels and store them for you.
If you switch to a different channel, Minerino will show you all the messages you missed.

### Notifications
Minerino allows you to add custom notifications, for example your username. 
Use ```/minerino alert add <notification>```

This means whenever your <notification> is mentioned in another chat, it will show you this message and play a sound.
It will always show you the message, even if you're currently reading another chat.

Use ```/minerino alert remove <notification>``` to remove a notification.

### Ignoring
Minerino allows you to ignore chatters. 
Use ```/minerino ignore <username>``` to ignore someone OR ```/minerino unignore <username>``` to unignore someone.

Notes: This does not apply to minecraft chat, only for twitch. 
Additionally, this is not linked to the ignore feature on twitch and will only apply for the minecraft client.

## Upcoming Features
- 1.17 support
- User interface for even more customization
- Emote support
- Forge Port

## Additional info
**Important!** This mod is not affiliated with the Chatterino software in any way!

This mod uses the twitch4j java api (https://github.com/twitch4j/twitch4j) 
as well as the spruce ui (https://github.com/LambdAurora/SpruceUI)
