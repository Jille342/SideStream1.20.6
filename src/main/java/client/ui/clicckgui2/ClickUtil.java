package client.ui.clicckgui2;

public class ClickUtil {

    public static boolean isHovered(double x, double y, double width, double height, double mouseX, double mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
    public static boolean isHovered2(double x, double y, double x2, double y2, double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x2 && mouseY <= y2;
    }
}
