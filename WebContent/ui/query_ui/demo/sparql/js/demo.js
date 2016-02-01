jsPlumb.ready(function () {

    //function : draw schema classes

    var titles = ["KinaseDomain", "Gene", "Mutation", "Disease", "Pathway", "Structure", "Sequence", "Motif"];

    var draw_window = function (title, id) {
        $('#statemachine-demo').append('<div class="ww" data-toggle="popover" id="' + id + '" name="' + title + '">' + title + '</div>');
    };
    var prefix = "prokino";


    var instance = jsPlumb.getInstance({
        Endpoint: ["Dot", {
            radius: 1
        }],
        HoverPaintStyle: {
            strokeStyle: "#1e8151",
            lineWidth: 2
        },
        ConnectionOverlays: [
            ["Arrow", {
                location: 1,
                id: "arrow",
                length: 10,
                foldback: 0.6
            }],
            ["Label", {
                cssClass: "aLabel"
            }]
        ],

        Container: "statemachine-demo"
    });

    //draw schema classes and define the position
    var pos_new = null;
    var pos_old = null;
    var pos0 = null;
    var top = 0;
    var left = 0;
    var width = 0;
    var pop_place = "";
    //draw classes
    $.each(titles, function (index, value) {
        var window_obj = {
            title: value,
            id: "window" + (index).toString()
        };

        draw_window(window_obj.title, window_obj.id);


    });

    //positioning the classes	
    $.each(titles, function (index, value) {

        if (index === 0) {
            pos0 = $('#window0').position();
        } else {
            pos_old = $('#window' + (index - 1).toString()).position();
            pos_new = $('#window' + index.toString()).position();
            width = $('#window' + (index - 1).toString()).width();
        }

        if (index <= 3) {
            top = pos0.top;
            pop_place = "top";
        } else {
            top = pos0.top + 150;
            pop_place = "bottom";
        }

        if (index === 0 || index === 4) {
            left = pos0.left;
        } else {
            left = pos_old.left + width + 200;
        }
        top += "px";
        left += "px";

        if (index > 0) {
            $('#window' + index.toString()).css({
                left: left,
                top: top
            });

        }

    });



    var windows = jsPlumb.getSelector(".statemachine-demo .ww");

    // initialise draggable elements.
    instance.draggable(windows);

    // bind a click listener to each connection; the connection is deleted. you could of course
    // just do this: jsPlumb.bind("click", jsPlumb.detach), but I wanted to make it clear what was
    // happening.

    /*  
   instance.bind("click", function (c, originalEvent) {
   		//if (c.paintStyleInUse.strokeStyle == '#3ADF00') {
   			//alert ("style ");}
   			//else {
   		alert("conn "+c.paintStyle.strokeStyle);
        c.setPaintStyle({lineWidth:3 , strokeStyle: '#3ADF00'});
        //}
    });
 */



    // bind a connection listener. note that the parameter passed to this function contains more than
    // just the new connection - see the documentation for a full list of what is included in 'info'.
    // this listener sets the connection's internal
    // id as the label overlay's text.
    instance.bind("connection", function (info) {
        //info.connection.getOverlay("label").setLabel(info.connection.id);
    });


    // suspend drawing and initialise.
    instance.doWhileSuspended(function () {
        var isFilterSupported = instance.isDragFilterSupported();
        // make each ".ep" div a source and give it some parameters to work with.  here we tell it
        // to use a Continuous anchor and the StateMachine connectors, and also we give it the
        // connector's paint style.  note that in this demo the strokeStyle is dynamically generated,
        // which prevents us from just setting a jsPlumb.Defaults.PaintStyle.  but that is what i
        // would recommend you do. Note also here that we use the 'filter' option to tell jsPlumb
        // which parts of the element should actually respond to a drag start.
        // here we test the capabilities of the library, to see if we
        // can provide a `filter` (our preference, support by vanilla
        // jsPlumb and the jQuery version), or if that is not supported,
        // a `parent` (YUI and MooTools). I want to make it perfectly
        // clear that `filter` is better. Use filter when you can.
        if (isFilterSupported) {
            instance.makeSource(windows, {
                filter: ".ep",
                anchor: "Continuous",
                connector: ["StateMachine", {
                    curviness: 20
                }],
                connectorStyle: {
                    strokeStyle: "#92b9d2",
                    lineWidth: 2,
                    // outlineColor: "transparent",
                    outlineWidth: 4

                },
                maxConnections: 5,
                onMaxConnections: function (info, e) {
                    alert("Maximum connections (" + info.maxConnections + ") reached");
                }
            });
        } else {
            var eps = jsPlumb.getSelector(".ep");
            for (var i = 0; i < eps.length; i++) {
                var e = eps[i],
                    p = e.parentNode;
                instance.makeSource(e, {
                    parent: p,
                    anchor: "Continuous",
                    connector: ["StateMachine", {
                        curviness: 5
                    }],
                    /* connectorStyle: {
                        strokeStyle: "#5c96bc",
                        lineWidth: 2,
                        outlineColor: "transparent",
                        outlineWidth: 4
                    },*/
                    maxConnections: 5,
                    onMaxConnections: function (info, e) {
                        alert("Maximum connections (" + info.maxConnections + ") reached");
                    }
                });
            }
        }
    });

    // initialise all '.w' elements as connection targets.
    instance.makeTarget(windows, {
        dropOptions: {
            hoverClass: "dragHover"
        },
        anchor: "Continuous",
        allowLoopback: true,
    });



    // make the relations (arrows)
    instance.connect({
        source: "window0",
        target: "window1",
        label: "codedBy"
    });
    instance.connect({
        source: "window1",
        target: "window2",
        label: "hasMutation"
    });
    instance.connect({
        source: "window1",
        target: "window4",
        label: "participatesIn"
    });
    instance.connect({
        source: "window1",
        target: "window6",
        label: "hasSequence"
    });
    instance.connect({
        source: "window2",
        target: "window6",
        label: "occursIn"
    });
    instance.connect({
        source: "window2",
        target: "window7",
        label: "locatedIn"
    });
    instance.connect({
        source: "window2",
        target: "window3",
        label: "implicatedIn"
    });
    instance.connect({
        source: "window6",
        target: "window5",
        label: "hasStructure"
    });
    instance.connect({
        source: "window6",
        target: "window7",
        label: "hasMotif"
    });
    var windowss = jsPlumb.getSelector(".statemachine-demo .ww");

    instance.bind("click", function (connection, originalEvent) {
        var triple = getTriple() + "?" + $("#" + connection.sourceId).attr("name").toLowerCase() +
            " " + prefix + ":" + connection.getLabel() + " ?" + $("#" + connection.targetId).attr("name").toLowerCase() + '.\n';
            
        setTriple(triple);   
        connection.setPaintStyle({
            strokeStyle: '#3ADF00'
        });

    });


    jsPlumb.fire("jsPlumbDemoLoaded", instance);

});