<?php
header("Content-type: text/html; charset=utf-8");
//$userid=$_POST["userid"];
include_once 'conn.php';
$roleid = $_POST["roleid"];
if(isset($roleid) && $roleid=="3"){
	$sql="SELECT a.bugid,a.bugtype,a.bugaddr,a.bugfindphoto,a.bugfinddescrip,a.bugfindtime,b.personname,a.completetime,a.state FROM bugrepair as a LEFT JOIN user as b ON a.bugfinduserid=b.userid  where state=6";
}else{
	$sql="SELECT a.bugid,a.bugtype,a.bugaddr,a.bugfindphoto,a.bugfinddescrip,a.bugfindtime,b.personname,a.compeletchecktime_zr as completetime,a.state FROM bugrepair as a LEFT JOIN user as b ON a.bugfinduserid=b.userid  where state=61";
}
$sql.=" order by completetime desc";

$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
	{
		echo "";//没有找到该用户返回1		
	}
else{
		while($row=mysql_fetch_array($res))
		{		
		$output[]=$row;		
		}	
		echo json_encode($output); //查询正确返回查询结果
	}	
	mysql_free_result($res);
	mysql_close();

?>