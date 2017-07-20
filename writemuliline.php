<?php
header("Content-type: text/html; charset=utf-8");
$bugid=$_POST["bugid"];
include_once 'conn.php';
//查询返回没有分派的故障
$sql='insert into equipmentrecord (equipmenttype,equipmentname,equipmentaddr) VALUES("信号灯","红绿灯","张衡路与仲景路交叉口"),("信号灯","红绿灯","张衡路与仲景路交叉口"),("信号灯","红绿灯","张衡路与仲景路交叉口")';
//echo $sql;
$res=mysql_query("$sql");
if(mysql_query("$sql"))
	{
		echo "确认完毕";//没有找到该用户返回1		
	}else
	{
	echo "";//没有找到该用户返回1	
	}

	mysql_free_result($res);
	mysql_close();

?>