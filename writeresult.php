<?php
header("Content-type: text/html; charset=utf-8");
$bugid=$_POST["bugid"];
$repairsenduserid=$_POST["receiveruserid"];
$bugsenduserid=$_POST["bugsenduserid"];
include_once 'conn.php';
//查询返回没有分派的故障
$sql="UPDATE bugrepair SET bugsendtime=now(),repairsenduserid=$repairsenduserid,bugsenduserid=$bugsenduserid,state=1 WHERE bugid=$bugid";
//echo $sql;
$res=mysql_query("$sql");
if($res)
	{
		echo "分派成功";//没有找到该用户返回1		
	}else
	{
	echo "";//没有找到该用户返回1	
	}

	mysql_close();

?>