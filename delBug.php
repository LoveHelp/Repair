<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$bugid=$_POST["bugid"];
$query=mysql_query("delete FROM bugrepair WHERE bugid=".$bugid);
if($query)
	echo "1"; //成功		
else 
	echo "0";//失败

mysql_close();

?>
