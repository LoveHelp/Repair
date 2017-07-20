<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
$sqlname="SELECT userid as id, personname as name,areaid FROM user where roleid = 3";

$res=mysql_query("$sqlname");
while($row=mysql_fetch_array($res)){
	$result[] = $row;
}

echo json_encode($result);

mysql_free_result($res);
mysql_close();

?>