# ZenithProxy 2b2t.vc Chatbot

In-game 2b2t chatbot, powered by https://api.2b2t.vc

How to install ZenithProxy plugins: https://wiki.2b2t.vc/Plugins/

## Commands

* `!coords` / `!locate`
* `!firstdeath` / `!fd`
* `!firstkill` / `!fk`
* `!joindate` / `!join` / `!jd` / `!firstseen`
* `!firstwords`
* `!help`
* `!kd` / `!kills` / `!deaths`
* `!seen` / `!lastseen`
* `!nolife`
* `!playtime` / `!pt`
* `!prioplayers` / `!priocount` / `!priomoney` / `!money`
* `!prio` / `!prioslots` / `!slots`
* `!queue`
* `!quote`
* `!report`
* `!time`
* `!tp`
* `!wc` / `!wordcount` / `!clout` / `!word`

### FAQ

#### How do i type `!` prefixed commands if i'm using ZenithProxy?

by default, the `!` prefix is used to run ZenithProxy commands

to run chatbot commands from public chat type `!!` instead. The first `!` will be canceled.

or you could change the default prefix to something else: `commandConfig ingame prefix <prefix>`

#### Can anyone run this?

Yes, that's the idea! Anyone can run their own 2b2t chatbot with this plugin.

This plugin uses the public 2b2t.vc api: https://api.2b2t.vc

You don't need an API key, but getting one may help avoid rate limits.

#### Will this plugin only work on 2b2t?

It will work on other MC servers, but https://api.2b2t.vc only tracks and returns data about 2b2t.
