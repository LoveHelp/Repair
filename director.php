<?php
header("Content-type: text/html; charset=utf-8");
//$userid=$_POST["userid"];
include_once 'conn.php';
//查询返回没有分派的故障
$sql="SELECT a.bugid,a.bugtype,a.bugaddr,a.bugfindphoto,a.bugfinddescrip,a.bugfindtime,a.state,b.personname FROM bugrepair as a LEFT JOIN user as b ON a.bugfinduserid=b.userid  where state=0";
$sql.=" order by a.bugfindtime desc";

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