package ru.nsunny.nyubustracker.entities;

public class Route {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String src;
    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }

    private String dest;
    public String getDest() {
        return dest;
    }
    public void setDest(String dest) {
        this.dest = dest;
    }

    private Route linkedRoute;
    public Route getLinkedRoute() {
        return linkedRoute;
    }
    public void setLinkedRoute(Route linkedRoute) {
        this.linkedRoute = linkedRoute;
        if(this.linkedRoute.linkedRoute!=this)
            linkedRoute.setLinkedRoute(this);
    }

    public Route(String name, String src, String dest){
        this.name = name;
        this.src = src;
        this.dest = dest;
    }
    public Route(String name, String src, String dest, Route linkedRoute){
        this(name,src,dest);
        this.setLinkedRoute(linkedRoute);
    }
}
