<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$userid = $_POST["userid"];
$sql="SELECT count(*) FROM bugrepair where state=1 and repairsenduserid = '".$userid."'";
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";		
}
{
	if($row=mysql_fetch_array($res))
	{		
		//echo $row[0];		
		$output["count1"]=$row[0];	
	}		
}
$sql="SELECT count(*) FROM bugrepair where state = 4 and repairsenduserid = '".$userid."'";//专人待核价
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";//没有找到该用户返回1		
}
{
	if($row=mysql_fetch_array($res))
	{			
		$output["count4"]=$row[0];	
	}		
}
$sql="SELECT count(*) FROM bugrepair where state = 6 and repairsenduserid = '".$userid."'";//完工待专人确认
$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
{
	echo "";
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count6"]=$row[0];		
	}		
}


echo json_encode($output); //查询正确返回查询结果
mysql_free_result($res);
mysql_close();

?>