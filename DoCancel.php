<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
$bugid = isset($_POST["bugid"])?$_POST["bugid"]:"0";
$sqlname="update bugrepair set repairuserid = '',state = 1 where bugid = ".$bugid;
//echo $sqlname;
$res=mysql_query("$sqlname");
if($res)
	echo "1"; //成功	
else 
	echo "0";//失败

mysql_close();

?>