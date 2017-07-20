<?php
header("Content-type: text/html; charset=utf-8");

include_once 'conn.php';

//$roleid=isset($_POST["roleid"])?$_POST["roleid"]:"0";

if(!empty($_FILES["file"])){
	foreach ($_FILES["file"]["error"] as $key => $error) {  
	    $tmp_name = $_FILES["file"]["tmp_name"][$key];   
	    $name    = $_FILES["file"]["name"][$key];
	    $patch="./upload/".$name; 
		move_uploaded_file($tmp_name, iconv("UTF-8", "gb2312", $patch));
	 } 
}

$repaircheckphoto=isset($_POST["repaircheckphoto"])?urldecode($_POST["repaircheckphoto"]):"";
$completephoto=isset($_POST["completephoto"])?urldecode($_POST["completephoto"]):"";
$repaircheckphoto_old=isset($_POST["repaircheckphoto_old"])?urldecode($_POST["repaircheckphoto_old"]):"";
$completephoto_old=isset($_POST["completephoto_old"])?urldecode($_POST["completephoto_old"]):"";

$bugid=$_POST["bugid"];
$repairdescrip=urldecode($_POST["repairdescrip"]);
$repairreason=urldecode($_POST["repairreason"]);
$repairsolution=urldecode($_POST["repairsolution"]);
$bigcharge=urldecode($_POST["bigcharge"]);
$sqllist=urldecode($_POST["sqllist"]);
$completedescrip="";
if(!empty($_POST["completedes"])){
	$completedescrip=urldecode($_POST["completedes"]);
}
//新增：专人可以修改是否在保、是否有基本费用
$charge=0;
$isonrepair=urldecode($_POST["isonrepair"]);
$chargeflag=urldecode($_POST["chargeflag"]);
if($chargeflag=="1"){
	$charge=180;
}


$update="update bugrepair set repairdescrip='".$repairdescrip."',repairreason='".$repairreason."',repairsolution='".$repairsolution."',charge=".$charge.",isonrepair=".$isonrepair;
if(!empty($sqllist)){
	$update.=",bigcharge=".$bigcharge;
}

if($repaircheckphoto_old!=""){
	if($repaircheckphoto==""){
		$repaircheckphoto=$repaircheckphoto_old;
	}else{
		$repaircheckphoto.=",".$repaircheckphoto_old;
	}
}
$update.=",repaircheckphoto='".$repaircheckphoto."'";

if($completephoto_old!=""){
	if($completephoto==""){
		$completephoto=$completephoto_old;
	}else{
		$completephoto.=",".$completephoto_old;
	}
}
$update.=",completephoto='".$completephoto."'";

$update.=",completedescrip='".$completedescrip."' WHERE bugid=".$bugid;
$sql_del="delete from offer where bugid = ".$bugid;//根据bugid删除offer

$query=mysql_query($update); 

$result = "0";
if($query && !empty($sqllist)){
	$query1=mysql_query($sql_del); 
	$query2=mysql_query($sqllist); //向offer添加数据
	if($query2){
		$result = "1"; //	
	}
}else if($query){
	$result = "1";
}	
else {
	$result = "0";
}
echo $result;
mysql_close();

?>
