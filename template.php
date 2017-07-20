<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$sql="SELECT * FROM template";

$res=mysql_query("$sql");
while($row=mysql_fetch_array($res)){
	$result[] = $row;
}
echo json_encode($result);
mysql_free_result($res);
mysql_close();

?>