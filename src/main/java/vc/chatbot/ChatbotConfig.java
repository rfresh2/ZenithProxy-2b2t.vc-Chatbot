package vc.chatbot;

public class ChatbotConfig {
    public boolean enabled = true;
    public boolean publicChat = true;
    public boolean whispers = true;
    public boolean ignoreSelf = false;
    public String prefix = "!";
    public String apiKey = "";
    public int chatCooldownTicks = 40;
    public boolean responseRandomSuffix = true;
    public boolean responsePrefixEnabled = true;
    public String responsePrefix = "> ";
    public boolean responseRetry = true;
}
