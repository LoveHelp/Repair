<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
$bugid = isset($_POST["bugid"])?$_POST["bugid"]:"0";
$sqlname="SELECT personname FROM user where userid in (select repairuserid from bugrepair where bugid = ".$bugid.")";

$res=mysql_query("$sqlname");
while($row=mysql_fetch_array($res)){
	$result[] = $row;
}

echo json_encode($result);

mysql_free_result($res);
mysql_close();

?>