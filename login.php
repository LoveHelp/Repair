<?php
header("Content-type: text/html; charset=utf-8");
$username=urldecode($_POST["username"]);
$password=urldecode($_POST["userpass"]);
include_once 'conn.php';
$sqlname="SELECT * FROM user where username='".$username."'";
$sqllogin="SELECT * FROM user where username='".$username."' and password='".md5($password)."'";

$res=mysql_query("$sqlname");
if(mysql_num_rows($res)<1)
	{
		echo "1";//没有找到该用户返回1		
	}
	else
	{		
	$res=mysql_query("$sqllogin");
	if(mysql_num_rows($res)<1)
	{
		echo "2"; //密码错误返回2
	}
	else
	{
		while($row=mysql_fetch_array($res))
		{		
		echo json_encode($row); //查询正确返回查询结果
		}
	}
	}
	mysql_free_result($res);
	mysql_close();

?>