<?php
header("Content-type: text/html; charset=utf-8");

include_once 'conn.php';

$userid = isset($_POST["userid"])?$_POST["userid"]:"0";
$newPwd = isset($_POST["newPwd"])?$_POST["newPwd"]:"";

$result = "0";
if($newPwd != ""){
	$update = "update user set password = '".md5($newPwd)."' where userid = ".$userid;
	$query=mysql_query($update); 
	if($query)
		$result = "1";
}

echo $result;

mysql_close();

?>
