<?php 
header("Content-type: text/html; charset=utf-8");

$photo="";
if(!empty($_FILES["file"])){
	$roleid=isset($_POST["roleid"])?$_POST["roleid"]:"0";
	foreach ($_FILES["file"]["error"] as $key => $error) {  
    $tmp_name = $_FILES["file"]["tmp_name"][$key];  
    $name    = $_FILES["file"]["name"][$key];
    //$patch="./upload/".str_replace("ys_","",$name); 
    $array=explode(".",$name);
    $houzhui=".".$array[count($array)-1];
    $time_now=date('YmdHis',time());
    $new_name=$roleid.$time_now.$key.$houzhui;
    $photo.=$new_name.",";
    $patch="./upload/".$new_name; 
    move_uploaded_file($tmp_name, $patch);
 } 
}
if($photo!=""){
	$photo=substr($photo,0,strlen($photo)-1);
}

//if(!empty($_FILES["file"]))
//{
//foreach ($_FILES["file"]["error"] as $key => $error) {  
//  $tmp_name = $_FILES["file"]["tmp_name"][$key];  
//  $name    = $_FILES["file"]["name"][$key];
//  $path="./upload/".$name; 
//  $path_ls="./upload/ls_".$name; 
//  move_uploaded_file($tmp_name, $path_ls);
//  include_once 'zipImage.php';
//  resizeImage(imagecreatefromjpeg($path_ls),"800","600",$path);
//} 
//}

include_once 'conn.php';
$sql="insert into bugrepair (state,bugtype,bugaddr,bugfindphoto,bugfinddescrip,bugfindtime,bugfinduserid) values (0,'".$_POST["bugtype"]."','".$_POST["bugaddr"]."','".$photo."','".$_POST["bugfinddescrip"]."',now(),'".$_POST["bugfinduserid"]."')";
$res=mysql_query($sql);

if($res){
	echo "success"; 
}else{
	echo "fail";
}

mysql_close();
?>