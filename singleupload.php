<?php 
header("Content-type: text/html; charset=utf-8");
$patch="./upload/".$_FILES['file']['name'];
if (!empty($_FILES['file']['name'])){
move_uploaded_file($_FILES['file']['tmp_name'],"./upload/".$_FILES['file']['name']);
//echo "bugfindtime:".$_POST["bugfindtime"]."bugfinduserid:".$_POST["bugfinduserid"]."bugtype:".$_POST["bugtype"]."bugfindphoto:".$_POST["bugfindphoto"]."bugfinddescrip:".$_POST["bugfinddescrip"]"bugaddr:".$_POST["bugaddr"];
include_once 'conn.php';
$sql="insert into bugrepair (state,bugtype,bugaddr,bugfindphoto,bugfinddescrip,bugfindtime,bugfinduserid) values (0,'".$_POST["bugtype"]."','".$_POST["bugaddr"]."','".$_FILES['file']['name']."','".$_POST["bugfinddescrip"]."','".$_POST["bugfindtime"]."','".$_POST["bugfinduserid"]."')";
$q=mysql_query($sql);
echo json_encode("上报成功");
/* $q=mysql_query("SELECT * FROM user WHERE username='".$_REQUEST['username']."'");
while($e=mysql_fetch_assoc($q))
        //$output[]=$e; 
print(json_encode($output));  */
mysql_close();
}else{echo "";}
?>