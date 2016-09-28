# Shiro (Discord-Bot) &nbsp; ![](https://i.imgur.com/CxYRxt0.png)
[![Dependency Status](https://www.versioneye.com/user/projects/57e7abacbd6fa600512e25ad/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57e7abacbd6fa600512e25ad) 
[![Build Status](https://travis-ci.org/sn0w/shiro.svg?branch=master)](https://travis-ci.org/sn0w/shiro) 
[![](https://images.microbadger.com/badges/version/sn0w/shiro.svg)](http://microbadger.com/images/sn0w/shiro) 
[![](https://images.microbadger.com/badges/image/sn0w/shiro.svg)](https://microbadger.com/images/sn0w/shiro)

Shiro is a multipurpose [Discord](https://discordapp.com/) bot written in [Groovy](http://groovy-lang.org/).<br>

A design goal was to keep the experience with her non-intrusive.<br>
That means there are currently neither modules for moderation/administration nor plans to make them.<br>
However since Shiro supports modules you can extend her to infinity!

### Can I see/suggest features/commands/...?
YES!<br>
Check [here](https://github.com/sn0w/shiro/issues) if anyone had the idea before or open a new issue :)

### Documentation and FAQ
Go to [meetshiro.xyz](http://meetshiro.xyz)

### Requirements
- Any OS and CPU that runs Java 8 [or Docker]
- About 64mb of free RAM
- About 20mb of free HDD space [The docker image needs additional 250mb]
- A MySQL server (anywhere. Maybe at bplaced? 😅)
- FFMPEG/libav and youtube-dl if you want to use the Music module
- Internet connection, duh

### Docker? Docker!
Just do a 
```
docker run -dv /docker/shiro:/data --link <mysql-container> sn0w/shiro:<full commit id or branch name>
```
and everything is ready! :)

### Testimonials! (OMG OMG OMG \o/)
[![](https://i.imgur.com/5rPB8iM.png)](https://github.com/serraniel)<br>
![](https://i.imgur.com/6m0MhFX.png)<br>
![](https://i.imgur.com/gMciLmO.png)<br>
![](https://i.imgur.com/HTO4AYP.png)<br>
![](https://i.imgur.com/5CzH1yW.png)<br>
![](https://i.imgur.com/Xtu1uNF.png)

### Notable Mentions (<3)
Shiro wouldn't exist without these awesome pieces of software!

- [Groovy by CodeHaus/Apache](http://groovy-lang.org)
- [Discord](http://discordapp.com)
- [Discord4J by austinv11](https://github.com/austinv11/Discord4J)
- [Reflections by Ronmamo](https://github.com/ronmamo/reflections)
- [Unirest by Mashape](http://unirest.io)
- [Chatter-Bot-Api by Pierre David Belanger](https://github.com/pierredavidbelanger/chatter-bot-api)
- [LogBack](http://logback.qos.ch/)
- [Youtube-DL by RG3](https://github.com/rg3/youtube-dl/)
- [FFMPEG](http://ffmpeg.org/)
- [libav](https://libav.org/)

### Shiro's Friends :tada:
Bots built by friends or awesome strangers

|Avatar|Name|Language|Link|
|:-:|:-:|:-:|:-:|
|![](http://i.imgur.com/PNcNRfM.png)|Ako-Chan|C#|[Serraniel/Ako-Discord-Bot-Loader](https://github.com/Serraniel/Ako-Discord-Bot-Loader)
|![](http://i.imgur.com/Tb0FZoZ.png)|Shinobu-Chan|Python 3|[Der-Eddy/discord_bot](https://github.com/Der-Eddy/discord_bot) <br> **Warning:** Shiro (anime character) hater
|![](http://i.imgur.com/vBnv5u2.png)|Winry Rockbell|JavaScript|[Devsome/Winry-Discordbot](https://github.com/Devsome/EliteBot) <br> **Warning:** Author likes and writes messy code!
|![](http://i.imgur.com/LyJh6OY.png)|Nadeko|C#|[Kwoth/NadekoBot](https://github.com/Kwoth/NadekoBot)
|![](https://i.imgur.com/PlRrEFk.png)|Luna|Python3|[Miraai/LunaBot](https://github.com/miraai/LunaBot)
