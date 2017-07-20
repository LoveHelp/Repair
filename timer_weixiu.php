<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$repairuserid = $_POST["repairuserid"];
$where=" and repairuserid REGEXP '^".$repairuserid."$|^".$repairuserid.",|,".$repairuserid.",|,".$repairuserid."$'";
$sql="SELECT count(*) FROM bugrepair where state = 2".$where;
$res=mysql_query($sql);

if(mysql_num_rows($res)<1){
	echo "";		
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count2"] = $row[0];		
	}		
}

$sql="SELECT count(*) FROM bugrepair where state = 3".$where;
$res=mysql_query($sql);

if(mysql_num_rows($res)<1){
	echo "";		
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count3"] = $row[0];		
	}		
}

$sql="SELECT count(*) FROM bugrepair where state = 5".$where;
$res=mysql_query($sql);

if(mysql_num_rows($res)<1){
	echo "";//没有找到		
}else
{
	if($row=mysql_fetch_array($res))
	{		
		$output["count5"] = $row[0];		
	}		
}

echo json_encode($output); //查询正确返回查询结果
mysql_free_result($res);
mysql_close();

?>