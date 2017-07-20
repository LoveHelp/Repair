<?php
header("Content-type: text/html; charset=utf-8");

include_once 'conn.php';

$photo="";
if(!empty($_FILES["file"])){
	$roleid=isset($_POST["roleid"])?$_POST["roleid"]:"0";
	foreach ($_FILES["file"]["error"] as $key => $error) {  
	    $tmp_name = $_FILES["file"]["tmp_name"][$key];  
	    $name    = $_FILES["file"]["name"][$key];
	    $array=explode(".",$name);
	    $houzhui=".".$array[count($array)-1];
	    $time_now=date('YmdHis',time());
	    $new_name=$roleid.$time_now.$key.$houzhui;
	    $photo.=$new_name.",";
	    $patch="./upload/".$new_name; 
		move_uploaded_file($tmp_name, iconv("UTF-8", "gb2312", $patch));
	 } 
}
if($photo!=""){
	$photo=substr($photo,0,strlen($photo)-1);
}

$bugfindphoto=isset($_POST["bugfindphoto"])?urldecode($_POST["bugfindphoto"]):"";

$bugid=$_POST["bugid"];
$bugaddr=urldecode($_POST["bugaddr"]);
$bugfinddescrip=urldecode($_POST["bugfinddescrip"]);

if($bugfindphoto!=""){
	if($photo==""){
		$photo=$bugfindphoto;
	}else{
		$photo.=",".$bugfindphoto;
	}
}

$update="update bugrepair set bugaddr='".$bugaddr."',bugfinddescrip='".$bugfinddescrip."'";
$update.=",bugfindphoto='".$photo."' WHERE bugid=".$bugid;

$query=mysql_query($update); 

$result = "0";
if($query){
	$result = "1";
}	

echo $result;//$result;
mysql_close();

?>
