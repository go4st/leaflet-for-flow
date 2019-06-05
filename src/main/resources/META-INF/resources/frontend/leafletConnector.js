window.Vaadin.Flow.leafletConnector = {
    initLazy: function (c) {
        // Check whether the connector was already initialized for the datepicker
        if (c.$connector) {
            return;
        }

        var markers = [];
        var activeMarker;
        var markerLayer;

        c.$connector = {

            addPoint: function (lat, lon) {

                if (!markerLayer) {
                    markerLayer = L.layerGroup();
                    this.mymap.addLayer(markerLayer);
                }

                var marker = L.marker([lat, lon], {
                    id: markers.length,
                    draggable: true
                }).addTo(markerLayer);

                marker.on('dragend', function (event) {
                    activeMarker = event.target;
                    c.$connector.center(event.target);
                    c.$connector.update();
                });

                markers.push(marker);
                activeMarker = marker;

                this.center(marker);
                this.update();
            },

            setLocation: function(latitude, longitude) {
                if (activeMarker) {
                    activeMarker.setLatLng(L.latLng(latitude, longitude));
                    this.center(activeMarker);
                    this.update();
                }
            },

            setActiveMarker: function (id) {
                activeMarker = markers[id];
                this.update();
            },

            center: function (point) {
                this.mymap.panTo(point.getLatLng());
            },

            update: function () {
                var markerCopy = [];
                markers.forEach(function (marker) {
                    markerCopy.push(
                        {
                            id: marker.options.id,
                            latitude: marker.getLatLng().lat,
                            longitude: marker.getLatLng().lng,
                            active: marker === activeMarker
                        });
                });
                c.$server.update(markerCopy);
            },

            clear: function () {
                markers = [];
                activeMarker = null;
                if (markerLayer) {
                    this.mymap.removeLayer(markerLayer);
                }
                markerLayer = null;
                this.update();
            }

        };

        var currentValue = "";

        const pushChanges = function () {
            c.$server.updateValue(currentValue)
        }

        var mymap = c.$connector.mymap = L.map(c.id).setView([61, 22], 5);

        L.DomUtil.addClass(mymap._container, 'crosshair-cursor-enabled');

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: 'Map data © <a href=\"https://openstreetmap.org\">OpenStreetMap</a> contributors',
            maxZoom: 18,
        }).addTo(mymap);

        c.style.cursor = "crosshair";

        function onMapClick(e) {
            if (activeMarker) {
                c.$connector.setLocation(e.latlng.lat, e.latlng.lng);
            } else {
                c.$connector.addPoint(e.latlng.lat, e.latlng.lng)
            }
        }

        mymap.on('click', onMapClick);
    }
};
