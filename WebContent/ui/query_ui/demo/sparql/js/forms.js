var triple = "";
var selected_obj = [];

$(document).ready(function () {

    var obj0 = {
        id: "window0",
        title: "KinaseDomain",

        contents: [{
            id: "p00",
            subclass: "true",
            placeholder: "Group",
            parent: "window0",
            options: [],
            metaphor: "dd",
            json: "Group"
        }, {
            id: "p01",
            subclass: "true",
            placeholder: "Family",
            parent: "p00",
            options: [],
            metaphor: "dd",
            json: "family"
        }, {
            id: "p02",
            subclass: "true",
            placeholder: "SubFamily",
            parent: "p01",
            options: [],
            metaphor: "dd"
        }]
    };

    var obj1 = {
        id: "window1",
        title: "Gene",
        contents: [{
            id: "p10",
            placeholder: "Gene name",
            name: "hasPrimaryName",
            metaphor: "dd",
            json: "Gene"
        }, {
            id: "p11",
            placeholder: "Cell Location",
            name: "hasCellularLocation",
            metaphor: "dd",
            json: "CellularLocation"
        }]

    };


    var obj2 = {
        id: "window2",
        title: "Mutation",
        contents: [{
            id: "p20",
            metaphor: "dd",
            parent: "window2",
            subclass: "true",
            placeholder: "Mutation Type",
            options: ["Missense", "Coding Silent", "Insertion", "Deletion"]

        }, {
            id: "p21",
            metaphor: "text",
            subclass: "",
            placeholder: "Wild Type",
            name: "hasWildTypeResidue"
        }, {
            id: "p22",
            metaphor: "text",
            subclass: "",
            placeholder: "Mutant Type",
            name: "hasMutantType"
        }, {
            id: "p23",
            metaphor: "text",
            subclass: "",
            placeholder: "Position",
            name: "hasStartLocation"
        }, {
            id: "p24",
            metaphor: "text",
            subclass: "",
            placeholder: "PKA Position",
            name: "hasPKAStartLocation"
        }

        ]

    };

    var obj3 = {
        id: "window3",
        title: "Disease",
        contents: [{
            id: "p30",
            metaphor: "text",
            subclass: "",
            placeholder: "Disease name",
            name: "hasPrimaryName"
        }]

    };

    var obj4 = {
        id: "window4",
        title: "Pathway",
        contents: [{
            id: "p40",
            metaphor: "text",
            placeholder: "Pathway name",
            name: "hasPrimaryName"
        }]
    };

    var obj5 = {
        id: "window5",
        title: "Structure",
        contents: [{
            id: "p50",
            placeholder: "PDB ID",
            name: "hasPrimaryName",
            metaphor: "text"

        }, {
            id: "p51",
            placeholder: "kinase domain",
            name: "hasKinaseDomain",
            metaphor: "checkbx"

        }]
    };

    var obj6 = {
        id: "window6",
        title: "Sequence",
        contents: [{
            id: "p60",
            placeholder: "Sequence name",
            name: "hasPrimaryName",
            metaphor: "text"
        }]
    };


    var obj7 = {
        id: "window7",
        title: "Motif",
        contents: [{
            id: "p70",
            subclass: "true",
            parent: "window7",
            placeholder: "Motif Type",
            options: ["Structural Motif", "Sequence Motif"],
            metaphor: "dd"
        },

        {
            id: "p71",
            parent: "p70",
            placeholder: "Motif name",
            name: "hasPrimaryName",
            metaphor: "dd",
            options: []


        }, {
            id: "p72",
            parent: "p70",
            placeholder: "Start Position",
            name: "hasStartLocation",
            hideOn: "Structural Motif",
            metaphor: "text"

        }, {
            id: "p73",
            parent: "p70",
            placeholder: "End Position",
            name: "hasEndLocation",
            hideOn: "Structural Motif",
            metaphor: "text"

        }, {
            id: "p74",
            parent: "p70",
            placeholder: "PKA Start Position",
            name: "hasPKAStartLocation",
            hideOn: "Structural Motif",
            metaphor: "text"

        }, {
            id: "p75",
            parent: "p70",
            placeholder: "PKA End Position",
            name: "hasPKAEndLocation",
            hideOn: "Structural Motif",
            metaphor: "text"

        }]

    };


    var objects = [obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7];
    var prefix = "prokino";
    var query_select_init = '<div class="input-group"> <span>' +
        '<button type="button" id=';
    var result_vars = [];

    $.each(objects, function (obj_index, obj_value) {

        var obj_id = obj_value.id;
        var form_id = 'f' + obj_index.toString();
        var form = $('<form id="' + form_id + '" />');

        var obj_contents = obj_value.contents; //array

        $.each(obj_contents, function (h_index, h_value) {

            var h_id = h_value.id;
            var p_id = h_value.parent;
            var plc = h_value.placeholder;
            var hideOn = h_value.hideOn;
            var metaphor = h_value.metaphor;
            var query_select_id = h_id.replace('p', 's');
            var query_select = query_select_init + '"' + query_select_id + '" >?</button> </span>'; //renewed for each form element

            if (metaphor == "dd") {
                var s = $('</br><select style="width: 128px;" id="' + h_id + '" />');
                $('<option />', {
                    value: plc,
                    text: plc
                }).appendTo(s); //default


                if (h_value.json === undefined) { //highest level in hierarchy
                    if (obj_id == p_id) {
                        $.each(h_value.options, function (i, val) { //build all the options
                            $('<option />', {
                                value: val,
                                text: val
                            }).appendTo(s);
                        });

                    } //if 
                } //if
                else {
                    var jsonFile = "./json/" + h_value.json + ".json";
                    $.getJSON(jsonFile, function (data) {
                        $.each(data.Name, function (key, val) {
                            $('<option />', {
                                value: val,
                                text: val
                            }).appendTo(s);
                        }); //each

                    }); //getjson


                } //else
                s.appendTo(form);
            } //if dd


            else if (metaphor == "text") {
                var txt = $(query_select + '<input type="text" size="15" placeholder="' + plc + '" id="' + h_id + '"  ></div>');
                txt.appendTo(form);
            } //if text

            else if (metaphor == "checkbx") {
                var check = $('<input type="checkbox"  id="' + h_id + '" > ' + plc + '</div>');
                check.appendTo(form);
            }

            //implement click on the '?' next to each field
            $(document).on('click', '#' + query_select_id, function () {

                $('#' + h_id).val('?' + (plc.replace(" ", "")));
                result_vars.push('?' + (plc.replace(" ", "")));

            });

        }); //each content	

        //add Done button for each form
        var button_id = form_id.replace('f', 'b');
        $('</br><input type="button" value="Done" id="' + button_id + '" />').appendTo(form);
        $(document).on('click', '#' + button_id, function () {
            // change the style of the selected class
            $('#' + obj_id).css("background", "#3ADF00");
            $('#' + obj_id).popover('hide');

            //get the query variables and values
            var cls_var = "?" + obj_value.title.toLowerCase(); //class variable
            triple += cls_var + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n';

            $.each(obj_value.contents, function (h_index, h_value) {

                var h_id = h_value.id;
                var p_id = h_value.parent;
                var plc = h_value.placeholder;
                var pname = prefix + ":" + h_value.name;
                var metaphor = h_value.metaphor;
                var subclass = h_value.subclass;
                var query_select_id = 's' + h_id;
                var v = $('#' + h_id).val();

                if (metaphor === "checkbx") {
                    if ($('#' + h_id).is(":checked")) {
                        v = "true";
                    }
                }



                if (v !== plc && v !== "") {
                    if (metaphor === "dd" && subclass === "true") {
                        triple += cls_var + "\t rdf:type \t" + prefix + ":" + v + '.\n';
                        triple = triple.replace(cls_var + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n', "");
                    } else {
                        if (v.indexOf("?") !== -1) {
                            //alert(v);
                            v = v.toLowerCase();
                        } else {
                            v = '"' + v + '"';
                        }
                        triple += cls_var + '\t ' + pname + '\t ' + v + '.\n';
                    }
                } //if 

            }); //each content	
            //alert("triple :"+triple);

        });

        //implement popover containing the form
        var pop_place = "bottom";
        if (obj_index < 4) {
            pop_place = "top";
        }

        $("#" + obj_id).popover({
            html: true,
            content: form,
            placement: pop_place
        });

    }); //each object 

    //implement change for drop-downs
    $.each(objects, function (obj_index, obj_value) {
        var obj_contents = obj_value.contents; //array
        var obj_id = obj_value.id;
        $.each(obj_contents, function (h_index, h_value) {
            var h_id = h_value.id;
            var p_id = h_value.parent;
            var plc = h_value.placeholder;
            var hideOn = h_value.hideOn;
            var metaphor = h_value.metaphor;

            if (p_id != obj_id) {
                $(document).on('change', "#" + p_id, function () {
                    var selected = $(this).val();
                    if (metaphor == "dd") {
                        $('#' + h_id).empty();
                        $('<option />', {
                            value: h_value.placeholder,
                            text: h_value.placeholder
                        }).appendTo($('#' + h_id)); //default
                        //get the list from Json
                        //if (h_value.options[0].indexOf(".json") != -1) {
                        var jsonFile = "./json/" + selected.replace(" ", "") + ".json";
                        $.getJSON(jsonFile, function (data) {
                            $.each(data.Name, function (key, val) {
                                $('<option />', {
                                    value: val,
                                    text: val
                                }).appendTo($('#' + h_id));
                            }); //each

                        }); //getjson
                        //} if
                    } //dd

                    else if (metaphor == "text") {
                        if (hideOn != selected) {
                            $('#' + h_id).prop('disabled', false);
                            $('#' + h_id.replace("p", "s")).prop('disabled', false);
                        } else {
                            $('#' + h_id).prop('disabled', true);
                            $('#' + h_id.replace("p", "s")).prop('disabled', true);
                            //$( '#' + h_id.replace("p","s")).css( "background", "#E0E0E0" );
                            $('#' + h_id).css("background", "#E0E0E0");
                            $('#' + h_id).css("color", "#C0C0C0");
                            $('#' + h_id).val(plc);
                        }

                    } //text

                }); //change
            } //if 
        }); //each content
    }); //each object

});

function getTriple() {
    return triple;
}

function setTriple(newTriple) {
    triple = newTriple;
    alert(triple);
}