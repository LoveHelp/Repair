<?php
header("Content-type: text/html; charset=utf-8");
$bugid=$_POST["bugid"];
include_once 'conn.php';
$roleid = $_POST["roleid"];
if(isset($roleid) && $roleid=="3"){
	$sql="UPDATE bugrepair SET compeletchecktime_zr=now(),state=61 WHERE bugid=$bugid";
}else{
	$sql="UPDATE bugrepair SET compeletchecktime=now(),state=7 WHERE bugid=$bugid";
}

//echo $sql;
$res=mysql_query("$sql");
if($res)
	{
		echo "1";		
	}else
	{
	echo "";	
	}

	//mysql_free_result($res);
	mysql_close();

?>