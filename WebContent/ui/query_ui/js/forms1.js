var triple = "";
var selected_obj = [];
var form_array = {};


$(document).ready(function() {


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
            multi: "true",
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
        pcontents: [],
        contents: [{
            id: "p10",
            placeholder: "Gene name",
            name: "hasUniprotPrimaryName",
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
        isAbstract: "true",
        contents: [{
            id: "p20",
            metaphor: "dd",
            parent: "window2",
            subclass: "true",
            placeholder: "Mutation Type",
            name: "rdf:type",
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
            name: "hasStartLocation",
            getInt: "no"
        }, {
            id: "p24",
            metaphor: "text",
            subclass: "",
            placeholder: "PKA Position",
            name: "hasPKAStartLocation"
        }]
    };

    var obj3 = {
        id: "window3",
        title: "Cancer",
        contents: [{
            id: "p30",
            metaphor: "dd",
            subclass: "",
            placeholder: "Disease name",
            name: "hasPrimaryName",
            json: "Disease"
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
            name: "hasPKDomain",
            metaphor: "checkbx"

        }]
    };

    var obj6 = {
        id: "window6",
        title: "Sequence",
        contents: [{
            id: "p60",
            placeholder: "Sequence name",
            name: "rdfs:label",
            metaphor: "text"
        }]
    };

    var obj7 = {
        id: "window7",
        title: "Motif",
        isAbstract: "true",

        contents: [{
                id: "p70",
                subclass: "true",
                parent: "window7",
                placeholder: "Motif Type",
                name: "rdf:type",
                options: ["StructuralMotif", "SequenceMotif"],
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
                hideOn: "StructuralMotif",
                metaphor: "text",
                getInt: "no"

            }, {
                id: "p73",
                parent: "p70",
                placeholder: "End Position",
                name: "hasEndLocation",
                hideOn: "StructuralMotif",
                metaphor: "text",
                getInt: "no"

            }, {
                id: "p74",
                parent: "p70",
                placeholder: "PKA Start Position",
                name: "hasPKAStartLocation",
                hideOn: "StructuralMotif",
                metaphor: "text"

            }, {
                id: "p75",
                parent: "p70",
                placeholder: "PKA End Position",
                name: "hasPKAEndLocation",
                hideOn: "StructuralMotif",
                metaphor: "text"
            }
        ]
    };

    /**********************/

    var result_vars = [];
    var count_vars = [];

    var filterRegex = "FILTER regex";
    var filter = "FILTER";
    var filterStm = "";
    var form_count = 0;

    //Query Panel Buttons
    $('#qpanel').css("font-size", "11px");
    $(document).on('click', '#ex1', function() {
        var where = $('#qpanel').html();
        var groupby = "";
        var orderby = "";

        var select = "SELECT ";
        $.each(result_vars, function(index, value) {
            var selectvar = "(STR(" + value + ") as " + value.toUpperCase() + ")";
            select += selectvar + " ";

        });



        $.each(count_vars, function(index, value) {
            var selectvar = "(STR(" + value + ") as " + value.toUpperCase() + ")\n(Count(*) as " + value + "_Count)";
            groupby = "\n GROUP BY " + value;
            orderby = "\nORDER BY DESC("+value + "_Count"+")";
            //var selectvar = "(Count("+ value +") as "+value+"_Count)";

            select += selectvar + " ";

        });

        $('#qpanel').text("");
        if (select === "SELECT ") {
            select += "*";
        }
        //var qtext = (select + "\nWHERE {\n" +where+"\n}").replace(/\n/g,'<br/>');
        var qtext = select + "\nWHERE {\n" + where + "\n}" + groupby+orderby;
        $('#qpanel').append(qtext);

    });

    $(document).on('click', '#ex2', function() {
        location.reload();
    });

    ///////////////////

    var filterOps = [">", "<", "!="];
    var objects = [obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7];
    var prefix = "prokino";
    // var query_select_init = '<div class="input-group"> <span>' +
    // '<button type="button" class="roundbutton" id=';

    var instance_list = [];


    $.each(objects, function(obj_index, obj_value) {

        var obj_id = obj_value.id;
        var form_id = 'f' + obj_index.toString();
        var jsn_array = [];
        var instance_id = 0;
        var inst_to_jsn = {};
        var instance_list_id = form_id.replace('f', 'i');
        var add_button_id = form_id.replace('f', 'a');

        var form = $('<form class="form-group" id="' + form_id + '"></form>');
        $('<button type="button" class="generalbutton submitbutton" ' + ' id="' + add_button_id + '"> <span class="glyphicon glyphicon-plus"></span></button>').appendTo(form);
        $('<select class="formElement" style="width: 80px;" id="' + instance_list_id + '"> <option value="default">Instances</option>').appendTo(form);

        var obj_contents = obj_value.contents; //array

        $.each(obj_contents, function(h_index, h_value) {
            var h_id = h_value.id;
            var p_id = h_value.parent;
            var plc = h_value.placeholder;
            var hideOn = h_value.hideOn;
            var metaphor = h_value.metaphor;
            //var elmt_name = plc.toLowerCase().replace(/ /g, '') + instance_id;
            //var elmt_name = h_id;
            var elmt_name = h_value.name;
            //var query_select = query_select_init + '"' + query_select_id + '" >?</button> </span>'; //renewed for each form element			
            var query_select_id = h_id.replace('p', 's');
            var query_count_id = h_id.replace('p', 'c');

            var query_filter_id = h_id.replace('p', 'fl');

            var query_select = '<div class="input-group">' +
                '<div class="input-group-btn">' +
                '<button type="button" class="generalbutton ddbutton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" > <span class="glyphicon glyphicon-list"></span></button>' +
                '<ul class="dropdown-menu">' +
                '<li name="' + query_select_id + '" id="' + query_select_id + '"><a href="#">Select</a></li>' +
                '<li name="' + query_count_id + '" id="' + query_count_id + '"><a href="#">Count&#47;Group By</a></li>' +

                ' </ul>' +
                '</div>' +
                '<div class="input-group-btn">' +
                '<button type="button" class="generalbutton ddbutton dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" name="' + query_filter_id + '" id="' + query_filter_id + '"> <span class="glyphicon glyphicon-filter"></span></button>' +
                '<ul id="' + query_filter_id + '" class="dropdown-menu filter">' +
                '<li ><a href="#"><</a></li>' +
                '<li ><a href="#">></a></li>' +
                '<li ><a href="#">=</a></li>' +
                '<li ><a href="#">!=</a></li>' +
                '<li role="separator" class="divider"></li>' +
                '<li ><a href="#">exact match</a></li>' +
                '<li ><a href="#">contains</a></li>' +
                '<li ><a href="#">starts with</a></li>' +
                '<li ><a href="#">ends with</a></li>' +
                ' </ul>' +
                '</div>';



            if (metaphor == "dd") {
                var ss = $(query_select);
                var s = $('<select class="formElement" style="width: 100px;" placeholder="' + plc + '" name="' + elmt_name + '" id="' + h_id + '" />');
                $('<option />', {
                    value: plc,
                    text: plc
                }).appendTo(s); //default

                $('<option />', {
                    value: '?' + (plc.replace(/ /g, '')),
                    text: '?' + (plc.replace(/ /g, ''))
                }).appendTo(s); //default


                if (h_value.json === undefined) { //highest level in hierarchy
                    if (obj_id == p_id) {
                        $.each(h_value.options, function(i, val) { //build all the options
                            $('<option />', {
                                value: val,
                                text: val
                            }).appendTo(s);
                        });

                    } //if 
                } //if
                else {
                    var jsonFile = "../ui/query_ui/json/" + h_value.json + ".json";
                    $.getJSON(jsonFile, function(data) {
                        $.each(data.Name, function(key, val) {
                            $('<option />', {
                                value: val,
                                text: val
                            }).appendTo(s);
                        }); //each

                    }); //getjson


                } //else
                s.appendTo(ss);
                form.append(ss);
                // ss.appendTo(form);
            } //if dd
            else if (metaphor == "text") {
                var txt = $(query_select + '<input type="text" class="textbox formElement" size="15" placeholder="' + plc + '" name="' + elmt_name + '" id="' + h_id + '"  ></div>');
                txt.appendTo(form);
            } //if text
            else if (metaphor == "checkbx") {
                var check = $(query_select + '<input type="checkbox" name="' + elmt_name + '" id="' + h_id + '" checked> ' + '<label class="formElement">' + plc + '</label> </div>');
                check.appendTo(form);
            }

            /***********************************/

            //implement click on the 'Select' for each field
            $(document).on('click', '#' + query_select_id, function() {
                //toggle
                /*   if ($("#" + query_select_id).hasClass("roundbuttonSelect")) { //deselect

                       $("#" + query_select_id).addClass("roundbutton");
                       $("#" + query_select_id).removeClass("roundbuttonSelect");
                       $('#' + h_id).val("");
                       $('#' + h_id).attr("placeholder", plc).placeholder();
                       //$('#' + h_id).val(plc);

                   } else {
                       $('#' + h_id).val('?' + (plc.replace(/ /g, '')) + instance_id);
                       result_vars.push('?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id);
                       $("#" + query_select_id).addClass("roundbuttonSelect");

                   } //else*/

                if ($('#' + query_select_id).hasClass("checkmark")) { //deselect
                    $('#' + query_select_id).removeClass("checkmark");
                    $('#' + query_count_id).removeClass("checkmark");

                    $('#' + h_id).val("");
                    $('#' + h_id).attr("placeholder", plc).placeholder();

                } else {

                    $('#' + query_select_id).addClass("checkmark");
                    $('#' + query_count_id).removeClass("checkmark");
                    //dd
                    if (metaphor == "dd") {
                        $('#' + h_id)
                            .append($("<option></option>")
                                .attr("selected", "selected")
                                .attr("value", '?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id)
                                .text('?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id));
                    } else {

                        $('#' + h_id).val('?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id);
                    }
                    result_vars.push('?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id);


                }

            });

            //implement click on the 'Count' for each field
            $(document).on('click', '#' + query_count_id, function() {

                if ($('#' + query_count_id).hasClass("checkmark")) { //deselect
                    $('#' + query_count_id).removeClass("checkmark");
                    $('#' + h_id).val("");
                    $('#' + h_id).attr("placeholder", plc).placeholder();
                } else {
                    $('#' + query_count_id).addClass("checkmark");
                    $('#' + query_select_id).removeClass("checkmark");
                    //dd
                    if (metaphor == "dd") {
                        $('#' + h_id)
                            .append($("<option></option>")
                                .attr("selected", "selected")
                                .attr("value", "cnt ?" + (plc.toLowerCase().replace(/ /g, '')) + instance_id)
                                .text("cnt ?" + (plc.toLowerCase().replace(/ /g, '')) + instance_id));
                    } else {
                        $('#' + h_id).val("cnt ?" + (plc.toLowerCase().replace(/ /g, '')) + instance_id);
                    }
                    count_vars.push('?' + (plc.toLowerCase().replace(/ /g, '')) + instance_id);
                }

            });

            //implement click on the 'filter operations' for each field




        }); //each content	

        //add Done button for each form
        var show_button_id = form_id.replace('f', 'sh');
        var button_id = form_id.replace('f', 'b');
        var clr_button_id = form_id.replace('f', 'c');
        var edit_button_id = form_id.replace('f', 'e');

        var show = $('<input type="button" class="removebutton" value="Show" id="' + show_button_id + '" />');
        //form.append(show);

        $('<button type="button" class="generalbutton submitbutton" disabled="disabled"' + ' id="' + button_id + '"> <span class="glyphicon glyphicon-ok"></span></button>').appendTo(form);
        $('<button type="button" class="generalbutton submitbutton" disabled="disabled"' + ' id="' + clr_button_id + '"> <span class="glyphicon glyphicon-remove"></span></button>').appendTo(form);
        $('<button type="button" class="generalbutton submitbutton" disabled="disabled"' + ' id="' + edit_button_id + '"> <span class="glyphicon glyphicon-edit"></span></button>').appendTo(form);



        /* $('</br><input type="button" class="btn btn-link" value="+" id="' + add_button_id + '" />').appendTo(form);
         $('<select class="formElement" style="width: 80px;" id="' + instance_list_id + '"> <option value="default">Instances</option>').appendTo(form);
         $('</br><input type="button" class="gobutton" value="Done" id="' + button_id + '" />').appendTo(form);
         $('<input type="button" class="removebutton" value="Clear" id="' + clr_button_id + '" />').appendTo(form);*/

        //instace_change the instance number from drop down
        $(document).on('change', "#" + instance_list_id, function() {
            changeForm(instance_list_id, form_id);
            /*  var selected = $("#"+instance_list_id+" option:selected").val();
            var selected_txt = $("#"+instance_list_id+" option:selected").text();
            //alert(JSON.stringify((form_array[form_id][selected_txt])));

            //var obj = form_array[form_id][parseInt(selected)-1][selected_txt];
            var obj = form_array[form_id][selected_txt];

            $.each(obj, function(name, value) {
    			//alert(name + ": " + value);
    			
    			if (value === "on"){
    			$("#"+name).prop('checked', true);}
    			if (value === "off"){
    			$("#"+name).prop('checked', false);}
    			else {
    				$("#"+name).val(value);
    				}
				});*/

        });

        //remove button
        $(document).on('click', '#' + clr_button_id, function() {
            /////
            var selected_txt = $("#" + instance_list_id + " option:selected").text();
            var selected = $("#" + instance_list_id + " option:selected").val();
            var jsn = form_array[form_id][selected_txt];
            var inst_name = selected_txt;
            var inst_triple = "";
            inst_triple = "?" + inst_name + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n';
            $.each(jsn, function(prop, val) {

                var id = $('[name="' + prop + '"]').attr("id");
                if (val !== $("#" + id).attr('placeholder')) {

                    inst_triple = inst_triple + "?" + inst_name + "\t" + prefix + ":" + prop + "\t" + val + '.\n';
                }

                //$('#qpanel').append(inst_triple);
                var qtext = $('#qpanel').text();

                qtext = qtext.replace(inst_triple, "");
                $('#qpanel').text(qtext);

                inst_triple = "";

            });




            ///////

            $("#" + instance_list_id + ' option[value="' + selected + '"]').remove();
            var replaced = $("#" + instance_list_id + ' option:last-child').val();
            $("#" + instance_list_id).val(replaced);
            if (replaced === "default") {
                $('#' + form_id)[0].reset();
            } else {
                changeForm(instance_list_id, form_id);
            }

        }); //remove button

        //edit button
        $(document).on('click', '#' + edit_button_id, function() {
            /////
            var selected_txt = $("#" + instance_list_id + " option:selected").text();
            var selected = $("#" + instance_list_id + " option:selected").val();
            var jsn = form_array[form_id][selected_txt];
            //alert(JSON.stringify(jsn));
            var inst_name = selected_txt;
            var inst_triple = "";
            inst_triple = "?" + inst_name + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n';
            $.each(jsn, function(prop, val) {

                var id = $('[name="' + prop + '"]').attr("id");
                if (val !== $("#" + id).attr('placeholder')) {

                    inst_triple = inst_triple + "?" + inst_name + "\t" + prefix + ":" + prop + "\t" + val + '.\n';
                }

                //$('#qpanel').append(inst_triple);
                var qtext = $('#qpanel').text();

                //alert("qtext "+qtext+"tr "+inst_triple);
                qtext = qtext.replace(inst_triple, "");
                //alert("now :"+qtext);
                $('#qpanel').text(qtext);

                inst_triple = "";

            });




            ///////

            /*    $("#" + instance_list_id + ' option[value="' + selected + '"]').remove();
                var replaced = $("#" + instance_list_id + ' option:last-child').val();
                $("#" + instance_list_id).val(replaced);
                if (replaced === "default") {
                    $('#' + form_id)[0].reset();
                } else {
                    changeForm(instance_list_id, form_id);
                }
                /*var jsn = $("#" + form_id).serializeObject();
                inst_to_jsn[obj_value.title.toLowerCase() + instance_id] = jsn;
                form_array[form_id] = inst_to_jsn;*/


        }); //edit button

        /*         
        //clear button
        $(document).on('click', '#' + clr_button_id, function() {
            $('#' + obj_id).removeClass('wwSelect');
            $('#' + form_id)[0].reset();

            var elements = document.getElementById(form_id).elements;
            for (var i = 0; i < elements.length; i++) {
                if (elements[i].id.contains("s")) {
                    if ($('#' + elements[i].id).hasClass('roundbuttonSelect')) {
                        $('#' + elements[i].id).removeClass('roundbuttonSelect');
                    }
                }
            }
            $('#' + obj_id).popover('hide');

        }); //clear button
*/

        //Add button
        $(document).on('click', '#' + add_button_id, function() {

            instance_id++;
            var ins_var = obj_value.title.toLowerCase(); //class variable
            instance_list.push(ins_var + instance_id);

            $('#' + instance_list_id)
                .append($("<option></option>")
                    .attr("selected", "selected")
                    .attr("value", instance_id)
                    .text(ins_var + instance_id));

            $('#' + form_id)[0].reset();
            $('#' + button_id).attr("disabled", false);
            $('#' + clr_button_id).attr("disabled", false);
            $('#' + edit_button_id).attr("disabled", false);




        });

        //Done button 

        $(document).on('click', '#' + button_id, function() {

            //serialize the form  and store it           
            // $('#' + button_id).attr("disabled", true);
            var jsn = $("#" + form_id).serializeObject();
            inst_to_jsn[obj_value.title.toLowerCase() + instance_id] = jsn;
            form_array[form_id] = inst_to_jsn;
            //alert(JSON.stringify((form_array[form_id])));
            var inst_name = obj_value.title.toLowerCase() + instance_id;
            var inst_triple = "";
            var isAbstract = obj_value.isAbstract;
            //alert (isAbstract);

            inst_triple = "?" + inst_name + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n';

            $.each(jsn, function(prop, val) {

                var id = $('[name="' + prop + '"]').attr("id");
                var plch = $('[name="' + prop + '"]').attr('placeholder');
                //alert(val);
                if (isAbstract === "true") {
                	if (val.indexOf("?") === -1) { //no "?"
                		val = prefix + ":" + val;
                	}
                    inst_triple = "?" + inst_name + "\t" + prop + "\t" +  val + '.\n';
                    isAbstract = "false";
                } else {

                    if (val !== '') {
                        val = val.replace("cnt ?", "?");

                        if (val !== plch) {
                            if (val.indexOf("?") === -1) {
                                var temp_val = '?' + plch.toLowerCase().replace(/ /g, '') + instance_id;
                                ///
                                
                                $.each(filterOps, function(findex, fvalue) {
                                    if (val.indexOf(fvalue) >= 0) {
                                     val = temp_val + '.\nFILTER (xsd:integer(' + temp_val + ')' + val + ')';
                                    // result_vars.push('?' + (plch.toLowerCase().replace(/ /g, '')) + instance_id);


                        }});
                        
                        
                        if(val.indexOf("xsd") === -1){
                        val = temp_val + '.\nFILTER regex(' + temp_val + ',"' + val + '")';
                        }
    
                        }//?

                            inst_triple = inst_triple + "?" + inst_name + "\t" + prefix + ":" + prop + "\t" + val + '.\n';
                        }//plch
    
                    }
                    }
                

                $('#qpanel').append(inst_triple);
                inst_triple = "";

            });
            //alert(JSON.stringify($("#p"+prop).attributes));

            /*		
    				var $inputs = $("#"+form_id+" :input");

         // An array of just the ids...
         			var ids = {};

         $inputs.each(function (index)
         {
             // For debugging purposes...
             if ($(this).attr('id') === prop){
             alert(index + ': ' + $(this).attr('id')+":"+$(this).attr('placeholder'));
             //inst_triple = inst_triple
             }

           //  ids[$(this).attr('name')] = $(this).attr('id');
         });*/



            // change the style of the selected class
            $('#' + obj_id).addClass('wwSelect');
           // $('#' + obj_id).popover('hide');
            /*
                        //get the query variables and values
                        var cls_var = "?" + obj_value.title.toLowerCase() + instance_id; //class variable
                        triple = cls_var + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n';
                        var rdfTriple = "";
                        var pcontents = []; //for each class

                        if (obj_value.contents[0].subclass !== "true") {
                            //rdfTriple = triple.replace("\n", "<br>");
                            rdfTriple = triple;
                            $('#qpanel').append(rdfTriple);
                            pcontents.push(rdfTriple);
                        }

                        $.each(obj_value.contents, function(h_index, h_value) {

                            var h_id = h_value.id;
                            var p_id = h_value.parent;
                            var plc = h_value.placeholder;
                            var pname = prefix + ":" + h_value.name;
                            var metaphor = h_value.metaphor;
                            var subclass = h_value.subclass;
                            var multi = h_value.multi;
                            var query_select_id = 's' + h_id;
                            var v = $('#' + h_id).val();
                            var addXsd = h_value.getInt;


                            if (metaphor === "checkbx") {
                                if ($('#' + h_id).is(":checked")) {
                                    v = "true";
                                } else {
                                    v = "false";
                                }
                            }
                            if (v !== plc && v !== "" && v != "false") {
                                if (metaphor === "dd" && subclass === "true" && multi !== "true") {
                                    triple = cls_var + "\t rdf:type \t" + prefix + ":" + v + '.\n';
                                    triple = triple.replace(cls_var + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n', "");
                                    //var triplehtml = triple.replace(/\n/g,'<br/>');
                                    var triplehtml = triple;
                                    $('#qpanel').append(triplehtml);
                                    pcontents.push(rdfTriple);



                                } else if (multi === "true") {
                                    triple = cls_var + "\t rdf:type \t ?var.\n?var rdfs:subClassOf+ " + prefix + ":" + v + ".\n";
                                    triple = triple.replace(cls_var + "\t rdf:type \t" + prefix + ":" + obj_value.title + '.\n', "");
                                    // var triplehtml = triple.replace(/\n/g,'<br/>');
                                    var triplehtml = triple;
                                    $('#qpanel').append(triplehtml);
                                    pcontents.push(rdfTriple);

                                } else {
                                    if (v.indexOf("?") !== -1) {
                                        //alert(v);
                                        v = v.toLowerCase().replace("count", "");

                                    } else {
                                        filterStm = "";
                                        var tempv = v;
                                        v = "?" + plc.toLowerCase().replace(/ /g, '');

                                        $.each(filterOps, function(findex, fvalue) {
                                            if (tempv.indexOf(fvalue) >= 0) {
                                                //if filterStm = filter+"("+v+tempv+")\n";
                                                if (addXsd == "no") {
                                                    filterStm = filter + "(" + v + tempv + ")\n";
                                                } else {
                                                    filterStm = filter + " (xsd:integer(" + v + ")" + tempv + ")\n";
                                                }
                                                //result_vars.push('?' + (plc.toLowerCase().replace(/ /g, '')));

                                            }
                                        }); //each filter operation
                                        if (filterStm == "") {
                                            tempv = '"' + tempv + '"';
                                            filterStm = filterRegex + " (" + v + "," + tempv + ")\n";
                                        }

                                    } //else
                                    //triple += cls_var + '\t ' + pname + '\t ' + v + '.\n';
                                    if (pname.indexOf("rdfs") >= 0) {
                                        pname = h_value.name;
                                    }
                                    triple = cls_var + '\t ' + pname + '\t ' + v + '.\n';
                                    triple += filterStm;
                                    filterStm = "";
                                    //  var triplehtml = triple.replace(/\n/g,'<br/>');
                                    var triplehtml = triple;

                                    $('#qpanel').append(triplehtml);
                                    pcontents.push(rdfTriple);


                                }


                            } //if 

                        }); //each content	
                        //var pObj= {obj_id: pcontents};
                        obj_value.pcontents = pcontents;
                        //alert(obj_value.pcontents);
            */
        }); //Done button

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
    $.each(objects, function(obj_index, obj_value) {
        var obj_contents = obj_value.contents; //array
        var obj_id = obj_value.id;
        $.each(obj_contents, function(h_index, h_value) {
            var h_id = h_value.id;
            var p_id = h_value.parent;
            var plc = h_value.placeholder;
            var hideOn = h_value.hideOn;
            var metaphor = h_value.metaphor;

            if (p_id != obj_id) {
                $(document).on('change', "#" + p_id, function() {
                    var selected = $(this).val();
                    if (metaphor == "dd") {
                        $('#' + h_id).empty();
                        $('<option />', {
                            value: h_value.placeholder,
                            text: h_value.placeholder
                        }).appendTo($('#' + h_id)); //default
                        //get the list from Json
                        //if (h_value.options[0].indexOf(".json") != -1) {
                        var jsonFile = "../ui/query_ui/json/" + selected.replace(/ /g, '') + ".json";
                        $.getJSON(jsonFile, function(data) {
                            $.each(data.Name, function(key, val) {
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
                            if ($('#' + h_id).hasClass('disabledField')) {
                                $('#' + h_id).removeClass('disabledField');
                                //$('#' + h_id).removeClass('noHover');
                            }


                        } else {
                            $('#' + h_id).prop('disabled', true);
                            $('#' + h_id.replace("p", "s")).prop('disabled', true);
                            //$('#' + h_id.replace("p", "s")).addClass('noHover');
                            //$( '#' + h_id.replace("p","s")).css( "background", "#E0E0E0" );
                            $('#' + h_id).addClass('disabledField');
                            //$('#' + h_id).val(plc);

                        }

                    } //text

                }); //change
            } //if 
        }); //each content
    }); //each object

    $(document).on('click', 'li', function() {


        if ($(this).parent().hasClass('filter')) {
            var prop_id = $(this).parent().attr("id").replace("fl", "p");
            $("#" + prop_id).val($(this).text());
        }


    });

});

function changeForm(form_id, instance_list_id) {
    var selected = $(this).val();
    var selected_txt = $("#" + instance_list_id + " option:selected").text();
    //alert(JSON.stringify((form_array[form_id][selected_txt])));

    //var obj = form_array[form_id][parseInt(selected)-1][selected_txt];
    var obj = form_array[form_id][selected_txt];

    $.each(obj, function(name, value) {
        //alert(name + ": " + value);

        if (value === "on") {
            $("#" + name).prop('checked', true);
        }
        if (value === "off") {
            $("#" + name).prop('checked', false);
        } else {
            $("#" + name).val(value);
        }
    });

}

var changeForm = function(x, y) {

    var selected = $("#" + x + " option:selected").val();
    var selected_txt = $("#" + x + " option:selected").text();
    //alert(JSON.stringify((form_array[form_id][selected_txt])));

    //var obj = form_array[form_id][parseInt(selected)-1][selected_txt];
    var obj = form_array[y][selected_txt];

    $.each(obj, function(name, value) {
        //alert(name + ": " + value);
        var id = $('[name="' + name + '"]').attr("id");

        if (value === "on") {
            $("#" + name).prop('checked', true);
        }
        if (value === "off") {
            $("#" + name).prop('checked', false);
        } else {
            $("#" + id).val(value);
        }
    });



}




function getTriple() {

    return triple;
}

function setTriple(newTriple) {
    triple = newTriple;
    //alert(triple);

}

function httpGet(theUrl) {
    var xmlHttp = null;
    alert(theUrl);
    xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
}

$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};