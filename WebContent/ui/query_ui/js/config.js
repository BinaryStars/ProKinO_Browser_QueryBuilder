

<!DOCTYPE html>
<html>
<body>

<h1>JavaScript Can Change Images</h1>



<script>
var cls_list = ["Gene"];
for(var cls in cls_list) {
	var obj = new Object();
	var dp_list = ["hasPrimaryName","hasLocation"];
	obj.id = "window"+cls;
	obj.title = cls_list[cls];
	var contents = [];

	for(var dp in dp_list) {
		var content = new Object();
		content.id = "p"+obj_cnt+dp;

		content.placeholder = dp_list[dp];
		content.name = dp_list[dp];
		content.metaphor = "dd";
		content.json=content.id;
		contents[dp] = content;
	}
	obj.contents=contents;
	var myString = JSON.stringify(obj);
	alert("* "+myString);
}
</script>

</body>
</html>
