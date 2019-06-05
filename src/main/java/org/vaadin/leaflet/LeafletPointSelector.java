package org.vaadin.leaflet;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Tag("div")
@JavaScript("//unpkg.com/leaflet@1.3.4/dist/leaflet.js")
@StyleSheet("//unpkg.com/leaflet@1.3.4/dist/leaflet.css")
@JavaScript("frontend://leafletConnector.js")
@StyleSheet("frontend://leafletCssHacks.css")
public class LeafletPointSelector extends Component implements HasSize {

    private final String id = UUID.randomUUID().toString();

    private Collection<LeafletPoint> points = new ArrayList<>();

    private LeafletPoint activePoint;

    private List<Consumer<LeafletPoint>> valueChangeListeners = new ArrayList<>();;

    public LeafletPointSelector() {
        setId(id);
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

    public LeafletPoint getActivePoint() {
        return activePoint;
    }

    public void setActivePoint(LeafletPoint activePoint) {
        runBeforeClientResponse(ui -> getElement()
                .callFunction("$connector.setActiveMarker", activePoint.getId()));
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
        activePoint = null;

        for (int i = 0; i < jsPoints.length(); i++) {
            JsonObject jsPoint = jsPoints.get(i);
            LeafletPoint point = new LeafletPoint(jsPoint.get("latitude").asNumber(), jsPoint.get("longitude").asNumber());
            point.setId((int) jsPoint.getNumber("id"));

            boolean active = jsPoint.getBoolean("active");
            point.setActiveMarker(active);
            if (active) {
                activePoint = point;
            }

            points.add(point);
        }

        if (activePoint != null) {
            valueChangeListeners.forEach(consumer -> consumer.accept(activePoint));
        }
    }

    public Registration addValueChangeListener(Consumer<LeafletPoint> callback) {
        valueChangeListeners.add(callback);
        return () -> valueChangeListeners.remove(callback);
    }
}
