<?php
header("Content-type: text/html; charset=utf-8");
$bugid = $_POST["bugid"];
$repairuserid = $_POST["repairuserid"];
include_once 'conn.php';
$repairsendtime = date("Y-m-d H:i:s");
$sql="UPDATE bugrepair SET repairuserid = '" .$repairuserid . "', repairsendtime ='" . $repairsendtime . "', state=2 where bugid = " . $bugid;

$res=mysql_query("$sql");
if($res){
	echo "success"; 
}else{
	echo "fail";
}

mysql_close();

?>