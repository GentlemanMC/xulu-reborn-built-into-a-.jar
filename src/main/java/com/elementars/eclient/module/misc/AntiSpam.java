//
// Decompiled by Procyon v0.5.36
//

package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;

import java.util.regex.Pattern;

import dev.xulu.settings.Value;
import net.minecraft.network.play.server.SPacketChat;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiSpam extends Module {
    private final Value<Boolean> greenText = register(new Value<>("Green Text", this, true));
    private final Value<Boolean> discordLinks = register(new Value<>("Discord Links", this, true));
    private final Value<Boolean> webLinks = register(new Value<>("Web Links", this, true));
    private final Value<Boolean> announcers = register(new Value<>("Announcers", this, true));
    private final Value<Boolean> spammers = register(new Value<>("Spammers", this, true));
    private final Value<Boolean> insulters = register(new Value<>("Insulters", this, true));
    private final Value<Boolean> tradeChat = register(new Value<>("Trade Chat", this, true));
    private final Value<Boolean> duplicates = register(new Value<>("Duplicates", this, true));
    private final Value<Integer> duplicatesTimeout = register(new Value<>("Duplicates Timeout", this, 10, 1, 600));
    private final Value<Boolean> filterOwn = register(new Value<>("Filter Own", this, true));
    private final Value<Boolean> debug = register(new Value<>("Debug Messages", this, true));
    private ConcurrentHashMap<String, Long> messageHistory;
    SPacketChat sPacketChat;
    
    public AntiSpam() {
        super("AntiSpam", "Hides spam", Keyboard.KEY_NONE, Category.MISC, true);
    }
    @SubscribeEvent
    public void onPacket(ClientChatReceivedEvent event) {
        if (AntiSpam.mc.player != null && this.isToggled()) {
            if (this.detectSpam(event.getMessage().getUnformattedText())) {
                event.setCanceled(true);
            }
        }
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        this.messageHistory = new ConcurrentHashMap<String, Long>();
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        this.messageHistory = null;
    }

    private boolean detectSpam(final String message) {
        if (!this.filterOwn.getValue() && this.findPatterns(FilterPatterns.ownMessage, message)) {
            return false;
        }
        if (this.greenText.getValue() && this.findPatterns(FilterPatterns.greenText, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Green Text: " + message);
            }
            return true;
        }
        if (this.discordLinks.getValue() && this.findPatterns(FilterPatterns.discord, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Discord Link: " + message);
            }
            return true;
        }
        if (this.webLinks.getValue() && this.findPatterns(FilterPatterns.webLink, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Web Link: " + message);
            }
            return true;
        }
        if (this.tradeChat.getValue() && this.findPatterns(FilterPatterns.tradeChat, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Trade Chat: " + message);
            }
            return true;
        }
        if (this.announcers.getValue() && this.findPatterns(FilterPatterns.announcer, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Announcer: " + message);
            }
            return true;
        }
        if (this.spammers.getValue() && this.findPatterns(FilterPatterns.spammer, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Spammers: " + message);
            }
            return true;
        }
        if (this.insulters.getValue() && this.findPatterns(FilterPatterns.insulter, message)) {
            if (this.debug.getValue()) {
                sendDebugMessage("Insulter: " + message);
            }
            return true;
        }
        if (this.duplicates.getValue()) {
            if (this.messageHistory == null) {
                this.messageHistory = new ConcurrentHashMap<String, Long>();
            }
            boolean isDuplicate = false;
            if (this.messageHistory.containsKey(message) && (System.currentTimeMillis() - this.messageHistory.get(message)) / 1000L < this.duplicatesTimeout.getValue()) {
                isDuplicate = true;
            }
            this.messageHistory.put(message, System.currentTimeMillis());
            if (isDuplicate) {
                if (this.debug.getValue()) {
                    sendDebugMessage("Duplicate: " + message);
                }
                return true;
            }
        }
        return false;
    }

    private boolean findPatterns(final String[] patterns, final String string) {
        for (final String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(string).find()) {
                return true;
            }
        }
        return false;
    }

    private static class FilterPatterns
    {
        private static final String[] announcer;
        private static final String[] spammer;
        private static final String[] insulter;
        private static final String[] discord;
        private static final String[] greenText;
        private static final String[] tradeChat;
        private static final String[] webLink;
        private static final String[] ownMessage;

        static {
            announcer = new String[] { "I just walked .+ feet!", "I just placed a .+!", "I just attacked .+ with a .+!", "I just dropped a .+!", "I just opened chat!", "I just opened my console!", "I just opened my GUI!", "I just went into full screen mode!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just took a screen shot!", "I just swaped hands!", "I just ducked!", "I just changed perspectives!", "I just jumped!", "I just ate a .+!", "I just crafted .+ .+!", "I just picked up a .+!", "I just smelted .+ .+!", "I just respawned!", "I just attacked .+ with my hands", "I just broke a .+!", "I recently walked .+ blocks", "I just droped a .+ called, .+!", "I just placed a block called, .+!", "Im currently breaking a block called, .+!", "I just broke a block called, .+!", "I just opened chat!", "I just opened chat and typed a slash!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just changed perspectives, now im in .+!", "I just crouched!", "I just jumped!", "I just attacked a entity called, .+ with a .+", "Im currently eatting a peice of food called, .+!", "Im currently using a item called, .+!", "I just toggled full screen mode!", "I just took a screen shot!", "I just swaped hands and now theres a .+ in my main hand and a .+ in my off hand!", "I just used pick block on a block called, .+!", "Ra just completed his blazing ark", "Its a new day yes it is" };
            spammer = new String[] { "WWE Client's spammer", "Lol get gud", "Future client is bad", "WWE > Future", "WWE > Impact", "Default Message", "IKnowImEZ is a god", "THEREALWWEFAN231 is a god", "WWE Client made by IKnowImEZ/THEREALWWEFAN231", "WWE Client was the first public client to have Path Finder/New Chunks", "WWE Client was the first public client to have color signs", "WWE Client was the first client to have Teleport Finder", "WWE Client was the first client to have Tunneller & Tunneller Back Fill" };
            insulter = new String[] { ".+ Download WWE utility mod, Its free!", ".+ 4b4t is da best mintscreft serber", ".+ dont abouse", ".+ you cuck", ".+ https://www.youtube.com/channel/UCJGCNPEjvsCn0FKw3zso0TA", ".+ is my step dad", ".+ again daddy!", "dont worry .+ it happens to every one", ".+ dont buy future it's crap, compared to WWE!", "What are you, fucking gay, .+?", "Did you know? .+ hates you, .+", "You are literally 10, .+", ".+ finally lost their virginity, sadly they lost it to .+... yeah, that's unfortunate.", ".+, don't be upset, it's not like anyone cares about you, fag.", ".+, see that rubbish bin over there? Get your ass in it, or I'll get .+ to whoop your ass.", ".+, may I borrow that dirt block? that guy named .+ needs it...", "Yo, .+, btfo you virgin", "Hey .+ want to play some High School RP with me and .+?", ".+ is an Archon player. Why is he on here? Fucking factions player.", "Did you know? .+ just joined The Vortex Coalition!", ".+ has successfully conducted the cactus dupe and duped a itemhand!", ".+, are you even human? You act like my dog, holy shit.", ".+, you were never loved by your family.", "Come on .+, you hurt .+'s feelings. You meany.", "Stop trying to meme .+, you can't do that. kek", ".+, .+ is gay. Don't go near him.", "Whoa .+ didn't mean to offend you, .+.", ".+ im not pvping .+, im WWE'ing .+.", "Did you know? .+ just joined The Vortex Coalition!", ".+, are you even human? You act like my dog, holy shit." };
            discord = new String[] { "discord.gg" };
            greenText = new String[] { "^<.+> >" };
            tradeChat = new String[] { "buy", "sell" };
            webLink = new String[] { "http:\\/\\/", "https:\\/\\/" };
            ownMessage = new String[] { "^<" + AntiSpam.mc.player.getName() + "> ", "^To .+: " };
        }
    }
}
