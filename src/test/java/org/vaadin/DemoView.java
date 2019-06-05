package org.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.leaflet.LeafletPoint;
import org.vaadin.leaflet.LeafletPointSelector;

import java.util.Collections;

@Route
public class DemoView extends Div {

    protected LeafletPointSelector map;

    public DemoView() {
        map = new LeafletPointSelector();
        map.setHeight("400px");
        add(map);

        Button b2 = new Button("Show content", e -> {
            Notification.show(map.getPoints().toString());
            map.getPoints().stream()
                    .filter(LeafletPoint::isActiveMarker)
                    .findFirst()
                    .ifPresent(active -> Notification.show("Selected: " + active.getId()));
        });
        add(b2);
        Button b4 = new Button("Set value Point(50, 10)", e -> {
            LeafletPoint createPoint = new LeafletPoint(10, 50);
            map.addPoint(createPoint);
        });
        add(b4);
        
        
        Button b3 = new Button("Show in Dialog", e -> {
            Dialog dialog = new Dialog();
            final LeafletPointSelector leafletMap = new LeafletPointSelector();
            leafletMap.setHeight("300px");
            leafletMap.setWidth("500px");
            dialog.add(leafletMap);
            dialog.open();
        });
        add(b3);

        Button b6 = new Button("Add point");
        b6.addClickListener(event -> map.addPoint(new LeafletPoint(Math.random()*10, Math.random()*50)));
        add(b6);

        Button b7 = new Button("Clear");
        b7.addClickListener(event -> map.clearPoints());
        add(b7);
    }
}
