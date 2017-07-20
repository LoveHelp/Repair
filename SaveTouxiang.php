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
    $patch="./upload/touxiang/".$new_name; 
    move_uploaded_file($tmp_name, $patch);
 } 
}
if($photo!=""){
	$photo=substr($photo,0,strlen($photo)-1);
}

include_once 'conn.php';

$userid = isset($_POST["userid"])?$_POST["userid"]:"0";
$sql="update user set touxiang='".$photo."' where userid = '".$userid."'";
//echo $sql;
$query=mysql_query($sql); 
if($query)
	echo "1"; //成功	
else 
	echo "0";//失败

mysql_close();

?>
