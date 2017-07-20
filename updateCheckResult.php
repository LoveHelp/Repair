<?php
header("Content-type: text/html; charset=utf-8");
$bugid = $_POST["bugid"];
$state = $_POST["state"];

$checkeuserid = $_POST["checkeuserid"];
include_once 'conn.php';
$repairsendtime = date("Y-m-d H:i:s");
if($state == 5){
	$checkcharge = 1;
	$sql="UPDATE bugrepair SET checktime ='" . $repairsendtime . "', checkeuserid=" . $checkeuserid . ", state=" . $state . ", checkcharge =" . $checkcharge . " where bugid = " . $bugid;
	
}else if($state == 41){
	$checkcharge = 0;
	$sql="UPDATE bugrepair SET checktime_zr ='" . $repairsendtime . "', checkeuserid=" . $checkeuserid . ", state=" . $state . ", checkcharge =" . $checkcharge . " where bugid = " . $bugid;
}else{//state=3//核价不同意后驳回
	$checkcharge = 0;
	$s="UPDATE bugrepair SET checktime ='" . $repairsendtime . "', checkeuserid=" . $checkeuserid . ", state=" . $state . ",bigcharge=0, checkcharge =" . $checkcharge . " where bugid = " . $bugid;
	$r=mysql_query("$s");
	if($r){
		$sql="delete from offer where bugid = ".$bugid;
	}
}

if($sql){
	$res=mysql_query("$sql");
	if($res){
		echo "success"; 
	}else{
		echo "fail";
	}
}else{
	echo "fail";
}



//mysql_free_result($res);
mysql_close();

?>