package g_server.g_server.application.query.response;

public class VkrThemeView {
    private String vkrTheme;
    private boolean isVkrThemeEditable;

    public VkrThemeView(String vkrTheme, boolean isVkrThemeEditable) {
        this.vkrTheme = vkrTheme;
        this.isVkrThemeEditable = isVkrThemeEditable;
    }

    public String getVkrTheme() {
        return vkrTheme;
    }

    public void setVkrTheme(String vkrTheme) {
        this.vkrTheme = vkrTheme;
    }

    public boolean isVkrThemeEditable() {
        return isVkrThemeEditable;
    }

    public void setVkrThemeEditable(boolean vkrThemeEditable) {
        isVkrThemeEditable = vkrThemeEditable;
    }
}
