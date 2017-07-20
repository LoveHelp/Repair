<?php
header("Content-type: text/html; charset=utf-8");
//$userid=$_POST["userid"];
include_once 'conn.php';
//查询返回没有分派的故障
$sql="SELECT userid,personname from user where roleid='3'";

$res=mysql_query("$sql");
if(mysql_num_rows($res)<1)
	{
		echo "查询失败";//没有找到该用户返回1		
	}
	{
		while($row=mysql_fetch_array($res))
		{		
		$output[]=$row;		
		}		
	}
	echo json_encode($output); //查询正确返回查询结果
	mysql_free_result($res);
	mysql_close();

?>