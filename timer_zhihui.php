<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
$userid = $_POST["userid"];
//查询返回没有分派的故障
$sql="SELECT count(*) FROM bugrepair where state=0";
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count0"]=$row[0];		
	}		
}
$sql="SELECT count(*) FROM bugrepair where state=41 and bugsenduserid = '".$userid."'";
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count41"]=$row[0];		
	}		
}
$sql="SELECT count(*) FROM bugrepair where state=61 and bugsenduserid = '".$userid."'";
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count61"]=$row[0];		
	}		
}

echo json_encode($output); //查询正确返回查询结果
mysql_free_result($res);
mysql_close();

?>