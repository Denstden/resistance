package resistance;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Circle {

    final Point center;
    final int radius;
    final Color color;
    
    public Circle(Point point,int type) {
        center = point;
        if (type==0){
            radius = 10;
            color = Color.GRAY;
        } else {
            radius = 4;
            color = Color.BLACK;
        }
    }
    
    public void draw(Graphics page) {
        page.setColor(color);
        page.fillOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
    }
    
}
