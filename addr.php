<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
//if(empty($_POST["bugtype"])) $_POST["bugtype"]="信号灯";
$sqlname="SELECT * FROM equipmentrecord";
$res=mysql_query("$sqlname");
while($row=mysql_fetch_array($res)){
	$result[] = $row;
}
echo json_encode($result);
mysql_free_result($res);
mysql_close();

?>