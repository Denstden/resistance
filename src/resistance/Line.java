package resistance;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Line {
    
    List<Point> points;
    Circle begin;
    Circle end;
    private final Color color;
    List<Button> resistors;
    
    public Line() {
        this.points = new ArrayList<>();
        this.resistors = new ArrayList<>();
        color = Color.BLACK;
    }
    
    public void addPoint(Point point){
        points.add(point);
    }
    
    public void draw(Graphics page) {
        page.setColor(color);
        for(int i=0;i<points.size()-1;i++){
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);
            page.drawLine(p1.x,p1.y,p2.x,p2.y);
        }
        if (begin!=null) begin.draw(page);
        if (end!=null) end.draw(page);
    }
}
