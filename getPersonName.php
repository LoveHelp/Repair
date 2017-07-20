<?php
header("Content-type: text/html; charset=utf-8");
$personId = $_POST["personId"];
include_once 'conn.php';
$sql = "SELECT personname FROM user WHERE userid = ".$personId;

$res = mysql_query("$sql");
$row = mysql_fetch_row($res,MYSQL_ASSOC);
echo json_encode(array('user'=>$row));

mysql_free_result($res);
mysql_close();

?>