package org.vaadin.leaflet;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.function.SerializableConsumer;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Tag("div")
@JavaScript("//unpkg.com/leaflet@1.3.4/dist/leaflet.js")
@StyleSheet("//unpkg.com/leaflet@1.3.4/dist/leaflet.css")
@JavaScript("frontend://leafletConnector.js")
@StyleSheet("frontend://leafletCssHacks.css")
public class LeafletPointSelector extends Component implements HasSize {

    private final String id = UUID.randomUUID().toString();

    private Collection<LeafletPoint> points;

    public LeafletPointSelector() {
        setId(id);
        points = new ArrayList<>();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initConnector();
        if(!points.isEmpty()) {
            points.forEach(this::addPoint);
        }
    }

    private void initConnector() {
        runBeforeClientResponse(ui -> ui.getPage().executeJavaScript(
                "window.Vaadin.Flow.leafletConnector.initLazy($0)", getElement()));
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    public void addPoint(LeafletPoint point) {
        runBeforeClientResponse(ui -> getElement()
                .callFunction("$connector.addPoint", point.getLatitude(), point.getLongitude()));
    }

    public void setLocation(double latitude, double longitude) {
        runBeforeClientResponse(ui -> getElement()
                .callFunction("$connector.setLocation", latitude, longitude));
    }

    public void clearPoints() {
        getElement().callFunction("$connector.clear");
    }

    public Collection<LeafletPoint> getPoints() {
        return points;
    }

    @ClientCallable
    private void update(JsonArray jsPoints) {

        points.clear();

        for (int i = 0; i < jsPoints.length(); i++) {
            JsonObject jsPoint = jsPoints.get(i);
            LeafletPoint point = new LeafletPoint(jsPoint.get("latitude").asNumber(), jsPoint.get("longitude").asNumber());
            point.setId((int) jsPoint.getNumber("id"));
            point.setActiveMarker(jsPoint.getBoolean("active"));
            points.add(point);
        }

    }
}
