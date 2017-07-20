<?php
header("Content-type: text/html; charset=utf-8");
$userid = $_POST["userid"];
include_once 'conn.php';
$sql="SELECT personname FROM user WHERE userid=" . $userid;

$res=mysql_query("$sql");
$row = mysql_fetch_row($res,MYSQL_ASSOC);

echo json_encode($row);

mysql_free_result($res);
mysql_close();

?>