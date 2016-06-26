package resistance;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;

public class SchemePanel extends javax.swing.JPanel {

    public boolean resistorSelected = false;
    public boolean lineSelected = false;
    public boolean clampSelected = false;
    public int resistorCount = 0;
    
    public List<Circle> clamps = new ArrayList<>();
    public List<Line> lines = new ArrayList<>();
    public List<Circle> circles = new ArrayList<>();
    Map<String, Double> om = new HashMap<>();
    
    MainFrame mainFrame;
    
    Line currentLine = new Line();
    
    public SchemePanel(MainFrame mainFrame) {
        initComponents();
        addMouseListener(new ComponentListener());
        this.mainFrame = mainFrame;
        this.setBackground(Color.white);
    }

    @Override
    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        currentLine.draw(page);
        lines.stream().forEach((line) -> {line.draw(page);});
        clamps.stream().forEach((circle) -> {circle.draw(page);});
    }
    
    public void clear(){
        for (Line line:lines){
            for (Button resistor:line.resistors){
                mainFrame.remove(resistor);
            }
        }
        lines.clear();
        clamps.clear();
        circles.clear();
        resistorCount = 0;
        resistorSelected = false;
        lineSelected = false;
        clampSelected = false;
    }
    
    public void addLine(){
        if (currentLine.end != null)
            lines.add(currentLine);
        currentLine = new Line();
    }
    
    public boolean combineConsistent(){
        System.out.println("((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.println();
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
        System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        
        boolean changed = false;
        for(Line line:lines){
            if (line.resistors.size()>1){
                changed = true;
                String text = "R";
                String formula = " = ";
                Double rOm = 0.0;
                for(Button button:line.resistors){
                    String t = button.getLabel();
                    rOm+=om.get(t);
                    if (formula.length()>3)
                        formula+=" + ";
                    formula+=t;
                    t = t.substring(1);
                    text+=t;
                    mainFrame.remove(button);
                }
                Button resistor = new Button(text);
                mainFrame.add(resistor);
                resistor.setSize(50,20);
                resistor.setVisible(true);
                resistor.setLocation(line.resistors.get(0).getLocation());
                line.resistors = new ArrayList<>();
                line.resistors.add(resistor);
                
                formula = text+formula+" = "+rOm+" Om";
                om.put(text, rOm);
                mainFrame.addFormula(formula+"\n");
            }
        }
        return changed;
    }
    
    public boolean combineParallel(){
        
        System.out.println("Parallel");
        System.out.println("((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.print("           ");
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        
        boolean changed = false;
        int i=0;
        while(i<lines.size()){
            Line line1 = lines.get(i);
            int j = i+1;
            while(j<lines.size()){
                Line line2 = lines.get(j);
                if (isParallel(line1,line2)){
                    changed = true;
                    
                    System.out.println("--------------------------------------------------------------");
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.print("           ");
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        System.out.print("Points:     ");
                        for(Point point1:line2.points){
                            System.out.print(point1+" ");
                        }
                        System.out.print("           ");
                        System.out.print("Resistors:     ");
                        for(Button resistor:line2.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    
                    Double rOm = 0.0;
                    String t1 = line1.resistors.get(0).getLabel();
                    String t2 = line2.resistors.get(0).getLabel();
                    rOm+=1.0/om.get(t1);
                    rOm+=1.0/om.get(t2);
                    rOm = 1/rOm;
                    String formula = " = 1/"+t1+" + 1/"+t2+" = "+rOm+" Om";
                    t1 = t1.substring(1);
                    t2 = t2.substring(1);
                    String newT = "R"+t1+t2;
                    om.put(newT, rOm);
                    formula = "1/"+newT+formula;
                    mainFrame.addFormula(formula+"\n");
                    
                    mainFrame.remove(line1.resistors.get(0));
                    mainFrame.remove(line2.resistors.get(0));
                    lines.remove(j);
                    
                    Button resistor = new Button(newT);
                    mainFrame.add(resistor);
                    resistor.setSize(50,20);
                    resistor.setVisible(true);
                    resistor.setLocation(line1.resistors.get(0).getLocation());
                    line1.resistors = new ArrayList<>();
                    line2.resistors = new ArrayList<>();
                    line1.resistors.add(resistor);
                    
                } else j++;
            }
            i++;
        }
        
        System.out.println("Before");
        System.out.println("((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.print("           ");
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        
        combineLines();
        
        System.out.println("After");
        System.out.println("((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.print("           ");
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        
        return changed;
    }
    
    private void combineLines(){
        int i=0;
        while(i<lines.size()){
            Line line1 = lines.get(i);
            int j = i+1;
            while(j<lines.size()){
                Line line2 = lines.get(j);
                if(line1.end == line2.begin && countDegree(line1.end)==2){
                    line2.points.remove(0);
                    line1.points.addAll(line2.points);
                    line1.end = line2.end;
                    line1.resistors.addAll(line2.resistors);
                    circles.remove(line2.begin);
                    lines.remove(j);
                } else if(line1.end == line2.end  && countDegree(line1.end)==2){
                    line2.points.remove(line2.points.size()-1);
                    Collections.reverse(line2.points);
                    line1.points.addAll(line2.points);
                    line1.end = line2.begin;
                    line1.resistors.addAll(line2.resistors);
                    circles.remove(line2.end);
                    lines.remove(j);
                } else if(line1.begin == line2.begin && countDegree(line1.begin)==2){
                    line2.points.remove(0);
                    Collections.reverse(line1.points);
                    line1.points.addAll(line2.points);
                    Collections.reverse(line1.points);
                    line1.begin = line2.end;
                    line1.resistors.addAll(line2.resistors);
                    circles.remove(line2.begin);
                    lines.remove(j);
                } else if(line1.begin == line2.end  && countDegree(line1.begin)==2){
                    line2.points.remove(line2.points.size()-1);
                    Collections.reverse(line2.points);
                    Collections.reverse(line1.points);
                    line1.points.addAll(line2.points);
                    Collections.reverse(line1.points);
                    line1.begin = line2.begin;
                    line1.resistors.addAll(line2.resistors);
                    circles.remove(line2.end);
                    lines.remove(j);
                } else j++;
            }
            i++;
        }
    }
    
    private int countDegree(Circle circle){
        int count = 0;
        for(Line line:lines){
            if (line.begin == circle) count++;
            if (line.end == circle) count++;
        }
        return count;
    }
    
    private boolean isParallel(Line line1, Line line2){
        return line1.begin == line2.begin && line1.end == line2.end ||
                line1.begin == line2.end && line1.end == line2.begin;
    }
    
    private class ComponentListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            System.out.println(event.getPoint());
            if (resistorSelected){
                Point point = event.getPoint();
                Line line = findNearestLine(point);
                Point nearest = getNearestPoint(line, point);
                double distance = distance(point,nearest);
                if (distance < 10) {
                    point = nearest;
                    String name = "R"+resistorCount++;
                    Button button = new Button(name);
                    om.put(name, 2.0);
                    mainFrame.addFormula(name+" = 2 Om\n");
                    mainFrame.add(button);
                    button.setSize(50,20);
                    button.setVisible(true);
                    button.setLocation(point.x+105,point.y+19);
                    line.resistors.add(button);
                }
                
                
                System.out.println("--------------------------------------------------------------");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point1:line1.points){
                            System.out.print(point1+" ");
                        }
                        System.out.println();
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
                
                
            } else if (clampSelected){
                if(clamps.size()<2){
                    Circle circle = new Circle(event.getPoint(),0);
                    if (clamps.size()==1){
                        addFirstLine(event.getPoint());
                        mainFrame.getjPanelClamp().setVisible(false);
                    mainFrame.getjPanelLines().setVisible(true);
                    mainFrame.getjPanelResistor().setVisible(true);
                    mainFrame.getParal().setVisible(true);
                    mainFrame.getPosl().setVisible(true);
                    mainFrame.getjLabel8().setVisible(true);
                    }
                    clamps.add(circle);
                }
            } else if (lineSelected) {
                currentLine.addPoint(event.getPoint());
                checkPoint();
            }
            repaint();
        }
        
        public void checkPoint(){
            Point end = currentLine.points.get(currentLine.points.size()-1);
            Line line = findNearestLine(end);
            Point nearest = getNearestPoint(line, end);
            double distance = distance(end,nearest);
            System.out.println(distance);
            if (distance < 10) {
                currentLine.points.set(currentLine.points.size()-1, nearest);
                if (currentLine.points.size()==1)
                    currentLine.begin = getCircle(currentLine.points.get(0));
                else {
                    currentLine.end = getCircle(nearest);
                    splitLine(currentLine.begin);
                    splitLine(currentLine.end);
                    addLine();
                    System.out.println("--------------------------------------------------------------");
                    for(Line line1 :lines){
                        System.out.print("Points:     ");
                        for(Point point:line1.points){
                            System.out.print(point+" ");
                        }
                        System.out.println();
                        System.out.print("Resistors:     ");
                        for(Button resistor:line1.resistors){
                            System.out.print(resistor.toString()+" ");
                        }
                        System.out.println();
                    }
                }
            }
            else {
                if (currentLine.points.size()==1)
                    addLine();
            }
        }
        
        private void splitLine(Circle circle){
            Point point = circle.center;
            Line line = findNearestLine(point);
            if (line.begin == circle || line.end == circle)
                return;
            
            int index = getSublineIndex(line,point);
            Line newLine = new Line();
            newLine.points.add(point);
            newLine.begin = circle;
            for(int i=index+1;i<line.points.size();i++)
                newLine.points.add(line.points.get(i));
            newLine.end = line.end;
            lines.add(newLine);
            
            while(line.points.size()>index+1)
                line.points.remove(index+1);
            line.points.add(point);
            line.end = circle;
            
            List<Button> resistors = line.resistors;
            line.resistors = new ArrayList<>();
            for(Button resistor:resistors){
                Point center = resistor.getLocation();
                Point p1 = getNearestPoint(line,center);
                Point p2 = getNearestPoint(newLine,center);
                if (distance(center,p1)<distance(center,p2))
                    line.resistors.add(resistor);
                else newLine.resistors.add(resistor);
            }
        }
        
        private int getSublineIndex(Line line, Point point){
            Point nearest = null;
            if (line.points.size()>0)
                nearest = line.points.get(0);
            int index = 0;
            for(int i=0;i<line.points.size()-1;i++){
                Point begin = line.points.get(i);
                Point end = line.points.get(i+1);
                Point currNearest = begin;
                
                Point pointN = getNearestPoint(point,begin,end);
                double sumDist = distance(pointN,begin) + distance(pointN,end);
                if (Math.abs(sumDist - distance(begin,end))<6.0)
                    currNearest = pointN;
                else {
                    if (distance(point,begin)<distance(point,end))
                        currNearest = begin;
                    else currNearest = end;
                }
                if (nearest == null || distance(point,currNearest)<distance(point,nearest)){
                    nearest = currNearest;
                    index = i;
                }
            }
            return index;
        }
        
        private Circle getCircle(Point point){
            Circle nearest = circles.get(0);
            for(Circle circle: circles){
                double dist = distance(point, circle.center);
                if (dist< distance(point, nearest.center))
                        nearest = circle;
            }
            if(distance(point, nearest.center)<10)
                return nearest;
            else {
                nearest = new Circle(point,1);
                circles.add(nearest);
                return nearest;
            }
        }
        
        private double distance(Point p1, Point p2){
            return Math.sqrt((p1.x-p2.x)*(p1.x - p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
        }
        
        private Line findNearestLine(Point point){
            double distance = 999999999;
            int i=0;
            int index = 0;
            for(Line line: lines){
                Point nearest = getNearestPoint(line, point);
                double dist = distance(point,nearest);
                if (dist<distance){
                    distance = dist;
                    index = i;
                }
                i++;
            }
            return lines.get(index);
        }
        
        private Point getNearestPoint(Line line, Point point){
            Point nearest = null;
            if (line.points.size()>0)
                nearest = line.points.get(0);
            for(int i=0;i<line.points.size()-1;i++){
                Point begin = line.points.get(i);
                Point end = line.points.get(i+1);
                Point currNearest = begin;
                
                Point pointN = getNearestPoint(point,begin,end);
                double sumDist = distance(pointN,begin) + distance(pointN,end);
                if (Math.abs(sumDist - distance(begin,end))<6.0)
                    currNearest = pointN;
                else {
                    if (distance(point,begin)<distance(point,end))
                        currNearest = begin;
                    else currNearest = end;
                }
                if (nearest == null || distance(point,currNearest)<distance(point,nearest))
                    nearest = currNearest;
            }
            return nearest;
        }
        
        private Point getNearestPoint(Point point, Point begin, Point end){
            double a = begin.x;
                double b = end.x - begin.x;
                double c = begin.y;
                double d = end.y - begin.y;
                
                double a1 = d;
                double b1 = -b;
                double c1 = - a*d + b*c;
                
                double a2 = b;
                double b2 = d;
                double c2 = -point.x*a2 - point.y*b2;
                
                double x = -(c1*b2 - c2*b1)/(a1*b2 - a2*b1);
                double y = -(a1*c2 - a2*c1)/(a1*b2 - a2*b1);
                
                return new Point((int)x,(int)y);
        }
        
        public void addFirstLine(Point currentPoint){
            Line line = new Line();
            Point start = clamps.get(0).center;
            Point end = currentPoint;
            Point middle = new Point(start.x, end.y);
            line.addPoint(start);
            line.addPoint(middle);
            line.addPoint(end);
            line.begin = new Circle(start,1);
            line.end = new Circle(end,1);
            circles.add(line.begin);
            circles.add(line.end);
            lines.add(line);
        }
        
        @Override
        public void mousePressed(MouseEvent event) {
        }

        @Override
        public void mouseReleased(MouseEvent event) {
        }

        @Override
        public void mouseEntered(MouseEvent event) {
        }

        @Override
        public void mouseExited(MouseEvent event) {
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 695, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
